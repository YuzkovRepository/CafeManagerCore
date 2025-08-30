# --- Этап сборки ---
FROM maven:3.9.2-amazoncorretto-21 AS build
WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests

# --- Этап запуска ---
FROM openjdk:21-jdk-slim
WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

ENV PORT=${PORT}

ENTRYPOINT ["java","-jar","app.jar"]