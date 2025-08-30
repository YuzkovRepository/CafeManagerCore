# Этап сборки
FROM maven:3.9.2-amazoncorretto AS build
WORKDIR /app

# Копируем файлы проекта
COPY pom.xml .
COPY src ./src

# Собираем jar
RUN mvn clean package -DskipTests

# Этап запуска
FROM openjdk:21-jdk-slim
WORKDIR /app

# Копируем собранный jar
COPY --from=build /app/target/*.jar app.jar

# Устанавливаем переменную окружения для порта
ENV PORT=${PORT}

# Запускаем приложение
ENTRYPOINT ["java", "-jar", "app.jar"]