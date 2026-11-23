# 냉고

냉장고 재고를 중심에 둔 레시피 추천 AI입니다. 캡스톤 디자인에서 만든 옛 냉고 AI 서버(캡스톤 레포 [yschoi9733/naengo-ai](https://github.com/yschoi9733/naengo-ai))를 기획부터 다시 만들고 있습니다.

범용 요리 챗봇으로는 Gemini 같은 범용 AI를 이기기 어렵습니다. 그래서 냉고는 범용 AI가 모르는 **내 냉장고 재고**와, 알레르기 재료를 절대 내보내지 않는 추천에 집중합니다.

## 구조

| 폴더 | 맡은 일 | 스택 |
| --- | --- | --- |
| [`backend/`](backend) | 웹이 부르는 API 전부(회원, 재고, 레시피, 채팅 저장과 중계) | Spring Boot 4, Java 21, MySQL |
| [`ai/`](ai) | 채팅 답변(RAG), 레시피 검색 색인, 수집, 평가 | Python, FastAPI, LangGraph, Postgres(pgvector) |
| [`frontend/`](frontend) | 웹 화면 | React, TypeScript, Vite, Tailwind CSS |
| [`docs/`](docs) | 설계 문서 | |

서버를 왜 둘로 나눴는지, DB 테이블마다 어느 서버가 주인인지는 [docs/architecture.md](docs/architecture.md)에 있습니다.

## 캡스톤에서 바꾸는 것

캡스톤 AI 서버 코드를 다시 읽고 찾은 문제와, 새 설계에서 피하는 방법입니다.

| 캡스톤에서 | 새 설계에서 |
| --- | --- |
| 두 서버가 DB 하나를 같이 쓰고, 레시피·좋아요·채팅 API가 양쪽에 겹쳤다. | DB 테이블마다 주인 서버를 하나로 두고, 웹은 Spring만 부른다. |
| 답변을 끝까지 모아 검증한 뒤 한 번에 보내서, 첫 글자까지 LLM 호출을 여러 번 차례로 기다렸다. | 답변을 만드는 대로 흘려보낸다. |
| 검색이 동기 호출이라, 워커 하나짜리 서버에서 한 사람의 검색이 다른 요청까지 막는 구조였다. | 입출력은 모두 비동기로 부르고, AI 서버는 따로 둔다. |
| 알레르기를 이름 문자열로 비교해서 "갑각류"는 아무것도 못 걸렀고, "게"는 "맛있게"에도 걸렸다. | 재료에 알레르기 코드를 붙이고 코드로 거른다. |
| 사용자와 AI 메시지를 답변이 끝난 뒤 한꺼번에 저장해서, 실패하면 질문까지 사라졌다. | 사용자 메시지를 먼저 저장하고, 답변 중인 방은 409로 막는다. |
| 평가 도구가 미완성이었다. | 평가 케이스를 구현보다 먼저 쓴다. |

## 로컬 실행

필요한 것: Java 21, Docker, [uv](https://docs.astral.sh/uv/), Node 22. 세 서버는 끝나지 않고 터미널을 잡고 있으니, 각각 새 터미널을 열어 레포 맨 위에서 시작합니다.

백엔드 (http://localhost:8080)

```bash
cd backend
[ -f .env ] || cp .env.example .env   # 처음 한 번(이미 있으면 덮어쓰지 않는다)
docker compose up -d --wait
./gradlew bootRun
```

AI 서버 (http://localhost:8000)

```bash
cd ai
uv run uvicorn naengo_ai.main:app --reload --port 8000
```

프론트엔드 (http://localhost:5173, `/api` 요청은 8080으로 넘깁니다)

```bash
cd frontend
npm ci
npm run dev
```

## 라이선스

코드는 [MIT](LICENSE)입니다. 냉고가 수집하는 레시피 원문과 사진은 원저작자에게 권리가 있어서, 이 레포에 올리지 않고 MIT도 적용되지 않습니다.
