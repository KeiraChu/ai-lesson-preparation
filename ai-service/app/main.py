import logging
import time
from contextlib import asynccontextmanager
from uuid import uuid4

from fastapi import Depends, FastAPI, File, Form, Header, HTTPException, UploadFile
from prometheus_client import Counter, Histogram, make_asgi_app

from app.core.config import Settings, get_settings
from app.models.schemas import EvaluationCase, EvaluationResult, IngestRequest, IngestResponse, LessonPlanRequest, WorkflowRun, WorkflowStatus
from app.providers.base import ModelProvider
from app.providers.demo import DemoProvider
from app.providers.openai_compatible import OpenAICompatibleProvider
from app.rag.chunking import chunk_document
from app.rag.parsing import extract_text
from app.rag.store import HybridStore
from app.rag.postgres_store import PostgresHybridStore
from app.services.evaluation import evaluate_case
from app.services.workflow import LessonWorkflow

logging.basicConfig(level=logging.INFO, format="%(asctime)s %(levelname)s %(name)s %(message)s")
logger = logging.getLogger("ai-service")
REQUEST_COUNT = Counter("ai_requests_total", "AI service requests", ["endpoint", "status"])
REQUEST_LATENCY = Histogram("ai_request_latency_seconds", "AI service request latency", ["endpoint"])

store: HybridStore | PostgresHybridStore = HybridStore()
model_provider: ModelProvider | None = None


@asynccontextmanager
async def lifespan(_: FastAPI):
    global store, model_provider
    settings = get_settings()
    if settings.demo_mode:
        model_provider = DemoProvider()
    elif settings.api_key:
        model_provider = OpenAICompatibleProvider(settings)
    if settings.rag_backend.lower() == "postgres":
        persistent_store = PostgresHybridStore(settings.database_url)
        await persistent_store.connect()
        store = persistent_store
    yield
    if isinstance(store, PostgresHybridStore):
        await store.close()
    if isinstance(model_provider, OpenAICompatibleProvider):
        await model_provider.close()
    model_provider = None


app = FastAPI(title="AI Lesson Preparation Service", version="1.0.0", lifespan=lifespan)
app.mount("/metrics", make_asgi_app())


def require_internal_key(
    x_internal_api_key: str = Header(default=""), settings: Settings = Depends(get_settings)
) -> None:
    if settings.environment != "development" and x_internal_api_key != settings.internal_api_key:
        raise HTTPException(status_code=401, detail="invalid internal API key")


def provider(settings: Settings = Depends(get_settings)) -> ModelProvider:
    global model_provider
    if settings.demo_mode:
        if not isinstance(model_provider, DemoProvider):
            model_provider = DemoProvider()
        return model_provider
    if not settings.api_key:
        raise HTTPException(status_code=503, detail="AI_API_KEY is not configured")
    if model_provider is None:
        model_provider = OpenAICompatibleProvider(settings)
    return model_provider


@app.get("/health")
async def health() -> dict:
    settings = get_settings()
    return {"status": "ok", "service": settings.service_name, "demo_mode": settings.demo_mode, "model_enabled": bool(settings.api_key)}


@app.post("/v1/knowledge/documents", response_model=IngestResponse, dependencies=[Depends(require_internal_key)])
async def ingest(request: IngestRequest, model: ModelProvider = Depends(provider)) -> IngestResponse:
    metadata = {
        key: value
        for key, value in {
            "subject": request.subject,
            "grade": request.grade,
            "textbook_version": request.textbook_version,
        }.items()
        if value
    }
    chunks = chunk_document(
        document_id=request.document_id,
        document_name=request.document_name,
        text=request.text,
        metadata=metadata,
    )
    vectors = await model.embed([chunk.text for chunk in chunks])
    await store.upsert(
        chunks=chunks,
        vectors=vectors,
        user_id=request.user_id,
        knowledge_base_id=request.knowledge_base_id,
    )
    return IngestResponse(document_id=request.document_id, chunk_count=len(chunks))


@app.post("/v1/knowledge/files", response_model=IngestResponse, dependencies=[Depends(require_internal_key)])
async def ingest_file(
    user_id: str = Form(...),
    knowledge_base_id: str = Form(...),
    file: UploadFile = File(...),
    model: ModelProvider = Depends(provider),
) -> IngestResponse:
    content = await file.read()
    if len(content) > 20 * 1024 * 1024:
        raise HTTPException(status_code=413, detail="file exceeds 20MB")
    try:
        text = extract_text(file.filename or "document.txt", content)
    except ValueError as exc:
        raise HTTPException(status_code=415, detail=str(exc)) from exc
    if not text.strip():
        raise HTTPException(status_code=422, detail="document contains no extractable text")
    document_id = str(uuid4())
    chunks = chunk_document(
        document_id=document_id,
        document_name=file.filename or document_id,
        text=text,
        metadata={},
    )
    vectors = await model.embed([chunk.text for chunk in chunks])
    await store.upsert(chunks=chunks, vectors=vectors, user_id=user_id, knowledge_base_id=knowledge_base_id)
    return IngestResponse(document_id=document_id, chunk_count=len(chunks))


@app.post("/v1/workflows/lesson-plan", response_model=WorkflowRun, dependencies=[Depends(require_internal_key)])
async def lesson_plan(
    request: LessonPlanRequest,
    settings: Settings = Depends(get_settings),
    model: ModelProvider = Depends(provider),
) -> WorkflowRun:
    started = time.perf_counter()
    run = await LessonWorkflow(model, store, settings).run(request)
    status = "failed" if run.status == WorkflowStatus.failed else "success"
    REQUEST_COUNT.labels("lesson_plan", status).inc()
    REQUEST_LATENCY.labels("lesson_plan").observe(time.perf_counter() - started)
    logger.info("workflow_complete run_id=%s status=%s", run.run_id, run.status)
    return run


@app.post("/v1/evaluations/case", response_model=EvaluationResult, dependencies=[Depends(require_internal_key)])
async def evaluate(case: EvaluationCase) -> EvaluationResult:
    return evaluate_case(case)
