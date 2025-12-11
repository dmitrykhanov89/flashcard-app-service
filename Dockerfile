# -------- STAGE 1: Build --------
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app

COPY . .

RUN ./gradlew clean build -x test

# -------- STAGE 2: Run --------
FROM eclipse-temurin:21-jre
WORKDIR /app

COPY --from=build /app/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]

# запуск
# docker build -t flashcard-backend .
# docker run -p 8080:8080 --name flashcard-backend-container flashcard-backend