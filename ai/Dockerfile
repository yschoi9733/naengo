# syntax=docker/dockerfile:1

# 빌드 단계와 실행 단계를 같은 파이썬 이미지에서 시작해, 가상환경을 만든 파이썬과 실행할 파이썬을 같게 둔다
FROM python:3.13-slim-trixie AS build
COPY --from=ghcr.io/astral-sh/uv:0.11.24 /uv /bin/uv
ENV UV_PYTHON_DOWNLOADS=0 UV_COMPILE_BYTECODE=1 UV_LINK_MODE=copy
WORKDIR /app
# 의존성 층을 먼저 만들어 두어, 코드만 바뀌면 의존성을 다시 받지 않는다
RUN --mount=type=cache,target=/root/.cache/uv \
    --mount=type=bind,source=uv.lock,target=uv.lock \
    --mount=type=bind,source=pyproject.toml,target=pyproject.toml \
    uv sync --locked --no-dev --no-install-project
COPY . .
# 이 프로젝트도 가상환경 안에 설치해(--no-editable) 실행 이미지에는 가상환경만 옮긴다
RUN --mount=type=cache,target=/root/.cache/uv \
    uv sync --locked --no-dev --no-editable

FROM python:3.13-slim-trixie
# 홈 아래(~/.cache 등)에 쓰는 라이브러리가 있어 홈 디렉터리를 만든다
RUN groupadd --system naengo && useradd --system --gid naengo --create-home naengo
COPY --from=build /app/.venv /app/.venv
ENV PATH="/app/.venv/bin:$PATH" PYTHONUNBUFFERED=1
USER naengo
EXPOSE 8000
# 다른 컨테이너(backend)가 닿도록 0.0.0.0에 띄운다. 호스트에 포트를 열지 않는 것은 compose.yaml이 정한다
CMD ["uvicorn", "naengo_ai.main:app", "--host", "0.0.0.0", "--port", "8000"]
