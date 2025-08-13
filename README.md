# Stockflow Backend

## Features
- **Spring Boot 3.5.4**
- **Java 21**
- **PostgreSQL** integration
- **Flyway** for database migrations
- **Docker Compose** configuration for local DB setup

## Prerequisites
Make sure you have installed:
- [Java 21](https://adoptium.net/)
- [Gradle](https://gradle.org/) (or use the Gradle Wrapper)
- [Docker](https://www.docker.com/)
- [Docker Compose](https://docs.docker.com/compose/)

## Getting Started

### 1. Configure Environment Variables
Copy the ```.env.example``` file and rename it to ```.env```

### 2. Start the local database
```bash
docker-compose -f infra/dev/docker-compose.yml up -d
```
This will start PostgreSQL for local development.

### 3. Run the application
```bash
./gradlew bootRun
```
The API will be available at:
```
http://localhost:8080
```

### 4. Health Check
Spring Boot Actuator is included, so you can verify the service status at:
```
http://localhost:8080/actuator/health
```