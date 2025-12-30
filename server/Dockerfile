# syntax=docker/dockerfile:1.7

# ============================
# 1) Build Stage
# ============================
FROM gradle:8.7-jdk17 AS builder
WORKDIR /workspace

COPY gradlew gradlew
COPY gradle gradle
COPY build.gradle settings.gradle ./
COPY src src

RUN chmod +x gradlew
RUN --mount=type=cache,target=/home/gradle/.gradle \
    ./gradlew clean bootJar -x test

# ============================
# 2) Run Stage
# ============================
FROM eclipse-temurin:17-jre
WORKDIR /app

COPY --from=builder /workspace/build/libs/*.jar app.jar

# Eureka 기본 포트
EXPOSE 8761

ENV JAVA_OPTS=""

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]