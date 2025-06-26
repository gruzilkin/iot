# syntax=docker/dockerfile:1

# Dependency stage
FROM maven:3-eclipse-temurin-21 as dependencies
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -DincludeScope=test

# Build stage
FROM maven:3-eclipse-temurin-21 as build
WORKDIR /app
COPY --from=dependencies /root/.m2 /root/.m2
COPY . .
RUN mvn package

# --- Downloader Stage ---
FROM alpine/curl:latest as downloader
ARG OTEL_AGENT_URL=https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/download/v2.17.0/opentelemetry-javaagent.jar
WORKDIR /otel-agent
RUN curl -L -o opentelemetry-javaagent.jar ${OTEL_AGENT_URL}

# Run stage
FROM eclipse-temurin:21-jre

COPY --from=downloader /otel-agent/opentelemetry-javaagent.jar /opentelemetry-javaagent.jar
COPY --from=build /app/target/iot-0.0.1-SNAPSHOT.jar /iot-app.jar

# OpenTelemetry Environment Variables
ENV OTEL_SERVICE_NAME=iot
ENV OTEL_TRACES_EXPORTER=otlp
ENV OTEL_METRICS_EXPORTER=otlp
ENV OTEL_LOGS_EXPORTER=otlp
# ENV OTEL_EXPORTER_OTLP_ENDPOINT is now expected to be set at runtime, e.g., in docker-compose.yaml
ENV OTEL_INSTRUMENTATION_SPRING_SCHEDULING_ENABLED=true
ENV OTEL_INSTRUMENTATION_MICROMETER_ENABLED=true
# Optional: If OTLP logs require specific protocol (grpc/http/protobuf)
# ENV OTEL_EXPORTER_OTLP_LOGS_PROTOCOL=http/protobuf

EXPOSE 8080
EXPOSE 5005
ENTRYPOINT ["java", "-javaagent:/opentelemetry-javaagent.jar", "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005", "-jar", "/iot-app.jar"]