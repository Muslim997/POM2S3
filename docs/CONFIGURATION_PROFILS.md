# Guide de Configuration des Profils Spring Boot - CampusMaster

## Vue d'ensemble des fichiers de configuration

Votre application dispose de **6 fichiers de configuration** :

```
src/main/resources/
├── application.properties       (configuration minimale)
├── application.yml              (configuration PRINCIPALE par défaut)
├── application-h2.yml           (profil H2 - base de données en mémoire)
├── application-dev.yml          (profil développement - PostgreSQL local)
├── application-prod.yml         (profil production)
└── application-test.yml         (profil tests unitaires)
```

## Profil actuellement actif

### 🎯 Profil par défaut : **H2**

Dans [application.yml](../src/main/resources/application.yml) ligne 17 :
```yaml
spring:
  profiles:
    active: ${SPRING_PROFILES_ACTIVE:h2}
```

Cela signifie :
- **Par défaut** : Le profil `h2` est actif
- **Variable d'environnement** : Peut être surchargé par `SPRING_PROFILES_ACTIVE`

## Détails de chaque profil

### 1. Profil **H2** (actuellement actif)

**Fichier** : `application-h2.yml`

**Usage** : Développement rapide avec base de données en mémoire

**Caractéristiques** :
```yaml
Base de données : H2 (en mémoire)
URL             : jdbc:h2:mem:campusmaster
Utilisateur     : sa
Mot de passe    : (vide)
DDL             : create-drop (recrée les tables à chaque démarrage)
Logs SQL        : Activés
Console H2      : http://localhost:8080/api/h2-console
Swagger UI      : http://localhost:8080/api/swagger-ui.html
```

**Avantages** :
- ✅ Pas besoin de PostgreSQL installé
- ✅ Démarrage très rapide
- ✅ Idéal pour tests et développement rapide
- ✅ Console H2 pour voir les données

**Inconvénients** :
- ❌ Données perdues à chaque redémarrage
- ❌ Pas de persistance

### 2. Profil **DEV** (développement)

**Fichier** : `application-dev.yml`

**Usage** : Développement avec persistance des données

**Caractéristiques** :
```yaml
Base de données : PostgreSQL
URL             : jdbc:postgresql://localhost:5432/campusmaster_dev
Utilisateur     : postgres
Mot de passe    : postgres
DDL             : update (met à jour le schéma sans perdre les données)
Logs SQL        : Activés (détaillés)
Swagger UI      : Activé
Port            : 8080
```

**Prérequis** :
1. PostgreSQL installé localement
2. Base de données créée :
```sql
CREATE DATABASE campusmaster_dev;
```

**Avantages** :
- ✅ Données persistées entre redémarrages
- ✅ Logs détaillés pour debug
- ✅ Proche de la production
- ✅ Swagger pour tester l'API

### 3. Profil **PROD** (production)

**Fichier** : `application-prod.yml`

**Usage** : Déploiement en production

**Caractéristiques** :
```yaml
Base de données : PostgreSQL (URL via variable d'environnement)
DDL             : validate (vérifie le schéma, ne modifie pas)
Logs SQL        : Désactivés
Swagger UI      : DÉSACTIVÉ (sécurité)
Compression     : Activée (gzip)
Pool connexions : 20 (optimisé pour production)
Logs            : Niveau WARN (minimal)
```

**Prérequis** :
1. PostgreSQL en production
2. Variables d'environnement définies :
```bash
DATABASE_URL=jdbc:postgresql://prod-server:5432/campusmaster
DATABASE_USERNAME=prod_user
DATABASE_PASSWORD=secure_password
```

**Sécurité** :
- ✅ Swagger désactivé (pas d'exposition de l'API)
- ✅ Logs minimaux
- ✅ Validation stricte du schéma (ddl-auto: validate)
- ✅ Compression activée

### 4. Profil **TEST** (tests unitaires)

**Fichier** : `application-test.yml`

**Usage** : Tests automatisés (JUnit, Integration tests)

**Caractéristiques** :
```yaml
Base de données : H2 (en mémoire)
URL             : jdbc:h2:mem:testdb
DDL             : create-drop (base propre pour chaque test)
Logs            : Minimaux (WARN)
```

**Usage automatique** :
- Activé automatiquement lors de l'exécution de `mvn test`

## Comment changer de profil

### Méthode 1 : Via Maven (ligne de commande)

