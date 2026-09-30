# Build stage with pre-installed Maven & Java 17
FROM maven:3.9-eclipse-temurin-17 AS builder
WORKDIR /build

# Cache dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B || true

# Copy source code and build production jar
COPY src ./src
RUN mvn clean package -DskipTests

# Lightweight runtime stage
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=builder /build/target/military-asset-management-backend-0.0.1-SNAPSHOT.jar app.jar

ENV PORT=8080
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
