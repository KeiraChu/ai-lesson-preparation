from app.rag.chunking import Chunk
from app.services.quality import EvidenceQualityGate


def test_quality_gate_uses_validated_citations_and_retrieval_scores():
    chunk = Chunk("doc-1:0", "doc-1", "教材", "一次函数知识")
    report = EvidenceQualityGate().assess(
        selected=[(chunk, 0.8)],
        section_citations=[["doc-1:0"], []],
    )
    assert report.evidence_relevance_score == 0.8
    assert report.citation_coverage == 0.5
    assert report.decision == "REVIEW"
