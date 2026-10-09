import math
import re
from collections import Counter, defaultdict

from app.rag.chunking import Chunk


class HybridStore:
    """Development store with lexical + vector ranking; replaceable by pgvector in production."""

    def __init__(self) -> None:
        self._chunks: dict[str, Chunk] = {}
        self._vectors: dict[str, list[float]] = {}
        self._owners: dict[str, str] = {}
        self._knowledge_bases: dict[str, str] = {}
        self._workflow_runs: dict[str, dict] = {}

    async def upsert(self, *, chunks: list[Chunk], vectors: list[list[float]], user_id: str, knowledge_base_id: str) -> None:
        for chunk, vector in zip(chunks, vectors, strict=True):
            self._chunks[chunk.chunk_id] = chunk
            self._vectors[chunk.chunk_id] = vector
            self._owners[chunk.chunk_id] = user_id
            self._knowledge_bases[chunk.chunk_id] = knowledge_base_id

    async def search(self, *, query: str, query_vector: list[float], user_id: str, knowledge_base_ids: list[str], top_k: int) -> list[tuple[Chunk, float]]:
        query_terms = Counter(_tokens(query))
        candidates: list[tuple[Chunk, float]] = []
        for chunk_id, chunk in self._chunks.items():
            if self._owners[chunk_id] != user_id:
                continue
            if knowledge_base_ids and self._knowledge_bases[chunk_id] not in knowledge_base_ids:
                continue
            lexical = _lexical_score(query_terms, Counter(_tokens(chunk.text)))
            semantic = _cosine(query_vector, self._vectors[chunk_id])
            score = 0.45 * lexical + 0.55 * max(semantic, 0.0)
            candidates.append((chunk, score))
        return sorted(candidates, key=lambda item: item[1], reverse=True)[:top_k]

    async def save_workflow(self, *, run: dict, owner_id: str, workflow_input: dict) -> None:
        self._workflow_runs[run["run_id"]] = {"owner_id": owner_id, "input": workflow_input, **run}

    async def find_document_by_hash(self, *, user_id: str, knowledge_base_id: str, content_hash: str) -> tuple[str, int] | None:
        matching = [chunk_id for chunk_id, chunk in self._chunks.items() if self._owners[chunk_id] == user_id and self._knowledge_bases[chunk_id] == knowledge_base_id and chunk.metadata.get("content_hash") == content_hash]
        if not matching:
            return None
        return self._chunks[matching[0]].document_id, len(matching)

    async def list_documents(self, *, user_id: str, knowledge_base_id: str) -> list[dict]:
        grouped: dict[str, dict] = {}
        for chunk_id, chunk in self._chunks.items():
            if self._owners[chunk_id] != user_id or self._knowledge_bases[chunk_id] != knowledge_base_id:
                continue
            item = grouped.setdefault(chunk.document_id, {"document_id": chunk.document_id, "document_name": chunk.document_name, "knowledge_base_id": knowledge_base_id, "chunk_count": 0})
            item["chunk_count"] += 1
        return list(grouped.values())

    async def delete_document(self, *, document_id: str, user_id: str) -> bool:
        targets = [chunk_id for chunk_id, chunk in self._chunks.items() if chunk.document_id == document_id and self._owners[chunk_id] == user_id]
        for chunk_id in targets:
            self._chunks.pop(chunk_id, None)
            self._vectors.pop(chunk_id, None)
            self._owners.pop(chunk_id, None)
            self._knowledge_bases.pop(chunk_id, None)
        return bool(targets)

    async def get_document_chunks(self, *, document_id: str, user_id: str) -> list[Chunk]:
        return [chunk for chunk_id, chunk in self._chunks.items() if chunk.document_id == document_id and self._owners[chunk_id] == user_id]

    async def update_vectors(self, *, chunks: list[Chunk], vectors: list[list[float]], user_id: str) -> None:
        for chunk, vector in zip(chunks, vectors, strict=True):
            if self._owners.get(chunk.chunk_id) != user_id:
                raise PermissionError("document owner mismatch")
            self._vectors[chunk.chunk_id] = vector

    async def get_workflow(self, *, run_id: str, user_id: str) -> dict | None:
        run = self._workflow_runs.get(run_id)
        return run if run and run["owner_id"] == user_id else None


def _tokens(text: str) -> list[str]:
    return re.findall(r"[\u4e00-\u9fff]|[a-zA-Z0-9_]+", text.lower())


def _lexical_score(query: Counter, document: Counter) -> float:
    if not query:
        return 0.0
    overlap = sum(min(count, document[token]) for token, count in query.items())
    return min(1.0, overlap / sum(query.values()))


def _cosine(left: list[float], right: list[float]) -> float:
    if not left or len(left) != len(right):
        return 0.0
    dot = sum(a * b for a, b in zip(left, right))
    norm = math.sqrt(sum(a * a for a in left)) * math.sqrt(sum(b * b for b in right))
    return dot / norm if norm else 0.0
