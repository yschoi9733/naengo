# frontend/CLAUDE.md

레포 맨 위 CLAUDE.md(서버 사이 규칙·게이트·Git·라벨)를 먼저 따른다. 여기는 `frontend/`에만 해당하는 규칙이다.

## 프로젝트

- **Naengo Frontend** — 냉고의 웹 화면. Spring 서버만 부른다.
- React 19 / TypeScript 7 / Vite 8 / Tailwind CSS 4 / shadcn/ui(`radix-vega`) / oxlint / npm, Node 22.23.2(`.nvmrc`). 명령은 모두 `frontend/`에서 친다.

## 구조

- 화면 구조(폴더·라우터)는 첫 화면 이슈에서 정하고 이 절에 적는다.
- `src/components/ui/`·`src/hooks/`·`src/lib/utils.ts`는 shadcn/ui가 만든 파일이다. `npx shadcn add <이름>`으로만 더하고 손으로 고치지 않는다(Prettier·oxlint·셀프 리뷰 대상도 아니다). `shadcn add`는 `src/index.css`도 고칠 수 있어서, 뒤에 `npm run format`을 돌린다.
- 서버는 `/api/v1/...` 경로로만 부른다. 서버 도메인을 코드·`VITE_` 환경변수에 두지 않는다(로컬은 Vite 프록시가 넘긴다).
- 비밀 값은 코드·`.env*`·`VITE_` 환경변수에 두지 않는다.

## 코딩 규칙

- 스타일은 Tailwind 클래스와 shadcn/ui 부품으로만 쓴다. `src/index.css` 밖에 CSS 파일·인라인 `style`을 만들지 않는다.
- 색은 `bg-primary`·`text-muted-foreground` 같은 테마 이름으로 쓴다. `bg-zinc-900` 같은 색 값은 쓰지 않는다(바꿀 땐 `src/index.css`의 CSS 변수).
- 서버 오류 본문의 `message`는 그대로 화면에 보여 준다. 화면 문구는 사용자가 읽을 한국어로 쓴다.
- 서식은 Prettier(`.prettierrc`)가 정한다. 고친 뒤 `npm run format`으로 맞춘다(마크다운은 대상이 아니다).
- import는 `@/` 별칭으로 쓴다.
- 테스트는 Vitest + Testing Library로 쓰고, 화면 요소는 사용자가 보는 문구·역할(`getByRole`·`getByText`)로 찾는다.
