# PlaceNextAI — Production Deployment Guide

Deploys the existing app as-is: no business logic changes, only configuration.

| Piece | Host | Why |
|---|---|---|
| Frontend (React/Vite) | **Vercel** | Static build, free, instant HTTPS |
| Backend (Spring Boot) | **Render** (Docker) | Free web service, Dockerfile support |
| AI service (FastAPI) | **Render** (Docker) | Same as above, separate service |
| Database | **Aiven for MySQL** (free tier) | The app uses MySQL/JPA — Render has no managed MySQL, so this replaces the "MongoDB Atlas" step. Railway MySQL is an equally good alternative if you prefer it. |

Your GitHub repo root already **is** `backend/`, `frontend/`, `ai-service/` as top-level folders (confirmed from your existing `origin` remote at `https://github.com/rohanprakash-7/PlaceNextAI.git`) — every path below assumes that layout.

---

## 0. Prerequisites

Create free accounts (skip any you already have):
- GitHub — you already have `rohanprakash-7/PlaceNextAI`
- [Aiven](https://aiven.io) (or [Railway](https://railway.app)) — for MySQL
- [Render](https://render.com) — sign up with GitHub for one-click repo access
- [Vercel](https://vercel.com) — sign up with GitHub

---

## 1. Push your existing work to GitHub

Your local repo is already 6 phases behind `origin/main` (everything from the mentorship platform onward, plus today's bug fixes, has never been pushed). From the `phase1` folder:

```bash
cd phase1
git add -A
git commit -m "Add phases 7-12, bug fixes, and production deployment config"
git push origin main
```

If `git push` asks for credentials and you haven't set up a GitHub token/SSH key on this machine, GitHub will prompt you to authenticate in the browser the first time.

---

## 2. Database — Aiven for MySQL

1. Sign in at [console.aiven.io](https://console.aiven.io) → **Create service** → **MySQL**.
2. Pick the **Free** plan, any region close to you, name it `placenextai-db`.
3. Wait ~2 minutes for it to go from "Rebuilding" to "Running".
4. Open the service → **Overview** tab → copy the connection details:
   - `Host`
   - `Port`
   - `User` (usually `avnadmin`)
   - `Password`
   - `Default database name` (usually `defaultdb` — rename or just use it)
5. Build your JDBC URL (Aiven requires SSL):
   ```
   jdbc:mysql://<HOST>:<PORT>/defaultdb?useSSL=true&requireSSL=true&serverTimezone=UTC
   ```
   You'll paste this as `DB_URL` in Render (step 4). Keep the username/password handy for `DB_USERNAME` / `DB_PASSWORD`.

**Railway alternative:** New Project → Provision MySQL → Variables tab gives you `MYSQL_URL`, `MYSQLUSER`, `MYSQLPASSWORD` directly — same idea, just build the same `jdbc:mysql://...` string from Railway's host/port instead.

---

## 3. AI service on Render

1. Render dashboard → **New** → **Web Service** → connect the `rohanprakash-7/PlaceNextAI` repo.
2. Settings:
   - **Name:** `placenextai-ai-service`
   - **Root Directory:** `ai-service`
   - **Runtime:** Docker (Render auto-detects the `Dockerfile`)
   - **Instance type:** Free
   - **Health check path:** `/health`
3. Click **Create Web Service**. First build takes ~3-5 minutes.
4. Once live, copy its URL, e.g. `https://placenextai-ai-service.onrender.com`. You need this in step 4.

> A blueprint (`render.yaml` at the repo root) is already in your repo — you can alternatively use **New → Blueprint** and point it at the repo to create both Render services at once, then just fill in the secret fields it leaves blank.

---

## 4. Backend on Render

1. **New** → **Web Service** → same repo.
2. Settings:
   - **Name:** `placenextai-backend`
   - **Root Directory:** `backend`
   - **Runtime:** Docker
   - **Instance type:** Free
   - **Health check path:** `/actuator/health`
3. **Environment** tab → add these variables:

   | Key | Value |
   |---|---|
   | `SPRING_PROFILES_ACTIVE` | `prod` |
   | `SPRING_JPA_HIBERNATE_DDL_AUTO` | `update` *(first deploy only — see note below)* |
   | `DB_URL` | your Aiven JDBC URL from step 2 |
   | `DB_USERNAME` | `avnadmin` (or your Aiven/Railway user) |
   | `DB_PASSWORD` | your Aiven/Railway password |
   | `JWT_SECRET` | any long random string (e.g. generate with `openssl rand -hex 32`) — **do not reuse the local dev secret** |
   | `AI_SERVICE_BASE_URL` | the Render AI service URL from step 3, e.g. `https://placenextai-ai-service.onrender.com` |
   | `CORS_ALLOWED_ORIGINS` | leave as `http://localhost:5173` for now — you'll update this in step 6 once Vercel gives you a URL |
   | `MAIL_ENABLED` | `false` (leave off unless you configure real SMTP) |

4. Click **Create Web Service**.

**About `SPRING_JPA_HIBERNATE_DDL_AUTO=update`:** your `application-prod.properties` intentionally uses `validate` (never auto-migrate a real production schema) — but on a **brand-new empty database**, `validate` fails because no tables exist yet. The `update` env var above overrides the file just for this deploy so Hibernate creates the schema once. After confirming the tables exist (Aiven console → your database → Tables), delete that env var from Render so it falls back to the safer `validate`.

Once live, copy the backend's URL, e.g. `https://placenextai-backend.onrender.com`.

---

## 5. Frontend on Vercel

1. Vercel dashboard → **Add New** → **Project** → import `rohanprakash-7/PlaceNextAI`.
2. **Root Directory:** `frontend` (click Edit next to Root Directory and select it).
3. Framework preset: Vite (auto-detected).
4. **Environment Variables:**

   | Key | Value |
   |---|---|
   | `VITE_API_BASE_URL` | `https://placenextai-backend.onrender.com/api` (your real backend URL + `/api`) |

5. **Deploy.** Vercel gives you a URL like `https://placenextai.vercel.app`.

### Alternative: Frontend on Render Static Site instead of Vercel

Works the same way, with one gotcha that will otherwise break every route except `/`:

1. Render dashboard → **New** → **Static Site** → same repo.
2. **Root Directory:** `frontend`
3. **Build Command:** `npm run build`
4. **Publish Directory:** `dist`
5. **Environment Variables:** same `VITE_API_BASE_URL` as above (Static Site builds still read Vite env vars at build time).
6. **The gotcha:** a client-side-routed app (React Router) needs the server to serve `index.html` for *every* path (`/register`, `/login`, `/dashboard/...`), not just `/`. Without that, any hard navigation or refresh on those routes returns Render's 404 — or, if you added a catch-all rule in the dashboard set to **Redirect** instead of **Rewrite**, it bounces back to `/` (this is the exact "Create Account causes a full reload back to homepage" bug). The repo now ships `frontend/public/_redirects` (`/* /index.html 200`), which Vite copies into `dist/` on build and Render Static Sites auto-detect — so this is handled automatically as long as you're deploying from the current commit. If you'd previously added a manual Redirect/Rewrite rule in the Render dashboard, delete it so it doesn't conflict with the `_redirects` file.

---

## 6. Wire the URLs together (required — do this or CORS will block everything)

Go back to the **backend** service on Render → Environment → update:

| Key | Value |
|---|---|
| `CORS_ALLOWED_ORIGINS` | `https://placenextai.vercel.app` (your real Vercel URL — comma-separate multiple origins if you have a preview URL too) |

Save → Render redeploys automatically. This is the step people most often forget: without it, the browser will block every API call from the deployed frontend with a CORS error even though everything else is running.

---

## 7. Environment variable reference (full list)

**Backend (`placenextai-backend` on Render)**

| Variable | Required | Notes |
|---|---|---|
| `SPRING_PROFILES_ACTIVE` | yes | `prod` |
| `DB_URL` | yes | Aiven/Railway JDBC URL with SSL params |
| `DB_USERNAME` | yes | |
| `DB_PASSWORD` | yes | |
| `JWT_SECRET` | yes | long random string, never the local dev default |
| `AI_SERVICE_BASE_URL` | yes | full HTTPS URL of the Render AI service |
| `CORS_ALLOWED_ORIGINS` | yes | your Vercel URL(s), comma-separated |
| `SPRING_JPA_HIBERNATE_DDL_AUTO` | first deploy only | `update`, then remove |
| `MAIL_ENABLED` | no | `false` unless you configure SMTP |
| `MAIL_HOST` / `MAIL_USERNAME` / `MAIL_PASSWORD` | no | only if `MAIL_ENABLED=true` |
| `PORT` | no | Render sets this automatically |

**AI service (`placenextai-ai-service` on Render)**

| Variable | Required | Notes |
|---|---|---|
| `PORT` | no | Render sets this automatically |

**Frontend (Vercel)**

| Variable | Required | Notes |
|---|---|---|
| `VITE_API_BASE_URL` | yes | `https://<your-backend>.onrender.com/api` |

---

## 8. Verification checklist

Run through these against your live Vercel URL once all three services are deployed:

- [ ] `https://<ai-service>.onrender.com/health` → `{"status":"ok",...}`
- [ ] `https://<backend>.onrender.com/actuator/health` → `{"status":"UP"}`
- [ ] Frontend loads at your Vercel URL with no console CORS errors
- [ ] Register + login as a Student (confirms backend ↔ database)
- [ ] Upload a resume in Resume Analyzer (confirms backend ↔ AI service)
- [ ] Start a Mock Interview and answer a question
- [ ] Register a Recruiter, post a job, view it in Job Postings
- [ ] Register an Alumni, add a slot, have the student send a mentor request, accept it
- [ ] Bell icon shows a notification and "Mark all read" works
- [ ] Settings → change password works for at least one role

---

## 9. Known free-tier caveat

Render's free web services **spin down after ~15 minutes of inactivity** and take 30-60 seconds to wake back up on the next request. The first request your friends make after it's been idle will feel slow (or the AI call may briefly time out) — that's expected on the free tier, not a bug. If that matters for a demo, hit both Render URLs yourself a minute before sharing the link.

---

## 10. Final URLs (fill in once deployed)

| Service | URL |
|---|---|
| Frontend (share this one) | `https://____________.vercel.app` |
| Backend API | `https://____________.onrender.com` |
| AI service | `https://____________.onrender.com` |
| Database | Aiven console (not public) |

Once filled in, `https://____________.vercel.app` is the single link your friends need — everything else is internal.
