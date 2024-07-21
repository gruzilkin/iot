# Build stage
FROM maven:3.8.4-openjdk-17 as build
WORKDIR /app
COPY . /app
RUN mvn clean package

# Run stage
FROM openjdk:17
COPY --from=build /app/target/iot-0.0.1-SNAPSHOT.jar /iot-app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/iot-app.jar"]