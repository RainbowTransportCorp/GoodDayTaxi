## ============================
## 1) Build Stage
## ============================
#FROM eclipse-temurin:17-jdk AS builder
#WORKDIR /app
#
## Gradle wrapper & project files
#COPY gradlew .
#COPY gradle ./gradle
#COPY build.gradle .
#COPY settings.gradle .
#COPY src ./src
#
## Build jar
#RUN ./gradlew bootJar --no-daemon
#
## ============================
## 2) Run Stage
## ============================
#FROM eclipse-temurin:17-jre
#WORKDIR /app
#
#COPY --from=builder /app/build/libs/*.jar app.jar
#
#EXPOSE 8761
#ENTRYPOINT ["java", "-jar", "app.jar"]
