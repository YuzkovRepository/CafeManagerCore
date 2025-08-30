# --- Этап сборки ---
FROM amazoncorretto:21
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