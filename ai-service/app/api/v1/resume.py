import io

from fastapi import APIRouter, File, Form, HTTPException, UploadFile
from pypdf import PdfReader

from app.schemas.resume import ResumeAnalysis
from app.services.ats import analyze_resume

router = APIRouter(prefix="/api/v1/resume", tags=["resume"])

MAX_FILE_BYTES = 5 * 1024 * 1024


@router.post("/analyze", response_model=ResumeAnalysis)
async def analyze(
    file: UploadFile = File(...),
    job_description: str | None = Form(default=None),
) -> ResumeAnalysis:
    if file.content_type not in ("application/pdf", "application/octet-stream"):
        raise HTTPException(status_code=400, detail="Only PDF resumes are supported.")

    raw = await file.read()
    if len(raw) > MAX_FILE_BYTES:
        raise HTTPException(status_code=400, detail="Resume must be under 5 MB.")

    try:
        reader = PdfReader(io.BytesIO(raw))
        text = "\n".join(page.extract_text() or "" for page in reader.pages)
    except Exception:
        raise HTTPException(status_code=400, detail="Could not read this PDF. Export a text-based PDF and retry.")

    if len(text.split()) < 20:
        raise HTTPException(
            status_code=400,
            detail="This PDF has no extractable text (likely a scanned image). Export from Word/Docs instead.",
        )

    return analyze_resume(text, job_description)
