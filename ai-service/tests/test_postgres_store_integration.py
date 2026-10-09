import asyncio
import os
from uuid import uuid4

import pytest

from app.rag.chunking import Chunk
from app.rag.postgres_store import PostgresHybridStore


def test_postgres_document_and_workflow_lifecycle():
    database_url = os.getenv("TEST_LESSON_DATABASE_URL")
    if not database_url:
        pytest.skip("set TEST_LESSON_DATABASE_URL to run PostgreSQL integration")

    async def scenario():
        store = PostgresHybridStore(database_url)
        await store.connect()
        owner = "900001"
        document_id = str(uuid4())
        run_id = str(uuid4())
        try:
            chunk = Chunk(f"{document_id}:0", document_id, "测试教材", "一次函数图像是一条直线", {"content_hash": uuid4().hex + uuid4().hex})
            await store.upsert(chunks=[chunk], vectors=[[1.0] + [0.0] * 1023], user_id=owner, knowledge_base_id="integration")
            assert len(await store.get_document_chunks(document_id=document_id, user_id=owner)) == 1
            await store.update_vectors(chunks=[chunk], vectors=[[0.0, 1.0] + [0.0] * 1022], user_id=owner)
            run = {"run_id": run_id, "status": "PENDING", "current_step": "QUEUED", "stage_latency_ms": {"total": 2}, "model_usage": {"input_tokens": 1, "output_tokens": 2, "embedding_tokens": 3, "estimated_cost": 0.01}, "fallback_reasons": []}
            await store.save_workflow(run=run, owner_id=owner, workflow_input={})
            saved = await store.get_workflow(run_id=run_id, user_id=owner)
            assert saved["stage_latency_ms"]["total"] == 2
            assert saved["model_usage"]["embedding_tokens"] == 3
        finally:
            await store.delete_document(document_id=document_id, user_id=owner)
            await store.close()

    asyncio.run(scenario())
