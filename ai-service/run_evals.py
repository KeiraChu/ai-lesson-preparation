import argparse
import json
from pathlib import Path

from app.models.schemas import EvaluationCase
from app.services.evaluation import evaluate_case


def main() -> None:
    parser = argparse.ArgumentParser(description="Evaluate saved RAG/lesson-plan cases")
    parser.add_argument("dataset", type=Path)
    args = parser.parse_args()
    results = []
    for line in args.dataset.read_text(encoding="utf-8").splitlines():
        if line.strip():
            results.append(evaluate_case(EvaluationCase.model_validate_json(line)))
    summary = {
        "cases": len(results),
        "retrieval_hit_rate": sum(item.retrieval_hit for item in results) / len(results) if results else 0,
        "mean_citation_coverage": sum(item.citation_coverage for item in results) / len(results) if results else 0,
        "mean_groundedness_proxy": sum(item.groundedness_proxy for item in results) / len(results) if results else 0,
        "p95_latency_ms": sorted([item.latency_ms for item in results])[max(0, int(len(results) * 0.95) - 1)] if results else 0,
    }
    print(json.dumps(summary, ensure_ascii=False, indent=2))


if __name__ == "__main__":
    main()

