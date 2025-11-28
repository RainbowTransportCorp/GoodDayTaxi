# ============================
# 1) Build Stage
# ============================
FROM eclipse-temurin:17-jdk AS builder
WORKDIR /app

# Gateway 프로젝트 복사
COPY gateway/gradlew .
COPY gateway/gradle ./gradle
COPY gateway/build.gradle .
COPY gateway/settings.gradle .
COPY gateway/src ./src

# Build jar
RUN chmod +x gradlew
RUN ./gradlew bootJar --no-daemon

# ============================
# 2) Run Stage
# ============================
FROM eclipse-temurin:17-jre
WORKDIR /app

COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
