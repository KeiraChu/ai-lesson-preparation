from enum import Enum
from typing import Literal

from pydantic import BaseModel, Field


class Citation(BaseModel):
    document_id: str
    document_name: str
    chunk_id: str
    excerpt: str
    score: float = Field(ge=0, le=1)


class LessonSection(BaseModel):
    title: str
    content: str
    citations: list[str] = Field(default_factory=list)


class LessonPlan(BaseModel):
    title: str
    subject: str
    grade: str
    duration_minutes: int = Field(ge=10, le=240)
    objectives: list[str]
    key_points: list[str]
    difficulties: list[str]
    sections: list[LessonSection]
    homework: list[str]
    citations: list[Citation] = Field(default_factory=list)
    quality_warnings: list[str] = Field(default_factory=list)


class LessonPlanRequest(BaseModel):
    user_id: str
    subject: str
    grade: str
    topic: str
    duration_minutes: int = Field(default=45, ge=10, le=240)
    teaching_goals: list[str] = Field(default_factory=list)
    constraints: list[str] = Field(default_factory=list)
    knowledge_base_ids: list[str] = Field(default_factory=list)
    model: str | None = None


class IngestRequest(BaseModel):
    user_id: str
    knowledge_base_id: str
    document_id: str
    document_name: str
    text: str = Field(min_length=1, max_length=2_000_000)
    subject: str | None = None
    grade: str | None = None
    textbook_version: str | None = None


class IngestResponse(BaseModel):
    document_id: str
    chunk_count: int


class WorkflowStatus(str, Enum):
    pending = "PENDING"
    running = "RUNNING"
    waiting_for_review = "WAITING_FOR_REVIEW"
    succeeded = "SUCCEEDED"
    failed = "FAILED"


class WorkflowRun(BaseModel):
    run_id: str
    status: WorkflowStatus
    current_step: str
    lesson_plan: LessonPlan | None = None
    error: str | None = None


class EvaluationCase(BaseModel):
    case_id: str
    query: str
    expected_document_ids: list[str] = Field(default_factory=list)
    answer: str = ""
    citations: list[Citation] = Field(default_factory=list)
    latency_ms: float = 0
    input_tokens: int = 0
    output_tokens: int = 0


class EvaluationResult(BaseModel):
    case_id: str
    retrieval_hit: bool
    citation_coverage: float
    groundedness_proxy: float
    latency_ms: float

