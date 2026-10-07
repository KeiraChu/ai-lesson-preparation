import hashlib
import re
from collections.abc import AsyncIterator

from app.providers.base import ModelProvider


class DemoProvider(ModelProvider):
    """Deterministic offline provider for interviews and local product demos."""

    async def generate_json(self, *, messages: list[dict], schema: dict, model: str | None = None) -> dict:
        content = messages[-1]["content"]
        subject = self._field(content, "学科") or "示例学科"
        grade = self._field(content, "年级") or "示例年级"
        topic = self._field(content, "主题") or "示例课题"
        duration_text = self._field(content, "课时") or "45"
        duration = int(re.search(r"\d+", duration_text).group())
        citations = list(dict.fromkeys(re.findall(r"\[([^\]]+:[^\]]+)\]", content)))
        primary = citations[0] if citations else ""
        return {
            "title": f"{topic}教学设计（演示模式）",
            "subject": subject,
            "grade": grade,
            "duration_minutes": duration,
            "objectives": [f"理解{topic}的核心概念", f"能够依据材料解释并应用{topic}"],
            "key_points": [f"{topic}的概念与应用"],
            "difficulties": ["从资料证据迁移到实际问题"],
            "sections": [
                {"title": "情境导入", "content": f"用贴近学生经验的问题引出{topic}，记录已有认识。", "citations": [primary] if primary else []},
                {"title": "证据探究", "content": "阅读知识库片段，小组提取关键信息并说明依据。", "citations": citations[:2]},
                {"title": "应用检测", "content": "完成分层任务，并用材料中的概念解释解题过程。", "citations": [primary] if primary else []},
            ],
            "homework": [f"整理{topic}知识卡片", "完成一道基础题和一道迁移题"],
            "quality_warnings": ["当前为离线演示模式，结果用于展示工作流，不代表真实模型质量。"],
        }

    async def stream_text(self, *, messages: list[dict], model: str | None = None) -> AsyncIterator[str]:
        yield "离线演示模式"

    async def embed(self, texts: list[str]) -> list[list[float]]:
        return [self._embedding(text) for text in texts]

    @staticmethod
    def _field(content: str, name: str) -> str:
        match = re.search(rf"^{name}：([^\n]+)", content, re.MULTILINE)
        return match.group(1).strip() if match else ""

    @staticmethod
    def _embedding(text: str, dimensions: int = 1024) -> list[float]:
        vector = [0.0] * dimensions
        normalized = text.lower()
        tokens = re.findall(r"[a-z0-9+#.-]+", normalized)
        for sequence in re.findall(r"[\u4e00-\u9fff]+", normalized):
            tokens.extend(sequence[index:index + 2] for index in range(max(1, len(sequence) - 1)))
        for token in tokens:
            digest = hashlib.sha256(token.encode("utf-8")).digest()
            vector[int.from_bytes(digest[:2], "big") % dimensions] += 1.0
        if not any(vector):
            vector[0] = 1.0
        return vector
