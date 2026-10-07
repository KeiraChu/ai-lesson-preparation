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

