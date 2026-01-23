# CampusMaster - Campus Management System

Une application de gestion de campus universitaire construite avec Spring Boot et suivant les meilleures pratiques de l'architecture Clean Architecture, SOLID et Domain-Driven Design (DDD).

## 🚀 Démarrage rapide

**Nouveau ici ?** Commencez par [START_HERE.md](START_HERE.md) pour démarrer l'application en 2 minutes avec H2 Database !

```bash
# Windows
start-h2.bat

# Linux/Mac
./start-h2.sh
```

Puis accédez à : http://localhost:8080/api/swagger-ui.html

---

## Table des matières

- [Architecture](#architecture)
- [Technologies](#technologies)
- [Prérequis](#prérequis)
- [Installation](#installation)
- [Configuration](#configuration)
- [Lancement](#lancement)
- [Documentation API](#documentation-api)
- [Structure du projet](#structure-du-projet)
- [Bonnes pratiques](#bonnes-pratiques)
- [Tests](#tests)
- [Déploiement](#déploiement)

## Architecture

Ce projet suit une architecture Clean Architecture en couches :

```
┌─────────────────────────────────────────┐
│          Web Layer (Controllers)        │  ← Présentation
├─────────────────────────────────────────┤
│     Application Layer (Use Cases)       │  ← Logique applicative
├─────────────────────────────────────────┤
│       Domain Layer (Entities)           │  ← Logique métier
├─────────────────────────────────────────┤
│   Infrastructure Layer (Persistence)    │  ← Accès aux données
└─────────────────────────────────────────┘
```

### Principes appliqués

- **Clean Architecture** : Séparation claire des responsabilités
- **SOLID** : Tous les principes SOLID sont respectés
- **DDD** : Domain-Driven Design avec entités, value objects, repositories
- **Separation of Concerns** : Chaque couche a sa responsabilité
- **Dependency Inversion** : Les dépendances pointent vers les abstractions

## Technologies

### Core
- **Java 17**
- **Spring Boot 4.0.0**
- **Spring Data JPA**
- **Spring Security**
- **PostgreSQL**

### Librairies
- **Lombok** : Réduction du code boilerplate
- **MapStruct** : Mapping automatique entre DTOs et entités
- **JWT (JJWT)** : Authentification par tokens
- **SpringDoc OpenAPI** : Documentation API automatique (Swagger)
- **Hibernate Validator** : Validation des données

### Outils de développement
- **Spring Boot DevTools** : Hot reload
- **H2 Database** : Base de données en mémoire pour les tests

## Prérequis

- Java 17 ou supérieur
- Maven 3.6+
- PostgreSQL 14+
- Un IDE (IntelliJ IDEA, Eclipse, VS Code)

## Installation

### 1. Cloner le projet

```bash
git clone <repository-url>
cd CampusMaster
```

### 2. Configurer la base de données

Créer une base de données PostgreSQL :

```sql
CREATE DATABASE campusmaster;
CREATE DATABASE campusmaster_dev;
```

### 3. Configurer les variables d'environnement

Copier le fichier `.env.example` en `.env` et modifier les valeurs :

```bash
cp .env.example .env
```

### 4. Installer les dépendances

```bash
mvn clean install
```

## Configuration

Le projet utilise des profils Spring pour différents environnements :

- **dev** : Développement (par défaut)
- **test** : Tests automatisés
- **prod** : Production

### Fichiers de configuration

- `application.yml` : Configuration principale
- `application-dev.yml` : Configuration développement
- `application-test.yml` : Configuration tests
- `application-prod.yml` : Configuration production

### Variables d'environnement importantes

```properties
# Base de données
DATABASE_URL=jdbc:postgresql://localhost:5432/campusmaster
DATABASE_USERNAME=postgres
DATABASE_PASSWORD=postgres

# JWT
JWT_SECRET=your-secret-key
JWT_EXPIRATION=86400000

# CORS
CORS_ALLOWED_ORIGINS=http://localhost:3000,http://localhost:4200
```

## Lancement

### Mode développement

```bash
mvn spring-boot:run
```

Ou avec un profil spécifique :

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

### Accès à l'application

- **API** : http://localhost:8080/api
- **Swagger UI** : http://localhost:8080/api/swagger-ui.html
- **API Docs** : http://localhost:8080/api/v3/api-docs

## Documentation API

La documentation API est générée automatiquement avec SpringDoc OpenAPI.

Accédez à Swagger UI : http://localhost:8080/api/swagger-ui.html

### Endpoints principaux

#### Users
- `POST /api/v1/users` - Créer un utilisateur
- `GET /api/v1/users/{id}` - Récupérer un utilisateur
- `GET /api/v1/users` - Lister tous les utilisateurs (paginé)
- `PUT /api/v1/users/{id}` - Mettre à jour un utilisateur
- `DELETE /api/v1/users/{id}` - Supprimer un utilisateur (soft delete)
- `GET /api/v1/users/search?keyword=...` - Rechercher des utilisateurs
- `PATCH /api/v1/users/{id}/activate` - Activer un utilisateur
- `PATCH /api/v1/users/{id}/deactivate` - Désactiver un utilisateur
- `PATCH /api/v1/users/{id}/suspend` - Suspendre un utilisateur

## Structure du projet

```
com.elzocodeur.campusmaster/
├── domain/                          # Couche Domain
│   ├── entity/                      # Entités JPA
│   │   ├── BaseEntity.java          # Entité de base avec audit
│   │   └── User.java                # Entité User
│   ├── enums/                       # Énumérations
│   │   ├── UserRole.java
│   │   └── UserStatus.java
│   └── exception/                   # Exceptions métier
│       ├── BusinessException.java
│       ├── business/                # Exceptions business
│       │   ├── ResourceNotFoundException.java
│       │   ├── ResourceAlreadyExistsException.java
│       │   └── ValidationException.java
│       └── technical/               # Exceptions techniques
│           ├── DatabaseException.java
│           └── ExternalServiceException.java
│
├── application/                     # Couche Application
│   ├── dto/                         # Data Transfer Objects
│   │   ├── request/                 # DTOs de requête
│   │   │   ├── CreateUserRequest.java
│   │   │   └── UpdateUserRequest.java
│   │   └── response/                # DTOs de réponse
│   │       ├── UserResponse.java
│   │       ├── ApiResponse.java
│   │       ├── ErrorResponse.java
│   │       └── PageResponse.java
│   ├── mapper/                      # Mappers
│   │   └── UserMapper.java
│   ├── usecase/                     # Interfaces de services
│   │   └── UserService.java
│   └── validator/                   # Validateurs métier
│       └── UserValidator.java
│
├── infrastructure/                  # Couche Infrastructure
│   ├── config/                      # Configurations
│   │   ├── database/
│   │   │   └── JpaConfig.java
│   │   └── web/
│   │       └── CorsConfig.java
│   └── persistence/                 # Accès aux données
│       └── repository/
│           └── UserRepository.java
│
├── service/                         # Implémentations des services
│   └── impl/
│       └── UserServiceImpl.java
│
├── web/                             # Couche Web
│   ├── controller/                  # Controllers REST
│   │   └── UserController.java
│   └── advice/                      # Gestion globale des exceptions
│       └── GlobalExceptionHandler.java
│
└── shared/                          # Composants partagés
    ├── constant/                    # Constantes
    │   ├── AppConstants.java
    │   └── ErrorMessages.java
    └── util/                        # Utilitaires
        ├── DateUtils.java
        └── StringUtils.java
```

## Bonnes pratiques

### 1. Gestion des exceptions

Toutes les exceptions sont capturées et gérées par le `GlobalExceptionHandler` qui retourne des réponses JSON standardisées.

### 2. Validation des données

- Validation des DTOs avec `@Valid` et les annotations Jakarta Validation
- Validation métier dans les `Validator` classes

### 3. Audit et traçabilité

- Toutes les entités héritent de `BaseEntity`
- Audit automatique avec `@CreatedDate`, `@LastModifiedDate`, etc.
- Soft delete au lieu de suppression physique

### 4. Pagination

- Tous les endpoints de liste supportent la pagination
- Réponses standardisées avec `PageResponse<T>`

### 5. Sécurité

- Validation des entrées
- Protection CORS configurée
- JWT pour l'authentification (à implémenter)
- Pas de données sensibles dans les logs

### 6. Logging

- Utilisation de SLF4J avec Logback
- Logs structurés par niveau (DEBUG, INFO, WARN, ERROR)
- Rotation des logs configurée

## Tests

### Lancer les tests

```bash
mvn test
```

### Lancer les tests avec couverture

```bash
mvn clean test jacoco:report
```

Le rapport de couverture sera disponible dans `target/site/jacoco/index.html`

## Déploiement

### Build de production

```bash
mvn clean package -DskipTests
```

Le fichier JAR sera généré dans `target/CampusMaster-0.0.1-SNAPSHOT.jar`

### Lancement en production

```bash
java -jar -Dspring.profiles.active=prod target/CampusMaster-0.0.1-SNAPSHOT.jar
```

### Docker (optionnel)

Créer un fichier `Dockerfile` :

```dockerfile
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

Build et run :

```bash
docker build -t campusmaster .
docker run -p 8080:8080 --env-file .env campusmaster
```

## Fonctionnalités à venir

- [ ] Authentification JWT complète
- [ ] Gestion des rôles et permissions (RBAC)
- [ ] Gestion des cours
- [ ] Gestion des inscriptions
- [ ] Gestion des notes
- [ ] Tableau de bord
- [ ] Notifications
- [ ] Rapports et statistiques

## Contribution

1. Fork le projet
2. Créer une branche feature (`git checkout -b feature/AmazingFeature`)
3. Commit les changements (`git commit -m 'Add some AmazingFeature'`)
4. Push vers la branche (`git push origin feature/AmazingFeature`)
5. Ouvrir une Pull Request

## Licence

Ce projet est sous licence MIT.

## Contact

Votre Nom - [@votretwitter](https://twitter.com/votretwitter)

Project Link: [https://github.com/yourusername/CampusMaster](https://github.com/yourusername/CampusMaster)
