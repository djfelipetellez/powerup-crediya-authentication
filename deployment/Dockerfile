FROM eclipse-temurin:21-jdk-alpine

# Install curl for health checks
RUN apk add --no-cache curl

# Create a non-root user
RUN addgroup -g 1001 -S appgroup && \
    adduser -u 1001 -S appuser -G appgroup

WORKDIR /app

# Copy gradle wrapper and make it executable
COPY gradlew gradlew.bat ./
COPY gradle/ gradle/
RUN chmod +x gradlew

# Copy build configuration files for dependency caching
COPY build.gradle main.gradle settings.gradle gradle.properties lombok.config ./

# Download dependencies (cache layer)
RUN ./gradlew dependencies --no-daemon || true

# Copy source code and build
COPY . .
RUN ./gradlew bootJar --no-daemon -x validateStructure

# Create necessary directories and set permissions
RUN mkdir -p /tmp /cache && \
    chown -R appuser:appgroup /app /tmp /cache

# Set environment variables
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=70 -Djava.security.egd=file:/dev/./urandom -XX:+UseG1GC -XX:+UseStringDeduplication"

VOLUME /tmp

# Switch to non-root user
USER appuser

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar applications/app-service/build/libs/*.jar"]
