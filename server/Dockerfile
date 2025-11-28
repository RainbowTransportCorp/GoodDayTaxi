# ============================
# 1) Build Stage
# ============================
FROM eclipse-temurin:17-jdk AS builder
WORKDIR /app

# Copy files from server folder because context = project root
COPY server/gradlew .
COPY server/gradle ./gradle
COPY server/build.gradle .
COPY server/settings.gradle .
COPY server/src ./src

# Build jar
RUN chmod +x gradlew
RUN ./gradlew bootJar --no-daemon

# ============================
# 2) Run Stage
# ============================
FROM eclipse-temurin:17-jre
WORKDIR /app

COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8761
ENTRYPOINT ["java", "-jar", "app.jar"]
