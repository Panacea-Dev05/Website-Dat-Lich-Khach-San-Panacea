# Dùng JDK 17
FROM openjdk:17-jdk-slim

# Copy file .jar vào container
COPY target/*.jar app.jar

# Chạy app
ENTRYPOINT ["java", "-jar", "/app.jar"]
