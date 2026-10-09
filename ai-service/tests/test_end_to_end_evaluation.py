import asyncio
from pathlib import Path

from run_evals import run


def test_evaluation_runs_the_real_workflow():
    dataset = Path(__file__).parents[2] / "evals/datasets/lesson_plan_cases.jsonl"
    summary = asyncio.run(run(dataset))

    assert summary["evaluation_mode"] == "end_to_end_demo_provider"
    assert summary["cases"] == 6
    assert summary["refusal_accuracy"] == 1.0
    assert all("latency_ms" in result for result in summary["results"])