#### Démarrer avec le profil H2 (par défaut)
```bash
mvn spring-boot:run
```

#### Démarrer avec le profil DEV
```bash
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=dev"
```

#### Démarrer avec le profil PROD
```bash
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=prod"
```

#### Démarrer avec le profil TEST
```bash
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=test"
```

### Méthode 2 : Via le JAR compilé

```bash
# Compiler le projet
mvn clean package

# Exécuter avec différents profils
java -jar target/CampusMaster-0.0.1-SNAPSHOT.jar --spring.profiles.active=h2
java -jar target/CampusMaster-0.0.1-SNAPSHOT.jar --spring.profiles.active=dev
java -jar target/CampusMaster-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod
```

### Méthode 3 : Via variable d'environnement

#### Windows (PowerShell)
```powershell
$env:SPRING_PROFILES_ACTIVE="dev"
mvn spring-boot:run
```

#### Windows (CMD)
```cmd
set SPRING_PROFILES_ACTIVE=dev
mvn spring-boot:run
```

#### Linux/Mac
```bash
export SPRING_PROFILES_ACTIVE=dev
mvn spring-boot:run
```

### Méthode 4 : Modifier application.yml

**Déconseillé** (préférer les méthodes ci-dessus)

Modifier la ligne 17 de `application.yml` :
```yaml
spring:
  profiles:
    active: dev  # Changer h2 par dev, prod ou test
```

### Méthode 5 : Via IDE (IntelliJ IDEA / Eclipse)

#### IntelliJ IDEA
1. Run → Edit Configurations
2. Sélectionner votre configuration Spring Boot
3. Dans "Active profiles", entrer : `dev`, `prod`, ou `test`
4. Cliquer sur OK et lancer l'application

#### Eclipse (Spring Tools)
1. Run → Run Configurations
2. Sélectionner votre projet
3. Onglet "(x)= Arguments"
4. Dans "Program arguments", ajouter : `--spring.profiles.active=dev`
5. Apply et Run

## Vérifier le profil actif

### Au démarrage de l'application

Regardez les logs au démarrage :
```
2024-01-15 10:30:00 - The following 1 profile is active: "h2"
```

### Via l'API (si actuator est activé)

```bash
curl http://localhost:8080/api/actuator/env
```

### Dans les logs

Regardez la connexion à la base de données :
```
# Profil H2
HikariPool - Added connection conn0: url=jdbc:h2:mem:campusmaster

# Profil DEV
HikariPool - Added connection conn0: url=jdbc:postgresql://localhost:5432/campusmaster_dev

# Profil PROD
HikariPool - Added connection conn0: url=jdbc:postgresql://prod-server:5432/campusmaster
```

## Tableau récapitulatif

| Profil | Base de données | Persistance | Swagger | Logs SQL | Usage |
|--------|----------------|-------------|---------|----------|-------|
| **h2** (défaut) | H2 mémoire | ❌ Non | ✅ Oui | ✅ Oui | Dev rapide |
| **dev** | PostgreSQL local | ✅ Oui | ✅ Oui | ✅ Oui | Dev avec persistance |
| **test** | H2 mémoire | ❌ Non | ❌ Non | ⚠️ Minimal | Tests unitaires |
| **prod** | PostgreSQL prod | ✅ Oui | ❌ Non | ❌ Non | Production |

## Configuration complète des URLs

### Profil H2 (actuel)
```
Application    : http://localhost:8080/api
Swagger UI     : http://localhost:8080/api/swagger-ui.html
API Docs       : http://localhost:8080/api/v3/api-docs
H2 Console     : http://localhost:8080/api/h2-console
WebSocket      : ws://localhost:8080/api/ws
Actuator       : http://localhost:8080/api/actuator
```

### Profil DEV
```
Application    : http://localhost:8080/api
Swagger UI     : http://localhost:8080/api/swagger-ui.html
API Docs       : http://localhost:8080/api/v3/api-docs
WebSocket      : ws://localhost:8080/api/ws
Actuator       : http://localhost:8080/api/actuator
```

### Profil PROD
```
Application    : http://your-domain.com/api
WebSocket      : wss://your-domain.com/api/ws
Actuator       : http://your-domain.com/api/actuator
(Swagger désactivé)
```

## Variables d'environnement disponibles

Toutes ces variables peuvent surcharger les valeurs par défaut :

### Serveur
```bash
SERVER_PORT=8080
```

