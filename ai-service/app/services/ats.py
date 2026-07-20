"""ATS resume analysis.

v1 is deliberately lightweight and deterministic: TF-IDF cosine similarity
(scikit-learn) + dictionary skill extraction + structural heuristics.
The public function signature is stable so a sentence-transformer model can
replace the similarity component later without touching the API layer.
"""

import re

from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.metrics.pairwise import cosine_similarity

from app.schemas.resume import ResumeAnalysis

SKILL_DICTIONARY = [
    "python", "java", "c++", "c#", "javascript", "typescript", "go", "rust", "kotlin", "swift",
    "html", "css", "react", "angular", "vue", "next.js", "node.js", "express", "spring boot",
    "django", "flask", "fastapi", "rest api", "graphql", "microservices",
    "sql", "mysql", "postgresql", "mongodb", "redis", "elasticsearch", "hibernate", "jpa",
    "machine learning", "deep learning", "nlp", "computer vision", "data science",
    "pandas", "numpy", "scikit-learn", "tensorflow", "pytorch", "transformers",
    "docker", "kubernetes", "aws", "azure", "gcp", "jenkins", "ci/cd", "linux", "git",
    "data structures", "algorithms", "system design", "oops", "object oriented programming",
    "power bi", "tableau", "excel", "selenium", "junit", "pytest", "agile", "jira",
    "tailwind", "bootstrap", "figma", "communication", "leadership", "teamwork",
]

SECTION_PATTERNS = {
    "education": r"\beducation\b|\bacademic\b",
    "projects": r"\bprojects?\b",
    "experience": r"\bexperience\b|\binternship\b",
    "skills": r"\bskills?\b|\btechnologies\b",
}

CONTACT_PATTERNS = {
    "email": r"[\w.+-]+@[\w-]+\.[\w.]+",
    "phone": r"(\+?\d[\d\s-]{8,14}\d)",
}


def _find_skills(text: str) -> list[str]:
    lowered = " " + re.sub(r"[^a-z0-9+#./ ]", " ", text.lower()) + " "
    found = []
    for skill in SKILL_DICTIONARY:
        pattern = r"(?<![a-z0-9])" + re.escape(skill) + r"(?![a-z0-9])"
        if re.search(pattern, lowered):
            found.append(skill)
    return found


def _structure_score(text: str) -> tuple[int, list[str]]:
    score = 0
    suggestions = []

    for name, pattern in SECTION_PATTERNS.items():
        if re.search(pattern, text, flags=re.IGNORECASE):
            score += 15
        else:
            suggestions.append(f"Add a clear '{name.title()}' section - ATS parsers look for it.")

    for name, pattern in CONTACT_PATTERNS.items():
        if re.search(pattern, text):
            score += 10
        else:
            suggestions.append(f"Include your {name} in the header so recruiters can reach you.")

    words = len(text.split())
    if words < 150:
        suggestions.append("The resume is very short - aim for 300-600 words of substance.")
    elif words > 1200:
        suggestions.append("The resume is long - tighten it; one to two pages reads best.")
    else:
        score += 20

    return min(score, 100), suggestions


def analyze_resume(text: str, job_description: str | None = None) -> ResumeAnalysis:
    text = text.strip()
    word_count = len(text.split())

    extracted = _find_skills(text)
    structure, suggestions = _structure_score(text)

    skill_richness = min(len(extracted) * 8, 100)

    missing: list[str] = []
    if job_description and job_description.strip():
        jd_skills = _find_skills(job_description)
        missing = [skill for skill in jd_skills if skill not in extracted]
        coverage = 100 if not jd_skills else round(
            100 * (len(jd_skills) - len(missing)) / len(jd_skills)
        )

        vectorizer = TfidfVectorizer(stop_words="english")
        matrix = vectorizer.fit_transform([text, job_description])
        similarity = round(float(cosine_similarity(matrix[0:1], matrix[1:2])[0][0]) * 100)

        ats = round(0.45 * coverage + 0.25 * similarity + 0.30 * structure)
        if missing:
            preview = ", ".join(missing[:5])
            suggestions.insert(0, f"Add these keywords from the job description: {preview}.")
    else:
        ats = round(0.55 * structure + 0.45 * skill_richness)
        suggestions.append("Paste a target job description next time for a keyword-matched score.")

    if not extracted:
        suggestions.insert(0, "No recognizable technical skills found - list your skills explicitly.")

    return ResumeAnalysis(
        ats_score=max(0, min(100, ats)),
        extracted_skills=extracted,
        missing_keywords=missing,
        suggestions=suggestions[:6],
        word_count=word_count,
    )
