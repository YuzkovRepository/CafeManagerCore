# Используем официальный образ OpenJDK 17
FROM openjdk:17-jdk-slim

# Указываем рабочую директорию в контейнере
WORKDIR /app

# Копируем файлы проекта в контейнер
COPY target/*.jar app.jar

# Указываем переменную окружения для порта
ENV PORT=8080

# Команда для запуска Spring Boot приложения
ENTRYPOINT ["java", "-jar", "app.jar"]