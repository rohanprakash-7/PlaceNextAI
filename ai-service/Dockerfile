FROM python:3.11-slim
WORKDIR /app

COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

COPY app ./app

EXPOSE 8000
ENV PORT=8000
HEALTHCHECK --interval=30s --timeout=5s --start-period=15s --retries=5 \
  CMD python -c "import os,urllib.request; urllib.request.urlopen('http://localhost:' + os.environ.get('PORT', '8000') + '/health')" || exit 1

# Shell form (not exec-form JSON array) so $PORT actually expands - Render
# assigns this dynamically, defaulting to 8000 for local/docker-compose use.
CMD uvicorn app.main:app --host 0.0.0.0 --port ${PORT:-8000}
