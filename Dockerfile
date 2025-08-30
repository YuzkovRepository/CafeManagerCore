# Этап сборки
FROM eclipse-temurin:21-jdk-jammy AS build
WORKDIR /app
COPY . .
RUN chmod +x mvnw && ./mvnw clean package -DskipTests

RUN ls -la /app/target/

# Этап запуска
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

EXPOSE $PORT
ENTRYPOINT ["java","-jar","app.jar","--server.port=${PORT}"]