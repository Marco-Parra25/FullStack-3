# Use OpenJDK 21 as base image
FROM openjdk:21-jre-slim

# Set working directory
WORKDIR /app

# Copy jar file
COPY target/ms-api-gateway-*.jar app.jar

# Expose port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
