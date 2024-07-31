# syntax=docker/dockerfile:1

# Dependency stage
FROM maven:3-eclipse-temurin-17 as dependencies
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -DincludeScope=test

# Build stage
FROM maven:3-eclipse-temurin-17 as build
WORKDIR /app
COPY --from=dependencies /root/.m2 /root/.m2
COPY . .
RUN mvn package

# Run stage
FROM eclipse-temurin:17-jre
COPY --from=build /app/target/iot-0.0.1-SNAPSHOT.jar /iot-app.jar
EXPOSE 8080
EXPOSE 5005
ENTRYPOINT ["java", "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005", "-jar", "/iot-app.jar"]