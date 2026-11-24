# backend/CLAUDE.md

레포 맨 위 CLAUDE.md(서버 사이 규칙·게이트·Git·라벨)를 먼저 따른다. 여기는 `backend/`에만 해당하는 규칙이다.

## 프로젝트

- **Naengo Backend** — 냉고의 본 서버. 웹이 부르는 API를 모두 맡고, AI 기능은 Python AI 서버(`ai/`)에 내부 HTTP로 넘긴다.
- Spring Boot 4.1.2 / Java 21 / Gradle 래퍼 / MySQL 8.4. 기본 패키지 `com.naengo`. 명령은 모두 `backend/`에서 친다.

## 아키텍처: 모듈러 모놀리스

- 모듈 = 패키지. 정해 둔 모듈은 `member`(사용자·프로필·로그인) · `fridge`(냉장고 재고) · `ingredient`(재료 표준 사전·알레르기 코드) · `recipe`(레시피 원본·좋아요·스크랩) · `chat`(채팅방·메시지, AI 서버 호출과 답변 중계) · `common`(오류 응답·보안처럼 모듈이 함께 쓰는 것)이다. 모듈이나 DB 테이블을 더하면 `docs/architecture.md`의 테이블 주인도 함께 고친다.
- 모듈 안은 Controller → Service → Repository, 업무 규칙은 엔티티(도메인 객체)에 둔다.
- 웹이 부르는 API 주소는 모두 `/api/v1/...`로 시작한다.
- 다른 모듈의 기능은 그 모듈이 공개한 Service·인터페이스로만 부른다. 다른 모듈의 Repository는 직접 부르지 않는다.
- 다른 모듈 엔티티의 상태 변경은 그 모듈의 Service를 거친다. 상태를 바꾸지 않는 확인·계산 메소드와 열거형은 직접 써도 된다.

## 코딩 규칙

- Lombok은 `@Getter`·`@NoArgsConstructor(access = AccessLevel.PROTECTED)`·`@RequiredArgsConstructor`·`@Slf4j`·`@Builder`만 쓴다. `@Builder`는 클래스가 아니라 생성자에 붙인다. 엔티티에 `@Setter`·`@Data` 금지.
- 요청·응답 DTO는 `record`로 쓴다.
- 오류 응답 본문은 `{"message": "..."}` 하나로 맞춘다. 문구는 화면에 그대로 보이니 사용자가 읽을 말로 쓰고, 검증 애너테이션에도 `message`를 적는다.
- DB 스키마는 Flyway(`src/main/resources/db/migration/V<번호>__<설명>.sql`)로만 바꾼다. 머지된 마이그레이션은 고치지 않고 새 번호로 더한다(`ddl-auto: validate`).
- "오늘"과 현재 시각은 주입한 `Clock`(`ClockConfig`, Asia/Seoul)에서만 얻는다. 인자 없는 `now()`(`LocalDate.now()`·`Instant.now()` 등) 직접 호출 금지, `@Scheduled`에는 `zone = "Asia/Seoul"`.
- 시각 값은 `Instant`, 날짜만이면 `LocalDate`로 두고 `LocalDateTime`으로 시각을 저장하지 않는다.
- AI 서버가 느리거나 죽어도 AI 채팅 밖의 API(로그인·목록·좋아요·재고 등)는 영향을 받지 않아야 한다. AI 서버 호출에는 연결 제한 시간, 읽기 제한 시간(스트림 조각 사이 대기), 동시 처리 수 제한을 건다.
- 도메인 규칙은 스프링 없는 단위 테스트로, DB가 필요한 테스트는 `@Testcontainers(disabledWithoutDocker = true)`와 `@Import(TestcontainersConfiguration.class)`를 함께 붙인다. `@Disabled`로 끈 테스트를 남기지 않는다(CI는 건너뛴 테스트가 있으면 실패한다).
