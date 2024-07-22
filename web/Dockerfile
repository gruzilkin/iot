# syntax=docker/dockerfile:1

# Dependency stage
FROM maven:3.8.4-openjdk-17 as dependencies
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline

# Build stage
FROM maven:3.8.4-openjdk-17 as build
WORKDIR /app
COPY --from=dependencies /root/.m2 /root/.m2
COPY . .
RUN mvn clean package

# Run stage
FROM openjdk:17
COPY --from=build /app/target/iot-0.0.1-SNAPSHOT.jar /iot-app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/iot-app.jar"]