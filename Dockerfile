# Stage 1: Build the application using Maven
FROM maven:3.9.8-eclipse-temurin-22-alpine AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Run the application using OpenJDK
FROM openjdk:22-jdk
WORKDIR /app
COPY --from=build /app/target/BloggingApp-0.0.1-SNAPSHOT.jar BloggingApp.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "BloggingApp.jar"]
