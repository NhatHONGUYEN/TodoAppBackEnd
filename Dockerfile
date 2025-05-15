# Étape 1 : on compile avec Maven
FROM maven:3.9.6-eclipse-temurin-17 AS builder
WORKDIR /workspace
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Étape 2 : on crée l’image finale plus légère
FROM eclipse-temurin:17-jdk-slim
WORKDIR /app
# On récupère le JAR compilé
COPY --from=builder /workspace/target/ToDoApp-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]