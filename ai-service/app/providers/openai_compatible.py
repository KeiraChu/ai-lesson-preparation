import asyncio
import json
from collections.abc import AsyncIterator

import httpx

from app.core.config import Settings
from app.providers.base import ModelProvider


class OpenAICompatibleProvider(ModelProvider):
    def __init__(self, settings: Settings):
        self.settings = settings
        self.client = httpx.AsyncClient(
            base_url=settings.model_base_url.rstrip("/"),
            timeout=settings.request_timeout_seconds,
            headers={"Authorization": f"Bearer {settings.api_key}"},
        )

    async def close(self) -> None:
        await self.client.aclose()

    async def _post_with_retry(self, path: str, payload: dict) -> httpx.Response:
        last_error: Exception | None = None
        for attempt in range(self.settings.max_retries + 1):
            try:
                response = await self.client.post(path, json=payload)
                response.raise_for_status()
                return response
            except (httpx.TimeoutException, httpx.HTTPStatusError) as exc:
                last_error = exc
                if attempt == self.settings.max_retries:
                    break
                await asyncio.sleep(0.5 * (2**attempt))
        raise RuntimeError("model provider unavailable") from last_error

    async def generate_json(self, *, messages: list[dict], schema: dict, model: str | None = None) -> dict:
        response = await self._post_with_retry(
            "/chat/completions",
            {
                "model": model or self.settings.chat_model,
                "messages": messages,
                "temperature": 0.2,
                "response_format": {
                    "type": "json_schema",
                    "json_schema": {"name": "lesson_plan", "strict": True, "schema": schema},
                },
            },
        )
        body = response.json()
        usage = body.get("usage", {})
        input_tokens = usage.get("prompt_tokens", usage.get("input_tokens", 0))
        output_tokens = usage.get("completion_tokens", usage.get("output_tokens", 0))
        self.record_usage({"input_tokens": input_tokens, "output_tokens": output_tokens, "estimated_cost": (input_tokens * self.settings.chat_input_cost_per_million + output_tokens * self.settings.chat_output_cost_per_million) / 1_000_000})
        content = body["choices"][0]["message"]["content"]
        return json.loads(content)

    async def stream_text(self, *, messages: list[dict], model: str | None = None) -> AsyncIterator[str]:
        payload = {
            "model": model or self.settings.chat_model,
            "messages": messages,
            "temperature": 0.2,
            "stream": True,
        }
        async with self.client.stream("POST", "/chat/completions", json=payload) as response:
            response.raise_for_status()
            async for line in response.aiter_lines():
                if not line.startswith("data: ") or line == "data: [DONE]":
                    continue
                data = json.loads(line[6:])
                delta = data["choices"][0]["delta"].get("content")
                if delta:
                    yield delta

    async def embed(self, texts: list[str]) -> list[list[float]]:
        response = await self._post_with_retry(
            "/embeddings", {"model": self.settings.embedding_model, "input": texts}
        )
        body = response.json()
        rows = sorted(body["data"], key=lambda item: item.get("index", 0))
        tokens = body.get("usage", {}).get("total_tokens", body.get("usage", {}).get("prompt_tokens", 0))
        self.record_usage({"embedding_tokens": tokens, "estimated_cost": tokens * self.settings.embedding_cost_per_million / 1_000_000})
        return [item["embedding"] for item in rows]
