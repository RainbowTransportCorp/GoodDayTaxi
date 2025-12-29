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
#COPY m2 /home/gradle/.m2
#RUN chown -R gradle:gradle /home/gradle/.m2

# 🔑 로컬 Maven repo를 컨테이너로 복사
COPY m2 /home/gradle/.m2

# host의 ~/.m2 를 Gradle 컨테이너에 주입
#RUN ./gradlew dependencies --no-daemon
RUN chmod +x gradlew
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

ENTRYPOINT ["java", "-jar", "/app/app.jar"]