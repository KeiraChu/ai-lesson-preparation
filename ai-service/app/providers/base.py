from abc import ABC, abstractmethod
from collections.abc import AsyncIterator
from contextvars import ContextVar

_usage: ContextVar[list[dict]] = ContextVar("lesson_model_usage", default=[])


class ModelProvider(ABC):
    def reset_usage(self) -> None:
        _usage.set([])

    def record_usage(self, call: dict) -> None:
        _usage.set([*_usage.get(), call])

    def usage_summary(self) -> dict:
        calls = _usage.get()
        return {
            "model_calls": len(calls),
            "input_tokens": sum(int(item.get("input_tokens", 0)) for item in calls),
            "output_tokens": sum(int(item.get("output_tokens", 0)) for item in calls),
            "embedding_tokens": sum(int(item.get("embedding_tokens", 0)) for item in calls),
            "estimated_cost": round(sum(float(item.get("estimated_cost", 0)) for item in calls), 6),
            "currency": "CNY",
            "usage_source": "provider" if calls else "unavailable",
        }
    @abstractmethod
    async def generate_json(self, *, messages: list[dict], schema: dict, model: str | None = None) -> dict:
        raise NotImplementedError

    @abstractmethod
    async def stream_text(self, *, messages: list[dict], model: str | None = None) -> AsyncIterator[str]:
        raise NotImplementedError

    @abstractmethod
    async def embed(self, texts: list[str]) -> list[list[float]]:
        raise NotImplementedError
