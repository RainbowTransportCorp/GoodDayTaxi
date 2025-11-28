# ============================
# 1) Build Stage
# ============================
FROM eclipse-temurin:17-jdk AS builder
WORKDIR /workspace

# --------------------------------
# 1. common-core 빌드 & publish
# --------------------------------
# GoodDayTaxi 루트가 build context 이므로 바로 common-core 접근 가능
COPY common-core /workspace/common-core
WORKDIR /workspace/common-core
RUN ./gradlew clean build publishToMavenLocal --no-daemon

# --------------------------------
# 2. Gateway 서비스 빌드
# --------------------------------
WORKDIR /workspace/app

COPY gateway/gradlew .
COPY gateway/gradle ./gradle
COPY gateway/build.gradle .
COPY gateway/settings.gradle .
COPY gateway/src ./src

RUN ./gradlew bootJar --no-daemon

# ============================
# 2) Run Stage
# ============================
FROM eclipse-temurin:17-jre
WORKDIR /app

COPY --from=builder /workspace/app/build/libs/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
