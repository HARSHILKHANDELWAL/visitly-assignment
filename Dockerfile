# ===========================
# 1️⃣ Build Stage
# ===========================
FROM maven:3.9.11-eclipse-temurin-21 AS builder

# Set working directory
WORKDIR /app

# Copy Maven files
COPY pom.xml mvnw ./
COPY .mvn .mvn

# Copy source
COPY src src

# Package the application
RUN mvn -B -DskipTests package


# ===========================
# 2️⃣ Runtime Stage (Tomcat 10)
# ===========================
FROM tomcat:10.1-jdk21-temurin


# Set Spring Environment Variables
ENV DB_URL=jdbc:postgresql://host.docker.internal:5432/visitly
ENV DB_USERNAME=postgres
ENV DB_PASSWORD=root
ENV SPRING_DATASOURCE_DRIVER_CLASS_NAME=org.postgresql.Driver

ENV JWT_SECRET=wqeqhudhddksjjdksdefwofsbvbfvufhh2e1212u3123u12321j3nj
ENV JWT_EXPIRATION_MS=86400000

# Clean default apps (optional but recommended)
RUN rm -rf /usr/local/tomcat/webapps/*

# Copy WAR file to Tomcat webapps folder
COPY --from=builder /app/target/*.war /usr/local/tomcat/webapps/visitly.war

# Expose Tomcat default port
EXPOSE 8080

# Start Tomcat
CMD ["catalina.sh", "run"]