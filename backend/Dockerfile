# syntax=docker/dockerfile:1

# jar는 이미지 안에서 빌드한다(클론만 한 상태에서도 이미지가 만들어지게). 테스트는 backend CI가 돌리니 여기서는 돌리지 않는다
FROM eclipse-temurin:21-jdk AS build
WORKDIR /src
COPY gradlew settings.gradle build.gradle ./
COPY gradle gradle
COPY src src
# Gradle 배포판과 의존성은 빌드 캐시에 두어 다시 빌드할 때 받지 않는다
RUN --mount=type=cache,target=/root/.gradle ./gradlew --no-daemon bootJar

# 실행 이미지에는 JRE와 jar만 둔다. compose의 healthcheck가 이 이미지에 든 curl을 쓴다
FROM eclipse-temurin:21-jre
RUN groupadd --system naengo && useradd --system --gid naengo --no-create-home naengo
WORKDIR /app
COPY --from=build /src/build/libs/*.jar app.jar
USER naengo
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
