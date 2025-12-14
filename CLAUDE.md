# CLAUDE.md - Arcana Project

## Project Description
Argentinian electronic billing system with AFIP integration. Multi-module Maven project with Java 21 and Spring Boot 3.1.4.

## Project Structure
```
arcana/
├── pom.xml                      # Parent POM (packaging: pom)
├── afip-integration/            # AFIP integration module
│   ├── pom.xml
│   └── src/main/resources/wsdl/ # AFIP WSDLs (wsaa.wsdl, wsfev1.wsdl)
└── backend/                     # Spring Boot REST API
    └── pom.xml
```

## Technology Stack

### Backend
- **Java 21** (LTS)
- **Spring Boot 3.1.4**
    - spring-boot-starter-web
    - spring-boot-starter-data-jpa
    - spring-boot-starter-security
- **PostgreSQL** (driver 42.7.7)
- **Flyway 9.22.3** for migrations
- **Lombok 1.18.30**
- **JWT** (jjwt 0.11.5)
- **SpringDoc OpenAPI 2.5.0** (Swagger UI)

### AFIP Integration
- **Apache CXF 4.0.5** for SOAP clients
- Services:
    - **WSAA** - Authentication (Access Login Ticket)
    - **WSFEv1** - Electronic billing

## Important Maven Commands

```bash
# Full build
mvn clean install

# Compile only without tests
mvn clean compile -DskipTests

# Generate AFIP stubs from WSDL (run when WSDLs change)
mvn generate-sources -Pcodegen-ws -pl afip-integration

# Run backend
mvn spring-boot:run -pl backend

# Tests
mvn test
```

## Code Conventions

### Package Structure
```
com.arcana.backend
├── config/          # Spring configurations
├── controller/      # REST Controllers
├── service/         # Business logic
├── repository/      # JPA Repositories
├── entity/          # JPA Entities
├── dto/             # Data Transfer Objects
├── security/        # JWT, filters, auth
└── exception/       # Custom exceptions and handlers

ar.com.afip
├── wsaa/            # WSAA generated classes
└── wsfev1/          # WSFEv1 generated classes
```

### Code Style
- Use Lombok: `@Data`, `@Builder`, `@RequiredArgsConstructor`
- DTOs with Java 21 `record` when appropriate
- Controllers return `ResponseEntity<T>`
- Services use constructor injection (no `@Autowired` on fields)
- Spanish names for AFIP domain entities (e.g., `Comprobante`, `PuntoVenta`)
- English names for the rest of the code

### Database
- Migrations in `backend/src/main/resources/db/migration/`
- Format: `V{number}__{description}.sql`
- Example: `V001__create_users_table.sql`

## AFIP Configuration

### Environments
- **Homologation**: For development and testing
    - WSAA: `https://wsaahomo.afip.gov.ar/ws/services/LoginCms`
    - WSFEv1: `https://wswhomo.afip.gov.ar/wsfev1/service.asmx`
- **Production**: For production environment only
    - WSAA: `https://wsaa.afip.gov.ar/ws/services/LoginCms`
    - WSFEv1: `https://servicios1.afip.gov.ar/wsfev1/service.asmx`

### Certificates
- Digital certificate (.p12 or .pfx) issued by AFIP is required
- Configure in `application.yml`:
  ```yaml
  afip:
    cuit: "20XXXXXXXX3"
    cert-path: "classpath:certs/certificado.p12"
    cert-password: "${AFIP_CERT_PASSWORD}"
    environment: homologacion  # or produccion
  ```

## Tests
- Unit tests with JUnit 5
- Location: `src/test/java/`
- Naming: `*Test.java` for unit tests, `*IT.java` for integration tests

## Configuration Files
- `backend/src/main/resources/application.yml` - Main config
- `backend/src/main/resources/application-dev.yml` - Development config
- `backend/src/main/resources/application-prod.yml` - Production config

## Important Notes
1. **DO NOT commit** certificates or passwords to the repo
2. Use environment variables for secrets: `AFIP_CERT_PASSWORD`, `DB_PASSWORD`, `JWT_SECRET`
3. AFIP stubs (`target/generated-sources/cxf`) are in `.gitignore` - regenerate with `codegen-ws` profile
4. The `<n>` tag in backend/pom.xml should be `<name>` (there's a typo)

## Docker (if applicable)
```bash
# Start local PostgreSQL
docker run -d --name arcana-postgres \
  -e POSTGRES_DB=arcana \
  -e POSTGRES_USER=arcana \
  -e POSTGRES_PASSWORD=arcana123 \
  -p 5432:5432 \
  postgres:16

# Connect
psql -h localhost -U arcana -d arcana
```