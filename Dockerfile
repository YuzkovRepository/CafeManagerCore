# Этап сборки
FROM eclipse-temurin:21-jdk-jammy AS build
WORKDIR /app
COPY . .
RUN chmod +x mvnw && ./mvnw clean package -DskipTests

# Этап запуска
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
COPY --from=build /app/target/cafemanager-0.0.1-SNAPSHOT.jar app.jar
EXPOSE $PORT
ENTRYPOINT ["java","-jar","app.jar","--server.port=${PORT}"]