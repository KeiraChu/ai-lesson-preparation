from app.rag.chunking import chunk_document


def test_chunking_preserves_document_metadata_and_overlap():
    text = "。".join([f"第{i}段教学内容" * 15 for i in range(10)])
    chunks = chunk_document(
        document_id="doc-1",
        document_name="教材.txt",
        text=text,
        metadata={"subject": "语文"},
        max_chars=180,
        overlap=20,
    )
    assert len(chunks) > 1
    assert all(chunk.document_id == "doc-1" for chunk in chunks)
    assert all(chunk.metadata["subject"] == "语文" for chunk in chunks)

