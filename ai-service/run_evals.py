import argparse
import asyncio
import json
import time
from pathlib import Path

from pydantic import BaseModel, Field

from app.core.config import Settings
from app.models.schemas import LessonPlanRequest, WorkflowStatus
from app.providers.demo import DemoProvider
from app.rag.chunking import chunk_document
from app.rag.store import HybridStore
from app.services.workflow import LessonWorkflow


class EvaluationDocument(BaseModel):
    document_id: str
    document_name: str
    text: str
    knowledge_base_id: str = "evaluation"


class EndToEndCase(BaseModel):
    case_id: str
    request: LessonPlanRequest
    documents: list[EvaluationDocument] = Field(default_factory=list)
    expected_document_ids: list[str] = Field(default_factory=list)
    expect_refusal: bool = False


async def evaluate(case: EndToEndCase) -> dict:
    provider = DemoProvider()
    store = HybridStore()
    for document in case.documents:
        chunks = chunk_document(document_id=document.document_id, document_name=document.document_name, text=document.text, metadata={})
        vectors = await provider.embed([chunk.text for chunk in chunks])
        await store.upsert(chunks=chunks, vectors=vectors, user_id=case.request.user_id, knowledge_base_id=document.knowledge_base_id)
    started = time.perf_counter()
    workflow = await LessonWorkflow(provider, store, Settings(demo_mode=True, retrieval_min_score=0.15)).run(case.request)
    latency_ms = (time.perf_counter() - started) * 1000
    refused = workflow.status == WorkflowStatus.failed
    citations = workflow.lesson_plan.citations if workflow.lesson_plan else []
    cited_ids = {citation.document_id for citation in citations}
    expected_ids = set(case.expected_document_ids)
    sections = workflow.lesson_plan.sections if workflow.lesson_plan else []
    cited_sections = sum(bool(section.citations) for section in sections)
    return {
        "case_id": case.case_id,
        "passed": refused == case.expect_refusal and (case.expect_refusal or not expected_ids or bool(cited_ids & expected_ids)),
        "retrieval_hit": bool(cited_ids & expected_ids) if expected_ids else False,
        "refusal_correct": refused == case.expect_refusal,
        "citation_coverage": cited_sections / len(sections) if sections else 0.0,
        "latency_ms": round(latency_ms, 2),
        "status": workflow.status.value,
    }


async def run(dataset: Path) -> dict:
    cases = [EndToEndCase.model_validate_json(line) for line in dataset.read_text(encoding="utf-8").splitlines() if line.strip()]
    results = [await evaluate(case) for case in cases]
    latencies = sorted(item["latency_ms"] for item in results)
    retrieval_results = [result for result, case in zip(results, cases, strict=True) if case.expected_document_ids]
    return {
        "evaluation_mode": "end_to_end_demo_provider",
        "cases": len(results),
        "pass_rate": sum(item["passed"] for item in results) / len(results) if results else 0.0,
        "retrieval_hit_rate": sum(item["retrieval_hit"] for item in retrieval_results) / len(retrieval_results) if retrieval_results else 0.0,
        "refusal_accuracy": sum(item["refusal_correct"] for item in results) / len(results) if results else 0.0,
        "mean_section_citation_coverage": sum(item["citation_coverage"] for item in results) / len(results) if results else 0.0,
        "p95_latency_ms": latencies[max(0, int(len(latencies) * 0.95) - 1)] if latencies else 0.0,
        "results": results,
    }


def main() -> None:
    parser = argparse.ArgumentParser(description="Run the complete ingest, retrieve, generate and validate workflow")
    parser.add_argument("dataset", type=Path)
    args = parser.parse_args()
    print(json.dumps(asyncio.run(run(args.dataset)), ensure_ascii=False, indent=2))


if __name__ == "__main__":
    main()
