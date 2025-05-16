# Étape 1 : Compilation avec Maven
FROM maven:3.9.6-eclipse-temurin-17 AS builder
WORKDIR /workspace

# Copier uniquement le POM d'abord pour mettre en cache les dépendances
COPY pom.xml .
RUN mvn dependency:go-offline

# Copier le code source et compiler
COPY src ./src
RUN mvn clean package -DskipTests

# Étape 2 : Image d'exécution légère
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app


# Copier le JAR compilé depuis l'étape précédente
COPY --from=builder /workspace/target/ToDoApp-0.0.1-SNAPSHOT.jar app.jar

# Configuration pour utiliser le profil de production
ENV SPRING_PROFILES_ACTIVE=prod


EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
