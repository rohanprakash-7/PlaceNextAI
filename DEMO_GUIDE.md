# PlaceNextAI — Demo Guide

A ~15-minute walkthrough script covering all 12 phases. Run backend + frontend (+ AI service for the
resume step) before starting — see `README.md` for startup commands. Use two browser windows (or one
normal + one incognito) for the mentor-request/messaging steps, since they need both a student and an
alumni logged in at once.

## 0. Setup (before the audience sees anything)

- Backend, frontend, and AI service running.
- Have three accounts ready: a Student, an Alumni, and a Recruiter (register fresh ones, or reuse the
  seeded accounts in `backend/README.md`).
- Have a sample resume PDF ready to upload.

## 1. Authentication (Phase 1)

- Show the landing page, click **Get started**, register a student.
- Point out the role picker (Student/Recruiter/Alumni) and the day/night theme toggle in the corner.

## 2. Resume Analyzer + Eligibility (Phase 2 + boost feature)

- Student → **Resume Analyzer** → upload the sample PDF.
- Point out: ATS score, extracted skills, suggestions — **and the new "You're eligible for..."
  panel** showing real companies (Amazon, Microsoft, etc.) computed live from the uploaded skills.
- Click through to **Eligibility Checker** to show the full per-job breakdown (matched/missing
  skills, CGPA requirement vs the student's own).

## 3. AI Mock Interview (Phase 3)

- Student → **Mock Interviews** → pick a target company (e.g. Amazon) → **Start interview**.
- Let the first question play out loud (TTS). Click the mic, answer out loud, show the live
  transcript, submit.
- Show the score + feedback, answer the remaining questions, **Finish interview**.
- Point out the results screen, then jump to **Achievements** to show the "Interview Ready" badge
  and XP that just fired, and the bell icon showing the new notification.

## 4. Skill Gap Roadmap (Phase 4)

- Student → **Skill Roadmap** → pick the same target company → **Analyze skill gap** →
  **Generate roadmap**. Show the week-by-week plan and mark one item complete.

## 5. Job Applications & Recruiter Portal (Phase 5)

- Student → **Applications** → apply to a job.
- Switch to the Recruiter account → **Applications** → show the new application, move its status
  forward. Point out the student side (in the other browser) receiving a real-time notification.

## 6. Placement Prediction (Phase 6)

- Student → **Placement Prediction** → show the readiness score, risk level, and the explainable
  factor breakdown.

## 7. Alumni Mentorship (Phase 7)

- Alumni account → **My Slots** → add an availability window.
- Student → **Mentors** → search/filter by company, open a mentor profile, **Request mentorship**.
- Alumni → **Mentor Requests** → **Accept**.
- Both sides → open the chat on the now-accepted request, send a message each way live.

## 8. Achievements & Badges (Phase 8)

- Student → **Leaderboard** to show ranking, then back to **Achievements** → download a badge
  certificate (PDF).

## 9. Admin Analytics (Phase 9)

- Admin account → **Analytics** → scroll through: overview stats, risk distribution, department/
  college breakdowns, hiring trends chart, skill demand charts, activity heatmap.
- Click **Export PDF** or **Export Excel** to show the generated report.

## 10. Notifications (Phase 10)

- Click the bell icon on any dashboard → show the dropdown, **Mark all read**, and **Enable desktop
  alerts** (grant permission, then trigger any action above again to show a real OS notification).
- Visit the full **Notifications** history page.

## 11. Settings

- Any role → **Settings** → update a profile field, then change password, to show both flows work.

## 12. Production readiness (Phase 12)

- Show `docker-compose.yml` and run `docker compose up --build` to demonstrate one-command startup
  of all four services (MySQL, AI service, backend, frontend).
- Show `mvn test` passing (17 tests: unit tests for eligibility/gamification/interview-question logic,
  plus a full register→login→me integration test against an in-memory database).
- Show the GitHub Actions workflow (`.github/workflows/ci.yml`) and Swagger UI
  (`/swagger-ui.html`) with the JWT "Authorize" button.

## Closing talking points

- All 12 phases are implemented and wired together — badges/XP/notifications/readiness all react to
  the same underlying student events rather than being bolted on separately.
- Two explicit, documented tradeoffs worth mentioning if asked: the mock interview is rule-based
  (not an LLM call) for zero-cost determinism, and email/push notifications are opt-in features that
  degrade gracefully without configuration.
