# PlaceNextAI — Intelligent Placement Readiness, Recruitment & Career Success Platform

A full-stack, AI-assisted campus placement platform: students track their readiness, practice with an
AI mock interviewer, close skill gaps against real company requirements, and get matched to jobs;
recruiters manage postings and shortlist candidates; alumni run a mentorship program; admins get
platform-wide analytics. All 12 planned phases are implemented.

## Phases

| # | Phase | Highlights |
|---|-------|------------|
| 1 | Authentication & Authorization | JWT auth, 4 roles (Student/Recruiter/Admin/Alumni) |
| 2 | AI Resume Analyzer | ATS scoring, skill extraction via the Python `ai-service` |
| 3 | AI Mock Interview | Voice-driven Q&A (speech-to-text/text-to-speech), rule-based scoring, resume + company-aware questions |
| 4 | Skill Gap Analyzer | Company-targeted skill gap analysis and week-by-week roadmap generation |
| 5 | Job Applications & Recruiter Portal | Job postings, applications, status pipeline, recruiter feedback |
| 6 | Placement Prediction & Explainable Analytics | Readiness scoring, risk level, explainable factors |
| 7 | Alumni Mentorship Platform | Mentor profiles, requests, in-app messaging, slot booking, reviews, bookmarks |
| 8 | Achievements & Badges | XP, levels, streaks, leaderboard, badge certificates (PDF) |
| 9 | Admin Analytics | College/department/skill/hiring-trend analytics, PDF/Excel export, activity heatmap |
| 10 | Notifications | In-app + email + browser push, mentor/application/job-alert/session-reminder triggers |
| 11 | Eligibility Checker + AI Career features | Per-company eligibility (skills + CGPA) surfaced right after resume upload |
| 12 | Production Release | Docker, CI/CD, tests, logging, monitoring, security hardening, docs |

See [`ARCHITECTURE.md`](./ARCHITECTURE.md) for the system design and [`DEMO_GUIDE.md`](./DEMO_GUIDE.md)
for a walkthrough script.

## Tech stack

- **Backend**: Java 21, Spring Boot 3.5 (Web, Security, Data JPA, Validation, Mail, Actuator), MySQL 8, JWT
- **Frontend**: React 18 + Vite, Tailwind CSS, React Router, Framer Motion, Recharts
- **AI service**: FastAPI (Python) for resume/ATS analysis
- **Infra**: Docker, Docker Compose, GitHub Actions CI

## Quick start (Docker — recommended)

```bash
cd phase1
DB_PASSWORD=yourpassword docker compose up --build
```

- Frontend: http://localhost:5173
- Backend API: http://localhost:8080 (Swagger UI at `/swagger-ui.html`)
- AI service: http://localhost:8000/docs

## Quick start (manual)

**Backend** (needs MySQL 8 running locally):
```bash
cd phase1/backend
DB_USERNAME=root DB_PASSWORD=yourpassword mvn spring-boot:run
```

**AI service**:
```bash
cd phase1/ai-service
pip install -r requirements.txt
uvicorn app.main:app --reload
```

**Frontend**:
```bash
cd phase1/frontend
npm install
npm run dev
```

## Running tests

```bash
cd phase1/backend
mvn test
```

Tests run against an in-memory H2 database (`application-test.properties`) — no MySQL needed for CI.

## Project structure

```
phase1/
├── backend/        Spring Boot API (entities, services, controllers per feature)
├── frontend/        React + Vite SPA (pages, components, services per feature)
├── ai-service/      FastAPI microservice for resume/ATS analysis
└── docker-compose.yml
```

## Environment variables (backend)

| Variable | Default | Purpose |
|---|---|---|
| `DB_USERNAME` / `DB_PASSWORD` | `root` / `root` | MySQL credentials |
| `JWT_SECRET` | dev key | JWT signing secret — **set a real one in production** |
| `CORS_ALLOWED_ORIGINS` | `http://localhost:5173` | Allowed frontend origin(s) |
| `AI_SERVICE_BASE_URL` | `http://localhost:8000` | Resume AI microservice URL |
| `MAIL_ENABLED` | `false` | Set `true` + `MAIL_HOST/USERNAME/PASSWORD` to send real emails |
| `SPRING_PROFILES_ACTIVE` | *(none)* | Set `prod` to disable Swagger UI, tighten logging, and require `ddl-auto=validate` |
