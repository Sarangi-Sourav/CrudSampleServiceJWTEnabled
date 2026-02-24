# Build stage 1
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

# Copy pom.xml and download dependencies ( cached layer )
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code and build
COPY src ./src
RUN mvn clean package -DskipTests

# Runtime stage 2
# Base image with Java runtime
FROM eclipse-temurin:21-jre-alpine

# Create a non-root user for security
# Running as root in containers is a security risk
RUN addgroup -S spring && adduser -S spring -G spring

# Set working directory
WORKDIR /app

# Copy the JAR file from the local build
# COPY target/*.jar app.jar

# Copy the jar from the build stage i.e. stage 1
COPY --from=build /app/target/*.jar app.jar

# Change ownership to non-root user
RUN chown -R spring:spring app.jar

# Switch to non-root user
USER spring:spring

# Expose port 8080
# This is documentation - doesn't actually open the port
# Kubernetes will handle actual port mapping
EXPOSE 8080

# Add profile environment variable
ENV SPRING_PROFILES_ACTIVE=docker

# Health check (optional but recommended)
# Kubernetes can use this, or use its own probes
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
  CMD wget --quiet --tries=1 --spider http://localhost:8080/health || exit 1

# Run the application
# -Djava.security.egd=file:/dev/./urandom = faster startup (entropy source)
# -XX:+UseContainerSupport = JVM respects container memory limits
ENTRYPOINT ["java", \
            "-Djava.security.egd=file:/dev/./urandom", \
            "-XX:+UseContainerSupport", \
            "-XX:MaxRAMPercentage=75.0", \
            "-jar", \
            "/app/app.jar"]
