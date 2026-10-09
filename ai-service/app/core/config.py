from functools import lru_cache

from pydantic_settings import BaseSettings, SettingsConfigDict


class Settings(BaseSettings):
    service_name: str = "ai-lesson-preparation-ai"
    environment: str = "development"
    api_key: str = ""
    demo_mode: bool = False
    internal_api_key: str = "development-only"
    model_base_url: str = "https://dashscope.aliyuncs.com/compatible-mode/v1"
    chat_model: str = "qwen-plus"
    embedding_model: str = "text-embedding-v3"
    request_timeout_seconds: float = 60.0
    max_retries: int = 2
    max_context_chars: int = 12000
    retrieval_top_k: int = 8
    final_context_k: int = 4
    retrieval_min_score: float = 0.15
    rag_backend: str = "memory"
    database_url: str = "postgresql://ai:development-password@localhost:5432/ai_knowledge"
    chat_input_cost_per_million: float = 0.0
    chat_output_cost_per_million: float = 0.0
    embedding_cost_per_million: float = 0.0
    model_config = SettingsConfigDict(env_prefix="AI_", env_file=".env", extra="ignore")


@lru_cache
def get_settings() -> Settings:
    return Settings()
