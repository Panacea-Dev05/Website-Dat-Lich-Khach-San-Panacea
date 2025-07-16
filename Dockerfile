# Step 1: Build ứng dụng với Maven
FROM maven:3.8.5-openjdk-17 AS builder
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Step 2: Chạy ứng dụng từ file JAR vừa build
FROM openjdk:17-jdk-slim
# Cập nhật dòng COPY này để chỉ định chính xác tên file JAR
COPY --from=builder /app/target/Website_Dat_Lich_Khach_San-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]