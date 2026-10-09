import asyncio
import json
from datetime import datetime, timezone
from pathlib import Path

from app.core.config import Settings
from app.providers.openai_compatible import OpenAICompatibleProvider
from run_evals import run


async def main() -> None:
    settings = Settings(demo_mode=False)
    report_path = Path(__file__).parents[1] / "docs/real-model-evaluation.md"
    if not settings.api_key:
        raise SystemExit("AI_API_KEY is required; no report was overwritten")
    provider = OpenAICompatibleProvider(settings)
    try:
        dataset = Path(__file__).parents[1] / "evals/datasets/lesson_plan_cases.jsonl"
        result = await run(dataset, provider, settings)
    finally:
        await provider.close()
    report = "# 真实模型评测报告\n\n" + f"- 执行时间：{datetime.now(timezone.utc).isoformat()}\n- 模型：`{settings.chat_model}`\n- Embedding：`{settings.embedding_model}`\n- 状态：已执行\n\n```json\n{json.dumps(result, ensure_ascii=False, indent=2)}\n```\n"
    report_path.write_text(report, encoding="utf-8")
    print(report)


if __name__ == "__main__":
    asyncio.run(main())
