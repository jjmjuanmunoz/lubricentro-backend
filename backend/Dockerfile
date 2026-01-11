# Use Eclipse Temurin 21 as base image (more stable than OpenJDK)
FROM eclipse-temurin:21-jdk as builder

# Set working directory
WORKDIR /app

# Copy Maven wrapper from backend
COPY backend/mvnw .
COPY backend/.mvn .mvn

# Copy parent POM first (required for multi-module project)
COPY pom.xml .

# Copy afip-integration module (dependency of backend)
COPY afip-integration/pom.xml ./afip-integration/
COPY afip-integration/src ./afip-integration/src

# Copy backend POM and source code
COPY backend/pom.xml ./backend/
COPY backend/src ./backend/src

# Make mvnw executable
RUN chmod +x mvnw

# Install parent POM first (needed for child modules)
RUN ./mvnw install -N -B

# Install afip-integration module (dependency of backend)
RUN ./mvnw install -pl afip-integration -am -B

# Build the backend application
# The spring-boot-maven-plugin is now configured to execute repackage during package phase
RUN ./mvnw clean package -pl backend -am -DskipTests

# List JARs for debugging
RUN echo "=== JARs in backend/target ===" && \
    ls -lh /app/backend/target/*.jar 2>/dev/null || echo "No JARs found"

# Find and copy the executable JAR
# Spring Boot Maven plugin creates an executable JAR that replaces the original JAR
# We need to find the JAR without -sources or -javadoc classifier
RUN JAR_FILE=$(find /app/backend/target -maxdepth 1 -name "backend-*.jar" ! -name "*-sources.jar" ! -name "*-javadoc.jar" | head -1) && \
    if [ -z "$JAR_FILE" ]; then \
        echo "ERROR: No executable JAR found!" && \
        echo "Contents of target directory:" && \
        ls -la /app/backend/target/ && \
        exit 1; \
    fi && \
    echo "Found JAR: $JAR_FILE" && \
    echo "Verifying JAR manifest:" && \
    (unzip -p "$JAR_FILE" META-INF/MANIFEST.MF 2>/dev/null | grep -i "main-class" || echo "WARNING: Main-Class not found in manifest") && \
    cp "$JAR_FILE" /app/app.jar && \
    echo "JAR copied successfully to /app/app.jar"

# Create a new stage for runtime
FROM eclipse-temurin:21-jre

# Set working directory
WORKDIR /app

# Copy the built JAR from the build stage
COPY --from=builder /app/app.jar app.jar

# Expose port
EXPOSE 8080

# Set JVM options for better performance in containers
ENV JAVA_OPTS="-Xmx512m -Xms256m"

# Run the application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
