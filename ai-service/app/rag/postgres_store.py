import json

import asyncpg
from pgvector.asyncpg import register_vector

from app.rag.chunking import Chunk


class PostgresHybridStore:
    def __init__(self, database_url: str) -> None:
        self.database_url = database_url
        self.pool: asyncpg.Pool | None = None

    async def connect(self) -> None:
        async def initialize(connection: asyncpg.Connection) -> None:
            await register_vector(connection)
        self.pool = await asyncpg.create_pool(self.database_url, init=initialize, min_size=1, max_size=8)

    async def close(self) -> None:
        if self.pool:
            await self.pool.close()

    async def upsert(self, *, chunks: list[Chunk], vectors: list[list[float]], user_id: str, knowledge_base_id: str) -> None:
        if not self.pool:
            raise RuntimeError("PostgreSQL store is not connected")
        async with self.pool.acquire() as connection:
            async with connection.transaction():
                storage_knowledge_base_id = f"{user_id}:{knowledge_base_id}"
                await connection.execute(
                    "INSERT INTO knowledge_base(id, owner_id, name) VALUES($1,$2,$3) ON CONFLICT(id) DO NOTHING",
                    storage_knowledge_base_id, int(user_id), knowledge_base_id,
                )
                if chunks:
                    first = chunks[0]
                    await connection.execute(
                        """INSERT INTO knowledge_document(id, knowledge_base_id, owner_id, name, status, metadata)
                           VALUES($1,$2,$3,$4,'READY',$5::jsonb)
                           ON CONFLICT(id) DO UPDATE SET status='READY', metadata=EXCLUDED.metadata""",
                        first.document_id, storage_knowledge_base_id, int(user_id), first.document_name, json.dumps(first.metadata),
                    )
                for chunk, vector in zip(chunks, vectors, strict=True):
                    await connection.execute(
                        """INSERT INTO knowledge_chunk(id, document_id, owner_id, content, metadata, embedding)
                           VALUES($1,$2,$3,$4,$5::jsonb,$6)
                           ON CONFLICT(id) DO UPDATE SET content=EXCLUDED.content, metadata=EXCLUDED.metadata, embedding=EXCLUDED.embedding""",
                        chunk.chunk_id, chunk.document_id, int(user_id), chunk.text, json.dumps(chunk.metadata), vector,
                    )

    async def search(self, *, query: str, query_vector: list[float], user_id: str, knowledge_base_ids: list[str], top_k: int) -> list[tuple[Chunk, float]]:
        if not self.pool:
            raise RuntimeError("PostgreSQL store is not connected")
        storage_knowledge_base_ids = [f"{user_id}:{item}" for item in knowledge_base_ids]
        rows = await self.pool.fetch(
            """SELECT c.id, c.document_id, d.name AS document_name, c.content, c.metadata,
                      (0.55 * (1 - (c.embedding <=> $1::vector)) +
                       0.45 * LEAST(1, ts_rank_cd(c.search_vector, plainto_tsquery('simple', $2)))) AS score
               FROM knowledge_chunk c JOIN knowledge_document d ON d.id = c.document_id
               WHERE c.owner_id = $3
                 AND (cardinality($4::text[]) = 0 OR d.knowledge_base_id = ANY($4::text[]))
               ORDER BY score DESC LIMIT $5""",
            query_vector, query, int(user_id), storage_knowledge_base_ids, top_k,
        )
        return [
            (Chunk(row["id"], row["document_id"], row["document_name"], row["content"], dict(row["metadata"])), float(row["score"]))
            for row in rows
        ]

    async def save_workflow(self, *, run: dict, owner_id: str, workflow_input: dict) -> None:
        if not self.pool:
            raise RuntimeError("PostgreSQL store is not connected")
        await self.pool.execute(
            """INSERT INTO ai_workflow_run(id, owner_id, workflow_type, status, current_step, input, output, error)
               VALUES($1::uuid,$2,'LESSON_PLAN',$3,$4,$5::jsonb,$6::jsonb,$7)
               ON CONFLICT(id) DO UPDATE SET status=EXCLUDED.status, current_step=EXCLUDED.current_step,
                   output=EXCLUDED.output, error=EXCLUDED.error, updated_at=now()""",
            run["run_id"], int(owner_id), run["status"], run["current_step"],
            json.dumps(workflow_input, ensure_ascii=False),
            json.dumps(run.get("lesson_plan"), ensure_ascii=False) if run.get("lesson_plan") else None,
            run.get("error"),
        )
