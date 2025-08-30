# Используем OpenJDK 17
FROM openjdk:17-jdk-slim

WORKDIR /app

# Копируем готовый jar
COPY target/cafemanager-0.0.1-SNAPSHOT.jar app.jar

# Переменная порта
ENV PORT=${PORT}

# Запуск приложения
ENTRYPOINT ["java","-jar","app.jar"]
