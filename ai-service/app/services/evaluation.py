import re

from app.models.schemas import EvaluationCase, EvaluationResult


def evaluate_case(case: EvaluationCase) -> EvaluationResult:
    cited_ids = {citation.document_id for citation in case.citations}
    expected = set(case.expected_document_ids)
    retrieval_hit = not expected or bool(cited_ids & expected)
    citation_coverage = 1.0 if not expected else len(cited_ids & expected) / len(expected)
    cited_terms = set()
    for citation in case.citations:
        cited_terms.update(_tokens(citation.excerpt))
    answer_terms = set(_tokens(case.answer))
    groundedness = len(answer_terms & cited_terms) / len(answer_terms) if answer_terms else 0.0
    return EvaluationResult(
        case_id=case.case_id,
        retrieval_hit=retrieval_hit,
        citation_coverage=round(citation_coverage, 4),
        groundedness_proxy=round(groundedness, 4),
        latency_ms=case.latency_ms,
    )


def _tokens(text: str) -> list[str]:
    return re.findall(r"[\u4e00-\u9fff]|[a-zA-Z0-9_]+", text.lower())

