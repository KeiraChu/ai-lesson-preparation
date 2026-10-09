from collections.abc import Sequence

from app.models.schemas import EvidenceReport
from app.rag.chunking import Chunk


class EvidenceQualityGate:
    """Turns retrieval and citation signals into a review decision.

    The gate deliberately avoids a model-generated confidence score. Every
    value is derived from retrieved chunks and validated citation ids, so the
    teacher can understand why a lesson plan requires review.
    """

    def assess(
        self,
        *,
        selected: Sequence[tuple[Chunk, float]],
        section_citations: Sequence[Sequence[str]],
    ) -> EvidenceReport:
        total_sections = len(section_citations)
        cited_sections = sum(1 for citations in section_citations if citations)
        coverage = cited_sections / total_sections if total_sections else 0.0
        confidence = sum(score for _, score in selected) / len(selected) if selected else 0.0
        decision = "PASS" if coverage == 1.0 and confidence >= 0.35 else "REVIEW"
        return EvidenceReport(
            evidence_relevance_score=round(max(0.0, min(1.0, confidence)), 4),
            citation_coverage=round(coverage, 4),
            cited_sections=cited_sections,
            total_sections=total_sections,
            decision=decision,
        )
