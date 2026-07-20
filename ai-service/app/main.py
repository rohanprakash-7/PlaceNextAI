from fastapi import FastAPI

from app.api.v1.resume import router as resume_router
from app.core.config import settings


def create_app() -> FastAPI:
    app = FastAPI(title="PlaceNextAI - AI Service", version="0.3.0", docs_url="/docs")
    app.include_router(resume_router)

    @app.get("/health", tags=["system"])
    def health() -> dict:
        return {"status": "ok", "service": settings.app_name}

    return app


app = create_app()
