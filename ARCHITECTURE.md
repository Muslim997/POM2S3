# Architecture Détaillée - CampusMaster

## Vue d'ensemble

CampusMaster est construit en suivant les principes de **Clean Architecture**, **SOLID**, et **Domain-Driven Design (DDD)**. Cette approche garantit :

- **Maintenabilité** : Code facile à comprendre et à modifier
- **Testabilité** : Chaque couche peut être testée indépendamment
- **Évolutivité** : Facilité d'ajout de nouvelles fonctionnalités
- **Indépendance** : Les couches ne dépendent pas des frameworks

## Architecture en Couches

### 1. Domain Layer (Couche Domaine)

La couche la plus interne, contenant la logique métier pure.

**Responsabilités** :
- Définir les entités métier
- Définir les règles métier
- Définir les exceptions métier

**Composants** :

#### Entities (`domain/entity/`)
Objets métier avec identité et cycle de vie.

```java
@Entity
public class User extends BaseEntity {
    // Propriétés métier
    private String username;
    private String email;

    // Méthodes métier
    public boolean isActive() {
        return status == UserStatus.ACTIVE;
    }
}
```

**Caractéristiques** :
- Héritent de `BaseEntity` pour l'audit
- Contiennent la logique métier
- Annotations JPA minimales
- Méthodes business utiles

#### Enums (`domain/enums/`)
Énumérations représentant des valeurs métier fixes.

```java
public enum UserRole {
    ADMIN("Admin", "Administrator"),
    STUDENT("Student", "Enrolled student");
}
```

#### Exceptions (`domain/exception/`)
Exceptions métier séparées en :
- **Business** : Erreurs métier (ResourceNotFoundException, ValidationException)
- **Technical** : Erreurs techniques (DatabaseException, ExternalServiceException)

### 2. Application Layer (Couche Application)

Orchestre les cas d'utilisation et coordonne les entités du domaine.

**Responsabilités** :
- Définir les cas d'utilisation (Use Cases)
- Transformer les données (DTOs)
- Valider les données métier
- Coordonner les transactions

**Composants** :

#### DTOs (`application/dto/`)
Objets de transfert de données pour découpler l'API des entités.

**Request DTOs** :
```java
public class CreateUserRequest {
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50)
    private String username;
}
```

**Response DTOs** :
```java
public class UserResponse {
    private Long id;
    private String username;
    private String fullName;
}
```

**Avantages** :
- Sécurité : Ne pas exposer les entités directement
- Flexibilité : Structure différente de l'entité
- Validation : Règles de validation spécifiques

#### Mappers (`application/mapper/`)
Convertissent entre entités et DTOs.

```java
@Component
public class UserMapper {
    public User toEntity(CreateUserRequest request) { }
    public UserResponse toResponse(User user) { }
}
```

**Approche alternative** : Utiliser MapStruct pour génération automatique.

#### Use Cases (`application/usecase/`)
Interfaces définissant les opérations métier.

```java
public interface UserService {
    UserResponse createUser(CreateUserRequest request);
    UserResponse getUserById(Long id);
    void deleteUser(Long id);
}
```

#### Validators (`application/validator/`)
Validation métier complexe au-delà des annotations.

```java
@Component
public class UserValidator {
    public void validateCreateUser(CreateUserRequest request) {
        // Validation métier personnalisée
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ValidationException("email", "Email already exists");
        }
    }
}
```

### 3. Infrastructure Layer (Couche Infrastructure)

Gère les détails techniques et l'accès aux ressources externes.

**Responsabilités** :
- Accès à la base de données
- Configuration technique
- Services externes
- Sécurité

**Composants** :

#### Repositories (`infrastructure/persistence/repository/`)
Accès aux données avec Spring Data JPA.

```java
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    @Query("SELECT u FROM User u WHERE u.deleted = false")
    Page<User> findAllActive(Pageable pageable);
}
```

**Fonctionnalités** :
- Méthodes dérivées automatiques
- Requêtes JPQL personnalisées
- Spécifications pour requêtes dynamiques
- Pagination et tri

#### Configurations (`infrastructure/config/`)

**Database Configuration** :
```java
@Configuration
@EnableJpaAuditing
@EnableJpaRepositories(basePackages = "...")
public class JpaConfig { }
```

**CORS Configuration** :
```java
@Configuration
public class CorsConfig {
    @Bean
    public CorsConfigurationSource corsConfigurationSource() { }
}
```

### 4. Service Layer (Couche Service)

Implémente les cas d'utilisation définis dans la couche Application.

**Responsabilités** :
- Implémenter la logique applicative
- Coordonner les appels aux repositories
- Gérer les transactions
- Appliquer les validations

**Composants** :

```java
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final UserValidator userValidator;

    @Override
    @Transactional
    public UserResponse createUser(CreateUserRequest request) {
        // 1. Validation
        userValidator.validateCreateUser(request);

        // 2. Mapping
        User user = userMapper.toEntity(request);

        // 3. Persistence
        User savedUser = userRepository.save(user);

        // 4. Response
        return userMapper.toResponse(savedUser);
    }
}
```

### 5. Web Layer (Couche Web)

Point d'entrée de l'application, expose l'API REST.

**Responsabilités** :
- Exposer les endpoints REST
- Gérer les requêtes HTTP
- Gérer les exceptions globalement
- Documenter l'API

**Composants** :

#### Controllers (`web/controller/`)
```java
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<ApiResponse<UserResponse>> createUser(
            @Valid @RequestBody CreateUserRequest request) {
        UserResponse response = userService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("User created", response));
    }
}
```

