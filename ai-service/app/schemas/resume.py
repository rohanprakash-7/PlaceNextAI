from pydantic import BaseModel


class ResumeAnalysis(BaseModel):
    ats_score: int
    extracted_skills: list[str]
    missing_keywords: list[str]
    suggestions: list[str]
    word_count: int
