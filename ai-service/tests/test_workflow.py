import pytest

from app.core.config import Settings
from app.models.schemas import LessonPlanRequest, WorkflowStatus
from app.providers.base import ModelProvider
from app.rag.chunking import Chunk
from app.rag.store import HybridStore
from app.services.workflow import LessonWorkflow


class FakeProvider(ModelProvider):
    async def generate_json(self, *, messages, schema, model=None):
        return {
            "title": "一次函数教案",
            "subject": "数学",
            "grade": "八年级",
            "duration_minutes": 45,
            "objectives": ["理解一次函数"],
            "key_points": ["函数概念"],
            "difficulties": ["图像理解"],
            "sections": [{"title": "导入", "content": "复习变量", "citations": ["doc-1:0", "invented"]}],
            "homework": ["完成练习"],
        }

    async def stream_text(self, *, messages, model=None):
        if False:
            yield ""

    async def embed(self, texts):
        return [[1.0, 0.0] for _ in texts]


def request():
    return LessonPlanRequest(user_id="1", subject="数学", grade="八年级", topic="一次函数")


@pytest.mark.asyncio
async def test_workflow_refuses_ungrounded_generation():
    run = await LessonWorkflow(FakeProvider(), HybridStore(), Settings()).run(request())
    assert run.status == WorkflowStatus.failed
    assert "教学资料" in run.error


@pytest.mark.asyncio
async def test_workflow_removes_model_invented_citations():
    store = HybridStore()
    chunk = Chunk("doc-1:0", "doc-1", "教材", "一次函数表示变量之间的线性关系")
    await store.upsert(chunks=[chunk], vectors=[[1.0, 0.0]], user_id="1", knowledge_base_id="default")
    run = await LessonWorkflow(FakeProvider(), store, Settings(retrieval_min_score=0.0)).run(request())
    assert run.status == WorkflowStatus.waiting_for_review
    assert run.lesson_plan.sections[0].citations == ["doc-1:0"]
    assert run.lesson_plan.quality_warnings
