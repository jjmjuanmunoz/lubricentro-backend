# Lubricentro Backend

Java 21 • Spring Boot 3 • PostgreSQL • Flyway • Spring Security (JWT) • Swagger (springdoc)

## Run (local)
1. Create DB `lubricentro_db` in PostgreSQL
2. Copy `src/main/resources/application-example.yml` to `application.yml` and fill credentials/secret
3. Start:
   ```bash
   ./mvnw spring-boot:run