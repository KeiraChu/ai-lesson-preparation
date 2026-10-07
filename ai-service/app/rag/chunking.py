import re
from dataclasses import dataclass, field


@dataclass(frozen=True)
class Chunk:
    chunk_id: str
    document_id: str
    document_name: str
    text: str
    metadata: dict[str, str] = field(default_factory=dict)


def chunk_document(
    *, document_id: str, document_name: str, text: str, metadata: dict[str, str], max_chars: int = 900, overlap: int = 120
) -> list[Chunk]:
    cleaned = re.sub(r"[ \t]+", " ", text).strip()
    paragraphs = [part.strip() for part in re.split(r"\n{2,}|(?<=[。！？])\s*", cleaned) if part.strip()]
    chunks: list[Chunk] = []
    current = ""
    for paragraph in paragraphs:
        candidate = f"{current}\n{paragraph}".strip()
        if current and len(candidate) > max_chars:
            chunks.append(Chunk(f"{document_id}:{len(chunks)}", document_id, document_name, current, metadata))
            current = f"{current[-overlap:]}\n{paragraph}".strip()
        else:
            current = candidate
    if current:
        chunks.append(Chunk(f"{document_id}:{len(chunks)}", document_id, document_name, current, metadata))
    return chunks

