# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is an IoT sensor data processing application built with:
- **Backend**: Spring Boot 3.3.2 with Kotlin (Java 21)
- **Database**: PostgreSQL with JPA/Hibernate
- **Message Queue**: RabbitMQ for sensor data processing
- **Frontend**: Server-side rendering with Thymeleaf templates
- **Additional Services**: Python microservices for data optimization and WebSocket handling

## Architecture

The system consists of multiple containerized services:

1. **web** (`/web`): Main Spring Boot application
   - REST controllers for device and chart management
   - WebSocket handlers for real-time telemetry and chart data
   - JPA entities and repositories for data persistence
   - RabbitMQ message consumers for sensor data processing
   - OAuth2 security configuration

2. **db** (`/db`): PostgreSQL database with initialization scripts
   - Schema files for devices, tokens, sensor data, and weights

3. **queue**: RabbitMQ service for message processing

4. **db_optimizer** (`/db_optimizer`): Python service for database optimization

5. **zero** (`/zero`): Python WebSocket service

## Key Components

### Spring Boot Application Structure
- **Controllers**: REST endpoints in `controllers/` package
- **Entities**: JPA entities in `entities/` package (Device, SensorData, Token, etc.)
- **Services**: Business logic with interface/implementation pattern
- **WebSocket**: Real-time communication handlers in `ws/` package
- **Queue**: RabbitMQ configuration and message consumers
- **Security**: OAuth2 and custom authentication

### WebSocket Endpoints
- `/ws/telemetry`: Authenticated telemetry data submission
- `/charts/{id}/realtime`: Real-time chart data streaming

## Development Commands

### Building and Running
```bash
# Run full stack with Docker Compose
docker-compose up

# Build just the web application (Maven)
cd web
./mvnw clean compile

# Run tests
cd web
./mvnw test

# Package application
cd web
./mvnw package
```

### Development Workflow
- Main application runs on port 8080
- Debug port 5005 is exposed for remote debugging
- PostgreSQL runs on port 5432
- Uses `.env` file for environment configuration

### Testing
- Uses JUnit 5 with Kotlin test support
- Mockito Kotlin for mocking
- Testcontainers for integration tests with RabbitMQ
- H2 database for test environment
- Test files mirror main package structure in `src/test/kotlin`

#### Testing Strategy and Conventions
- **Integration-heavy approach**: Prefer `@SpringBootTest` integration tests over isolated unit tests to verify component interactions in this Spring Boot application
- **Test isolation**: Uses `@Sql("/cleanup.sql")` to clear H2 database between tests and `@DirtiesContext` when tests modify bean state
- **Testcontainers constraints**: Some tests using Testcontainers are disabled to avoid Docker-in-Docker issues during image builds

## Database Schema
Key tables include:
- `devices`: IoT device registration and management
- `device_access_tokens`: Authentication tokens for devices
- `sensor_data`: Time-series sensor readings
- `sensor_data_weights`: Weighting factors for sensor data

## Environment Variables
Configure via `.env` file:
- Database credentials (`DB_USER`, `DB_PASSWORD`, `DB_NAME`)
- RabbitMQ credentials (`RABBIT_USER`, `RABBIT_PASSWORD`)
- OAuth2 and Stripe configurations handled via Spring profiles