FROM openjdk:17-jdk-slim

WORKDIR /app

# Copy the Maven wrapper and pom file first for better caching
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Download dependencies
RUN ./mvnw dependency:go-offline -B

# Copy source code
COPY src src

# Build the application
RUN ./mvnw clean package -DskipTests

# Run the application
EXPOSE 8082
CMD ["java", "-jar", "target/streamer-order-consumer-service-0.0.1-SNAPSHOT.jar"]
