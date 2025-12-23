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
COPY m2 /home/gradle/.m2
RUN chown -R gradle:gradle /home/gradle/.m2

RUN chmod +x gradlew

# host의 ~/.m2 를 Gradle 컨테이너에 주입
RUN ./gradlew dependencies --no-daemon
RUN ./gradlew bootJar -x test --no-daemon

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