#### Global Exception Handler (`web/advice/`)
```java
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(
            ResourceNotFoundException ex) {
        // Construction de la réponse d'erreur
    }
}
```

### 6. Shared Layer (Composants Partagés)

Utilitaires et constantes utilisés dans toute l'application.

**Composants** :

#### Constants (`shared/constant/`)
```java
public final class AppConstants {
    public static final class API {
        public static final String API_VERSION = "/api/v1";
    }

    public static final class Pagination {
        public static final int DEFAULT_PAGE_SIZE = 20;
    }
}
```

#### Utilities (`shared/util/`)
```java
public final class StringUtils {
    public static boolean isNullOrEmpty(String str) { }
    public static String slugify(String input) { }
}
```

## Flux de données

### Flux de création d'un utilisateur

```
1. HTTP Request
   ↓
2. UserController.createUser()
   ↓
3. Validation (@Valid)
   ↓
4. UserService.createUser()
   ↓
5. UserValidator.validateCreateUser()
   ↓
6. UserMapper.toEntity()
   ↓
7. UserRepository.save()
   ↓
8. Database
   ↓
9. UserMapper.toResponse()
   ↓
10. ApiResponse<UserResponse>
    ↓
11. HTTP Response (JSON)
```

## Principes SOLID Appliqués

### Single Responsibility Principle (SRP)
- Chaque classe a une seule responsabilité
- Controllers : Gérer les requêtes HTTP
- Services : Logique métier
- Repositories : Accès aux données
- Mappers : Transformation de données

### Open/Closed Principle (OCP)
- Utilisation d'interfaces (UserService)
- Possibilité d'étendre sans modifier

### Liskov Substitution Principle (LSP)
- Toutes les entités héritent de BaseEntity
- Tous les services implémentent leurs interfaces

### Interface Segregation Principle (ISP)
- Interfaces spécifiques et cohésives
- Pas d'interface "fourre-tout"

### Dependency Inversion Principle (DIP)
- Dépendances vers les abstractions (interfaces)
- Injection de dépendances avec Spring

## Patterns Utilisés

### Repository Pattern
- Abstraction de l'accès aux données
- `UserRepository extends JpaRepository`

### DTO Pattern
- Séparation entre modèle de domaine et API
- `CreateUserRequest`, `UserResponse`

### Service Layer Pattern
- Logique métier centralisée
- `UserService` et `UserServiceImpl`

### Mapper Pattern
- Transformation entre objets
- `UserMapper`

### Builder Pattern
- Construction d'objets complexes
- Lombok `@Builder`

### Strategy Pattern (futur)
- Différentes stratégies de validation
- Différents algorithmes de recherche

## Gestion des Transactions

### Annotations
```java
@Transactional(readOnly = true)  // Au niveau de la classe
public class UserServiceImpl {

    @Transactional  // Override pour les méthodes d'écriture
    public UserResponse createUser() { }
}
```

### Principes
- Transactions au niveau Service
- Read-only par défaut
- Isolation appropriée
- Rollback automatique sur exception

## Sécurité

### Validation des entrées
- Jakarta Validation annotations
- Validators métier personnalisés

### Soft Delete
- Pas de suppression physique
- Flag `deleted` dans BaseEntity

### Audit
- Traçabilité avec `@CreatedDate`, `@CreatedBy`
- Version pour gestion de concurrence optimiste

### JWT (à implémenter)
- Authentification stateless
- Tokens sécurisés

## Performance

### Pagination
- Toujours paginer les listes
- Taille de page configurable

### Lazy Loading
- `open-in-view: false`
- Éviter N+1 queries

### Batch Processing
- `hibernate.jdbc.batch_size: 20`
- Order inserts et updates

### Caching (à implémenter)
- Spring Cache
- Redis pour cache distribué

## Évolutivité

### Ajout d'une nouvelle entité

1. Créer l'entité dans `domain/entity/`
2. Créer le repository dans `infrastructure/persistence/repository/`
3. Créer les DTOs dans `application/dto/`
4. Créer le mapper dans `application/mapper/`
5. Créer l'interface service dans `application/usecase/`
6. Implémenter le service dans `service/impl/`
7. Créer le controller dans `web/controller/`

### Ajout d'une nouvelle fonctionnalité

1. Définir le use case
2. Créer les DTOs si nécessaire
3. Implémenter dans le service
4. Exposer via le controller
5. Tester

## Monitoring et Observabilité

### Actuator Endpoints
- `/actuator/health` : État de santé
- `/actuator/metrics` : Métriques
- `/actuator/prometheus` : Métriques Prometheus

### Logging
- SLF4J avec Logback
- Niveaux appropriés (DEBUG, INFO, WARN, ERROR)
- Logs structurés

## Tests

### Structure des tests
```
src/test/java/
├── domain/          # Tests unitaires des entités
├── service/         # Tests des services
├── web/             # Tests d'intégration des controllers
└── integration/     # Tests d'intégration complets
```

### Types de tests
- **Unitaires** : Logique métier isolée
- **Intégration** : Plusieurs composants ensemble
- **End-to-End** : Flux complets

## Documentation

- **README.md** : Guide d'utilisation
- **ARCHITECTURE.md** : Ce document
- **Swagger UI** : Documentation API interactive
- **Javadoc** : Documentation du code (à générer)

## Conclusion

Cette architecture garantit :
- Code maintenable et testable
- Séparation claire des responsabilités
- Indépendance des frameworks
- Facilité d'évolution
- Respect des meilleures pratiques
