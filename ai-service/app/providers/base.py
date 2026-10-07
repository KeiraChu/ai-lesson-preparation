from abc import ABC, abstractmethod
from collections.abc import AsyncIterator


class ModelProvider(ABC):
    @abstractmethod
    async def generate_json(self, *, messages: list[dict], schema: dict, model: str | None = None) -> dict:
        raise NotImplementedError

    @abstractmethod
    async def stream_text(self, *, messages: list[dict], model: str | None = None) -> AsyncIterator[str]:
        raise NotImplementedError

    @abstractmethod
    async def embed(self, texts: list[str]) -> list[list[float]]:
        raise NotImplementedError

