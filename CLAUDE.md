# CLAUDE.md

## 프로젝트

- **냉고** — 냉장고 재고를 중심에 둔 레시피 추천 AI. 캡스톤 냉고([yschoi9733/naengo-ai](https://github.com/yschoi9733/naengo-ai))를 기획부터 다시 만드는 개인 포트폴리오 프로젝트다(상업 서비스가 아니다).
- 모노레포: `backend/`(Spring 본 서버) · `ai/`(Python AI 서버 — LangGraph 에이전트) · `frontend/`(웹 화면) · `docs/`(설계 문서). 폴더마다 자기 CLAUDE.md가 있으니, 그 폴더를 고치기 전에 읽는다.
- 설계 정본은 `docs/`다(서버 구성·테이블 주인은 `docs/architecture.md`). 코드와 문서가 어긋나면 같은 PR에서 함께 고친다.

## 서버 사이 규칙

`docs/architecture.md`를 줄인 것이다. 바꿀 때는 그 문서부터 고친다.

- 웹은 Spring만 부른다. Python AI 서버는 밖에 열지 않고, Spring이 내부 HTTP와 내부 토큰으로만 부른다. 사용자 로그인 토큰은 Spring에서 끝난다.
- DB 테이블마다 주인 서버는 하나다. Spring(MySQL)은 사용자·프로필·냉장고 재고·채팅방과 메시지(대화 기록의 정본)·좋아요·스크랩·레시피 원본·재료 표준 사전을, Python(Postgres+pgvector)은 검색 색인(레시피 임베딩과 재료 표준 사전 사본)과 수집 작업 테이블을 갖는다.
- 남의 테이블은 직접 읽거나 쓰지 않고 주인 서버의 API를 부른다. Python이 검색하다가 Spring을 되부르지 않는다(순환 호출).
- LLM과 임베딩 모델은 Python만 부른다.
- 두 서버 사이 API를 바꾸면 양쪽을 같은 PR에서 고친다.

## 공통 규칙

- 주석·문서는 한국어로 쓰고, 주석은 최소화한다 — 코드를 읽으면 알 수 있는 건 안 쓴다.
- 예외: `.claude/skills/` 스크립트는 판정 기준과 그 근거(실측·겪은 사고)를 머리말과 판정 옆 주석에 남긴다.
- 비밀 값(API 키·내부 토큰·로그인 토큰 서명 키·DB 비밀번호)은 환경변수로만 받는다. `.env`·`application-local.*`은 커밋하지 않는다(.gitignore에 있다).
- 공개 레포다. 수집한 레시피 원문·사진·데이터 덤프는 커밋하지 않고 `ai/data/`(.gitignore에 있다)나 DB에만 둔다. 비상업이어도 원문의 저작권은 남는다.

## 빌드 / 실행

- 로컬 실행: `backend/`에서 `[ -f .env ] || cp .env.example .env`(처음 한 번) → `docker compose up -d --wait`(MySQL) → `./gradlew bootRun`(8080), `ai/`에서 `uv run uvicorn naengo_ai.main:app --reload --port 8000`, `frontend/`에서 `npm ci` → `npm run dev`(http://localhost:5173, `/api` 요청은 8080으로 넘긴다).
- 기본 게이트(Claude가 PR 전에 돌리는 검사) — 고친 폴더마다 그 폴더에서 돌린다.
  - `backend/`: `./gradlew build`(컴파일+테스트). Docker가 꺼져 있으면 DB 테스트가 SKIPPED로 끝나니 그 사실을 PR에 적고 CI 결과로 넘긴다. 첫 빌드는 의존성을 받느라 몇 분 걸릴 수 있으니 Bash 제한 시간을 넉넉히(10분) 준다.
  - `ai/`: `uv run ruff format --check . && uv run ruff check . && uv run pytest`.
  - `frontend/`: `npm run format:check && npm run lint && npm test && npm run build`(빌드에 타입 검사가 들어 있다).
  - `.github/`를 고쳤으면 그 워크플로가 보는 폴더의 게이트를, `.claude/skills/`나 `.gitignore`를 고쳤으면 레포 맨 위에서 `bash .claude/skills/issue/tests/run.sh`를 돌린다.

## Git / 협업 규칙

- `main` 직접 push 금지. 흐름: **이슈 먼저** → 최신 `main`에서 `<타입>/<이슈번호>-<기능>` 브랜치 → PR 본문 첫 줄에 `Closes #<이슈번호>`.
- 기능 작업은 `/issue <번호> naengo`로 한다(`.claude/skills/issue`). 사람은 PR을 코드 리뷰하고 머지한다.
- **Squash 머지 전용**(merge commit·rebase 비활성, 머지하면 브랜치 자동 삭제) — PR 제목이 곧 `main`의 커밋 메시지이므로 PR 제목은 커밋 컨벤션(`feat: ...`)을 따른다.
- 커밋 메시지·이슈·PR은 한국어로 쓴다. 커밋·PR 어디에도 AI 관여 표기(Co-Authored-By, Generated with 등)를 넣지 않는다.
- PR 본문에 머지 조건·머지 담당 문장을 넣지 않는다.
- 스테이징은 파일을 지정해서 한다 — `git add -A` 금지.
- 이슈를 새로 만들기 전에 기존 이슈와 중복되는지 확인한다. 이슈·PR 본문은 올리기 전에 처음 읽는 사람 기준으로 가독성을 점검한다.

## 타입 / 영역 라벨

커밋·브랜치·PR·라벨에 **같은 타입 어휘**를 쓴다.

- 타입: `feat` · `fix` · `refactor` · `docs` · `chore` — 라벨은 이 5개. 커밋에는 필요하면 `build` · `ci` · `style` · `test` · `perf`도 쓴다.
- 영역(폴더와 같다): `area: backend`(Spring 본 서버) · `area: ai`(Python AI 서버·수집·평가) · `area: frontend`(웹 화면)
- 이슈·PR에는 **타입 라벨 1개**를 붙이고, 작업이 한 폴더에 분명히 속하면 그 **영역 라벨 1개**를 더 붙인다. 여러 폴더에 걸치거나(서버 사이 API 등) 폴더 밖(문서·CI·스킬) 작업은 영역 라벨 없이 둔다.
- 상태·메타 라벨: 위 타입·영역이 아닌 라벨(GitHub 기본 `bug`·`enhancement`·`question` 등).
