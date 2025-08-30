# --- Этап сборки ---
FROM maven:3.9.2-eclipse-temurin-21 AS build
WORKDIR /app

# Копируем только pom.xml сначала для кэша зависимостей
COPY pom.xml .

# Копируем исходники
COPY src ./src

# Собираем jar (без тестов)
RUN mvn clean package -DskipTests

# --- Этап запуска ---
FROM openjdk:21-jdk-slim
WORKDIR /app

# Копируем jar из этапа сборки
COPY --from=build /app/target/*.jar app.jar

# Переменная порта, Render задаёт автоматически
ENV PORT=${PORT}

# Запуск приложения
ENTRYPOINT ["java","-jar","app.jar"]