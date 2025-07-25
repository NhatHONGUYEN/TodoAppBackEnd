# ToDoApp Backend

## Description

Backend de l'application ToDo développé avec Spring Boot. Cette API RESTful gère l'authentification, le stockage et la manipulation des tâches pour l'application de gestion de tâches.

## Technologies utilisées

- **Spring Boot**: Framework Java pour le développement d'applications
- **Spring Security**: Gestion de l'authentification et des autorisations
- **Spring Data JPA**: Couche d'accès aux données
- **Keycloak**: Gestion des identités et des accès
- **PostgreSQL**: Base de données relationnelle
- **Swagger/OpenAPI**: Documentation API
- **Maven**: Gestion des dépendances

## Prérequis

- Java 17+
- Maven
- PostgreSQL
- Keycloak

## Installation et démarrage

### Configuration de la base de données

1. Créer une base de données PostgreSQL
2. Configurer les paramètres de connexion dans `src/main/resources/application.properties`

### Configuration de Keycloak

1. Démarrer un serveur Keycloak
2. Créer un realm "todoapp"
3. Configurer un client avec les redirections appropriées
4. Configurer les paramètres Keycloak dans `application.properties`

### Démarrage de l'application

```bash
# Compiler le projet
mvn clean install

# Lancer l'application
mvn spring-boot:run
```

## Structure de l'API

L'API est organisée selon les principes REST avec les contrôleurs suivants:

- **TaskReadController**: Endpoints pour lire les tâches
- **TaskCreateController**: Endpoints pour créer des tâches
- **TaskUpdateController**: Endpoints pour mettre à jour les tâches
- **TaskDeleteController**: Endpoints pour supprimer des tâches

### Points d'entrée principaux

| Méthode | URL                   | Description                                 |
| ------- | --------------------- | ------------------------------------------- |
| GET     | /api/read             | Récupérer toutes les tâches avec pagination |
| GET     | /api/read/{id}        | Récupérer une tâche spécifique              |
| POST    | /api/create           | Créer une nouvelle tâche                    |
| PUT     | /api/update/{id}      | Mettre à jour une tâche existante           |
| PUT     | /api/update/{id}/done | Marquer une tâche comme terminée            |
| DELETE  | /api/delete/{id}      | Supprimer une tâche                         |

## Sécurité

L'application utilise Spring Security intégré avec Keycloak pour l'authentification et l'autorisation. Chaque utilisateur ne peut accéder qu'à ses propres tâches.

## Documentation API

La documentation Swagger est disponible à l'adresse `http://localhost:8080/swagger-ui.html` lorsque l'application est en cours d'exécution.
