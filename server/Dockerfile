# ============================
# 1) Build Stage
# ============================
FROM eclipse-temurin:17-jdk AS builder
WORKDIR /app

COPY gradlew .
COPY gradle ./gradle
COPY build.gradle .
COPY settings.gradle .
COPY src ./src

RUN chmod +x gradlew

# ============================
# 2) Run Stage
# ============================
FROM eclipse-temurin:17-jre
WORKDIR /app

# ★ application.yaml 복사
COPY src/main/resources/application.yaml ./application.yaml
COPY src/main/resources/secrets.yaml ./secrets.yaml

COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8761
ENTRYPOINT ["java", "-jar", "app.jar"]
