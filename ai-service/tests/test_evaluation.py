from app.models.schemas import Citation, EvaluationCase
from app.services.evaluation import evaluate_case


def test_evaluation_reports_retrieval_and_grounding():
    case = EvaluationCase(
        case_id="case-1",
        query="教学目标",
        expected_document_ids=["doc-1"],
        answer="学生能够理解函数概念",
        citations=[Citation(document_id="doc-1", document_name="教材", chunk_id="doc-1:0", excerpt="理解函数概念", score=0.9)],
    )
    result = evaluate_case(case)
    assert result.retrieval_hit is True
    assert result.citation_coverage == 1.0
    assert result.groundedness_proxy > 0

