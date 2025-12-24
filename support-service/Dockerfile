# syntax=docker/dockerfile:1.7

FROM gradle:8.7-jdk17 AS builder
WORKDIR /workspace

COPY gradlew gradlew
COPY gradle gradle
COPY build.gradle settings.gradle ./
COPY src src

RUN chmod +x gradlew

# ✅ 핵심: 로컬 ~/.m2 + gradle cache 마운트
RUN --mount=type=bind,source=$HOME/.m2,target=/home/gradle/.m2,readonly \
    --mount=type=cache,target=/home/gradle/.gradle \
    ./gradlew clean bootJar -x test --no-daemon

FROM eclipse-temurin:17-jre
WORKDIR /app

COPY --from=builder /workspace/build/libs/*.jar app.jar

EXPOSE 8080
ENV JAVA_OPTS=""

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]
