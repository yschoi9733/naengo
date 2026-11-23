# ai/CLAUDE.md

레포 맨 위 CLAUDE.md(서버 사이 규칙·게이트·Git·라벨)를 먼저 따른다. 여기는 `ai/`에만 해당하는 규칙이다.

## 프로젝트

- **Naengo AI** — 냉고의 AI 서버. 밖에 열지 않고 Spring에서만 호출을 받는 내부 서버로, 채팅 답변(RAG — LangGraph 에이전트), 레시피 검색 색인, 레시피 수집, 평가, 나중에 붙일 재료 인식을 맡는다. 파이썬 패키지 이름은 `naengo_agent`다.
- Python 3.13 / uv / FastAPI. LangGraph·Gemini API·Postgres(pgvector)는 처음 쓰는 이슈에서 더한다. 명령은 모두 `ai/`에서 `uv run ...`으로 친다.
- 코드는 `src/naengo_agent/` 아래에 기능별 패키지로 둔다 — `api`(Spring이 부르는 내부 HTTP) · `graph`(LangGraph 흐름) · `search`(색인·검색) · `collect`(수집) · `eval`(평가). 처음 쓰는 이슈에서 만든다.

## 코딩 규칙

- 요청을 처리하는 길의 입출력은 모두 async로 부른다. 동기 라이브러리(동기 DB 드라이버·동기 SDK)는 `asyncio.to_thread`로 감싸고, async 함수 안에서 `run_sync`·`asyncio.run`을 부르지 않는다. 캡스톤 서버는 동기 검색이 워커 하나의 이벤트 루프를 막았고, 외부 검색은 async 안에서 `run_sync`를 불러 매번 예외가 났다.
- 예외를 삼키지 않는다. 실패는 로그에 남기고 Spring에 실패로 돌려준다 — 캡스톤의 외부 검색은 예외를 삼켜서 한 번도 안 돈 걸 몰랐다.
- 설정과 비밀 값은 `pydantic-settings`로 환경변수에서 읽는다.
- 주고받는 데이터는 Pydantic 모델로 쓰고, 함수에는 타입 힌트를 단다.
- 테스트는 pytest로 쓰고, LLM·임베딩 호출은 가짜로 바꿔 네트워크와 비용 없이 돌게 한다. 실제 모델로 재는 건 평가(`eval`)의 몫이다.
- 프롬프트·모델·검색 방식을 바꾸는 PR에는 평가를 돌린 전후 점수를 적는다(평가 도구를 만든 뒤부터).
- 수집기는 robots.txt를 지키고 요청 사이에 간격을 둔다. 수집 결과는 `ai/data/`(.gitignore에 있다)나 DB에만 둔다.
- 서식과 린트는 ruff(`pyproject.toml`)가 정한다. 고친 뒤 `uv run ruff format .`으로 맞춘다.
