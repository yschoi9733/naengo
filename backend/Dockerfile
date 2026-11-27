# syntax=docker/dockerfile:1

# jar는 이미지 안에서 빌드한다(클론만 한 상태에서도 이미지가 만들어지게). 테스트는 backend CI가 돌리니 여기서는 돌리지 않는다
FROM eclipse-temurin:21-jdk AS build
WORKDIR /src
# 실행 권한이 빠진 체크아웃(zip으로 받은 소스 등)에서도 돌게 권한을 붙여 복사한다
COPY --chmod=755 gradlew ./
COPY settings.gradle build.gradle ./
COPY gradle gradle
COPY src src
# Gradle 배포판과 의존성은 빌드 캐시에 두어 다시 빌드할 때 받지 않는다. 컨테이너 사이에서는 Gradle의 캐시 잠금이 풀리지
# 않아(동시 빌드가 잠금을 기다리다 실패한다) 한 번에 한 빌드만 캐시를 쓰게 한다
RUN --mount=type=cache,target=/root/.gradle,sharing=locked ./gradlew --no-daemon bootJar

FROM eclipse-temurin:21-jre
# compose의 healthcheck가 curl을 쓴다. 지금은 베이스 이미지에 들어 있지만, 빠져도 healthcheck가 깨지지 않게 직접 설치한다
RUN apt-get update \
    && apt-get install -y --no-install-recommends curl \
    && rm -rf /var/lib/apt/lists/*
RUN groupadd --system naengo && useradd --system --gid naengo --create-home naengo
WORKDIR /app
COPY --from=build /src/build/libs/*.jar app.jar
USER naengo
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