### Base de données
```bash
DATABASE_URL=jdbc:postgresql://localhost:5432/campusmaster
DATABASE_USERNAME=postgres
DATABASE_PASSWORD=postgres
DB_POOL_SIZE=10
HIBERNATE_DDL_AUTO=update
SHOW_SQL=false
```

### JWT
```bash
JWT_SECRET=votre_secret_jwt_ici
JWT_EXPIRATION=86400000
JWT_REFRESH_EXPIRATION=604800000
```

### CORS
```bash
CORS_ALLOWED_ORIGINS=http://localhost:3000,http://localhost:4200
CORS_ALLOWED_METHODS=GET,POST,PUT,DELETE,PATCH,OPTIONS
CORS_ALLOWED_HEADERS=*
CORS_MAX_AGE=3600
```

### Upload
```bash
UPLOAD_BASE_PATH=uploads
UPLOAD_MAX_SIZE=52428800
MAX_FILE_SIZE=50MB
MAX_REQUEST_SIZE=50MB
FILE_UPLOAD_DIR=./uploads
```

### Email (pour notifications)
```bash
SPRING_MAIL_HOST=smtp.gmail.com
SPRING_MAIL_PORT=587
SPRING_MAIL_USERNAME=votre-email@gmail.com
SPRING_MAIL_PASSWORD=votre-mot-de-passe-app
SPRING_MAIL_FROM=noreply@campusmaster.com
```

## Recommandations

### Pour le développement local
✅ **Utiliser le profil H2** :
- Rapide, simple, sans configuration
- Parfait pour tester rapidement des fonctionnalités
```bash
mvn spring-boot:run
```

### Pour le développement avancé
✅ **Utiliser le profil DEV** :
- Données persistées
- Plus proche de la production
- Nécessite PostgreSQL installé
```bash
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=dev"
```

### Pour les tests automatisés
✅ **Le profil TEST est automatique** :
```bash
mvn test
```

### Pour la production
✅ **Utiliser le profil PROD** avec variables d'environnement :
```bash
export DATABASE_URL=jdbc:postgresql://prod-server:5432/campusmaster
export DATABASE_USERNAME=prod_user
export DATABASE_PASSWORD=secure_password
export JWT_SECRET=long_secure_random_string_here

java -jar CampusMaster-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod
```

## Préparer PostgreSQL pour le profil DEV

### Installation PostgreSQL

#### Windows
1. Télécharger depuis https://www.postgresql.org/download/windows/
2. Installer avec l'installeur
3. Définir le mot de passe `postgres`

#### Linux (Ubuntu/Debian)
```bash
sudo apt update
sudo apt install postgresql postgresql-contrib
sudo systemctl start postgresql
```

#### Mac
```bash
brew install postgresql
brew services start postgresql
```

### Créer la base de données

```bash
# Se connecter à PostgreSQL
psql -U postgres

# Créer la base de données
CREATE DATABASE campusmaster_dev;

# Vérifier
\l

# Quitter
\q
```

### Tester la connexion

```bash
psql -U postgres -d campusmaster_dev
```

Si tout fonctionne, vous pouvez maintenant utiliser le profil DEV !

## Troubleshooting

### Problème : "Could not connect to database"

**Solution pour H2** :
- Aucune action nécessaire, H2 est intégré

**Solution pour DEV/PROD** :
1. Vérifier que PostgreSQL est démarré
2. Vérifier les credentials dans `application-dev.yml`
3. Créer la base de données si elle n'existe pas

### Problème : "Table does not exist"

**Solution** :
- Profil H2/TEST : Normal, tables créées automatiquement
- Profil DEV : Changer `ddl-auto: update` ou `create-drop`
- Profil PROD : Utiliser des migrations (Flyway/Liquibase)

### Problème : "Port 8080 already in use"

**Solution** :
```bash
# Changer le port
mvn spring-boot:run -Dspring-boot.run.arguments="--server.port=8081"

# Ou via variable d'environnement
export SERVER_PORT=8081
mvn spring-boot:run
```

## Résumé

**🎯 Profil actuel** : `h2` (défaut)

**✅ Pour commencer rapidement** :
```bash
mvn spring-boot:run
```

**✅ Pour développement avec persistance** :
```bash
# Installer PostgreSQL et créer la base
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=dev"
```

**✅ Pour tester** :
```bash
mvn test
```

**✅ Pour production** :
```bash
java -jar target/CampusMaster-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod
```
