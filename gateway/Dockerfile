# ============================
# 1) Build Stage
# ============================
FROM eclipse-temurin:17-jdk AS builder
WORKDIR /app

# context = ./gateway 이므로 gateway/ 제거
COPY gradlew .
COPY gradle ./gradle
COPY build.gradle .
COPY settings.gradle .
COPY src ./src

# Build jar
RUN chmod +x gradlew
RUN ./gradlew bootJar --no-daemon

# ============================
# 2) Run Stage
# ============================
FROM eclipse-temurin:17-jre
WORKDIR /app

# copy secrets.yaml (필수)
COPY src/main/resources/secrets.yaml ./secrets.yaml

COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
