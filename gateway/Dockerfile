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
# mavenLocal/cache 오염을 무시하고 dependencies를 강제로 다시 다운로드함
RUN ./gradlew clean --refresh-dependencies bootJar --no-daemon

# ============================
# 2) Run Stage
# ============================
FROM eclipse-temurin:17-jre
WORKDIR /app

# application.yaml 복사
COPY src/main/resources/application.yaml ./application.yaml
COPY src/main/resources/secrets.yaml ./secrets.yaml

# jar 복사
COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
