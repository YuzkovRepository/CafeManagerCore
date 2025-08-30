FROM openjdk:21-jdk-slim
WORKDIR /app
COPY target/cafemanager-0.0.1-SNAPSHOT.jar app.jar
ENV PORT=${PORT}
ENTRYPOINT ["java","-jar","app.jar"]

