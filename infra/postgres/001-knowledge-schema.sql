CREATE EXTENSION IF NOT EXISTS vector;
CREATE TABLE IF NOT EXISTS knowledge_base (
    id TEXT PRIMARY KEY, owner_id BIGINT NOT NULL, name TEXT NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE TABLE IF NOT EXISTS knowledge_document (
    id TEXT PRIMARY KEY, knowledge_base_id TEXT NOT NULL REFERENCES knowledge_base(id) ON DELETE CASCADE,
    owner_id BIGINT NOT NULL, name TEXT NOT NULL, object_key TEXT,
    status TEXT NOT NULL DEFAULT 'PENDING', metadata JSONB NOT NULL DEFAULT '{}'::jsonb, content_hash CHAR(64) NOT NULL DEFAULT '',
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);
ALTER TABLE knowledge_document ADD COLUMN IF NOT EXISTS content_hash CHAR(64) NOT NULL DEFAULT '';
CREATE UNIQUE INDEX IF NOT EXISTS idx_document_owner_kb_hash ON knowledge_document(owner_id, knowledge_base_id, content_hash) WHERE content_hash <> '';
CREATE TABLE IF NOT EXISTS knowledge_chunk (
    id TEXT PRIMARY KEY, document_id TEXT NOT NULL REFERENCES knowledge_document(id) ON DELETE CASCADE,
    owner_id BIGINT NOT NULL, content TEXT NOT NULL, metadata JSONB NOT NULL DEFAULT '{}'::jsonb,
    embedding vector(1024), search_vector tsvector GENERATED ALWAYS AS (to_tsvector('simple', content)) STORED
);
CREATE INDEX IF NOT EXISTS idx_chunk_embedding ON knowledge_chunk USING hnsw (embedding vector_cosine_ops);
CREATE INDEX IF NOT EXISTS idx_chunk_search ON knowledge_chunk USING gin (search_vector);
CREATE INDEX IF NOT EXISTS idx_chunk_owner ON knowledge_chunk(owner_id);
CREATE TABLE IF NOT EXISTS ai_workflow_run (
    id UUID PRIMARY KEY, owner_id BIGINT NOT NULL, workflow_type TEXT NOT NULL, status TEXT NOT NULL,
    current_step TEXT, input JSONB NOT NULL, output JSONB, error TEXT, model_name TEXT,
    input_tokens INTEGER NOT NULL DEFAULT 0, output_tokens INTEGER NOT NULL DEFAULT 0,
    latency_ms INTEGER, created_at TIMESTAMPTZ NOT NULL DEFAULT now(), updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);
