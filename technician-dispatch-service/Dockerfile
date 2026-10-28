# ==========================================
# Multi-stage Dockerfile for technician-dispatch-service
# ==========================================

# Stage 1: Build application with Maven and Eclipse Temurin JDK 21
FROM eclipse-temurin:21-jdk-alpine AS builder

WORKDIR /build

# Copy Maven wrapper and POM for dependency layer caching
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN ./mvnw dependency:go-offline -B

# Copy application source code and compile production JAR
COPY src/ src/
RUN ./mvnw clean package -DskipTests -B

# Stage 2: Minimal runtime image with JRE 21
FROM eclipse-temurin:21-jre-alpine AS runner

WORKDIR /app

# Create non-root user for security best practices
RUN addgroup -S helpflow && adduser -S helpflow -G helpflow
USER helpflow

# Copy compiled JAR from builder stage
COPY --from=builder /build/target/*.jar app.jar

EXPOSE 8081

ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar"]
