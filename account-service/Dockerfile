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
RUN --mount=type=bind,source=/home/ubuntu/.m2,target=/home/gradle/.m2,readonly \
    --mount=type=cache,target=/home/gradle/.gradle \
    ./gradlew clean bootJar -x test

# ============================
# 2) Run Stage
# ============================
FROM eclipse-temurin:17-jre AS runtime
WORKDIR /app

COPY --from=builder /workspace/build/libs/*.jar app.jar

# 스프링 기본 8080
EXPOSE 8080

# 필요하면 compose에서 JAVA_OPTS로 넣기
ENV JAVA_OPTS=""

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]