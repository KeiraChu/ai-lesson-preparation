import pytest

from app.providers.demo import DemoProvider


@pytest.mark.asyncio
async def test_demo_provider_is_deterministic_and_preserves_citations():
    provider = DemoProvider()
    messages = [{"role": "user", "content": "学科：数学\n年级：八年级\n主题：一次函数\n课时：45分钟\n\n[doc-1:0] 一次函数图像是一条直线"}]
    first = await provider.generate_json(messages=messages, schema={})
    second = await provider.generate_json(messages=messages, schema={})
    assert first == second
    assert first["sections"][0]["citations"] == ["doc-1:0"]
    assert "演示模式" in first["quality_warnings"][0]


@pytest.mark.asyncio
async def test_demo_embeddings_retrieve_related_chinese_text():
    provider = DemoProvider()
    query, related, unrelated = await provider.embed(["一次函数图像", "一次函数的图像是一条直线", "古诗词意象赏析"])
    dot = lambda left, right: sum(a * b for a, b in zip(left, right))
    assert dot(query, related) > dot(query, unrelated)
