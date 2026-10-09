import pytest

from app.rag.chunking import Chunk
from app.rag.store import HybridStore


@pytest.mark.asyncio
async def test_store_enforces_owner_and_knowledge_base_boundaries():
    store = HybridStore()
    chunk = Chunk("doc-1:0", "doc-1", "教材", "一次函数的基本概念")
    await store.upsert(chunks=[chunk], vectors=[[1.0, 0.0]], user_id="1", knowledge_base_id="kb-1")
    own = await store.search(query="函数", query_vector=[1.0, 0.0], user_id="1", knowledge_base_ids=["kb-1"], top_k=3)
    other_user = await store.search(query="函数", query_vector=[1.0, 0.0], user_id="2", knowledge_base_ids=["kb-1"], top_k=3)
    assert len(own) == 1
    assert other_user == []


@pytest.mark.asyncio
async def test_document_deduplication_listing_and_deletion():
    store = HybridStore()
    chunk = Chunk("doc-1:0", "doc-1", "教材", "一次函数的基本概念", {"content_hash": "abc"})
    await store.upsert(chunks=[chunk], vectors=[[1.0, 0.0]], user_id="1", knowledge_base_id="kb-1")

    assert await store.find_document_by_hash(user_id="1", knowledge_base_id="kb-1", content_hash="abc") == ("doc-1", 1)
    documents = await store.list_documents(user_id="1", knowledge_base_id="kb-1")
    assert documents[0]["document_id"] == "doc-1"
    assert await store.delete_document(document_id="doc-1", user_id="1") is True
    assert await store.list_documents(user_id="1", knowledge_base_id="kb-1") == []


@pytest.mark.asyncio
async def test_workflow_status_is_isolated_by_owner():
    store = HybridStore()
    await store.save_workflow(run={"run_id": "run-1", "status": "PENDING", "current_step": "QUEUED"}, owner_id="1", workflow_input={})

    assert (await store.get_workflow(run_id="run-1", user_id="1"))["status"] == "PENDING"
    assert await store.get_workflow(run_id="run-1", user_id="2") is None
