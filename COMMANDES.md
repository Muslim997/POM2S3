# Commandes Utiles - CampusMaster

## Maven

### Build et Installation

```bash
# Nettoyer le projet
mvn clean

# Compiler le projet
mvn compile

# Nettoyer et compiler
mvn clean compile

# Installer les dépendances
mvn install

# Nettoyer et installer
mvn clean install

# Installer sans lancer les tests
mvn clean install -DskipTests

# Mettre à jour les dépendances
mvn clean install -U
```

### Exécution

```bash
# Lancer l'application
mvn spring-boot:run

# Lancer avec un profil spécifique
mvn spring-boot:run -Dspring-boot.run.profiles=dev
mvn spring-boot:run -Dspring-boot.run.profiles=prod

# Lancer avec un port spécifique
mvn spring-boot:run -Dspring-boot.run.arguments=--server.port=8081

# Lancer avec des variables d'environnement
SERVER_PORT=8081 mvn spring-boot:run
```

### Tests

```bash
# Lancer tous les tests
mvn test

# Lancer les tests d'une classe spécifique
mvn test -Dtest=UserServiceTest

# Lancer un test spécifique
mvn test -Dtest=UserServiceTest#testCreateUser

# Lancer les tests avec couverture de code
mvn clean test jacoco:report

# Lancer les tests d'intégration
mvn verify

# Skip tests
mvn install -DskipTests
mvn install -Dmaven.test.skip=true
```

### Packaging

```bash
# Créer un JAR
mvn package

# Créer un JAR sans tests
mvn package -DskipTests

# Créer un WAR (si configuré)
mvn package -Pwar
```

### Nettoyage

```bash
# Supprimer le dossier target
mvn clean

# Nettoyer et supprimer les dépendances
mvn dependency:purge-local-repository
```

### Informations

```bash
# Afficher l'arbre de dépendances
mvn dependency:tree

# Analyser les dépendances
mvn dependency:analyze

# Vérifier les mises à jour disponibles
mvn versions:display-dependency-updates

# Afficher les propriétés
mvn help:effective-pom
```

## PostgreSQL

### Gestion de la base de données

```bash
# Se connecter à PostgreSQL
psql -U postgres

# Se connecter à une base spécifique
psql -U postgres -d campusmaster_dev

# Lister les bases de données
\l

# Se connecter à une base
\c campusmaster_dev

# Lister les tables
\dt

# Décrire une table
\d users

# Exécuter un fichier SQL
\i script.sql

# Quitter
\q
```

### Commandes SQL

```sql
-- Créer une base de données
CREATE DATABASE campusmaster_dev;
CREATE DATABASE campusmaster_test;
CREATE DATABASE campusmaster_prod;

-- Supprimer une base de données
DROP DATABASE IF EXISTS campusmaster_dev;

-- Créer un utilisateur
CREATE USER campusmaster WITH PASSWORD 'password';

-- Donner les droits
GRANT ALL PRIVILEGES ON DATABASE campusmaster_dev TO campusmaster;

-- Lister les tables
SELECT tablename FROM pg_tables WHERE schemaname = 'public';

-- Compter les enregistrements
SELECT COUNT(*) FROM users;

-- Voir les derniers utilisateurs créés
SELECT * FROM users ORDER BY created_at DESC LIMIT 10;

-- Supprimer toutes les données d'une table
TRUNCATE TABLE users CASCADE;

-- Réinitialiser les séquences
ALTER SEQUENCE users_id_seq RESTART WITH 1;

-- Backup de la base
pg_dump -U postgres campusmaster_dev > backup.sql

-- Restaurer une base
psql -U postgres campusmaster_dev < backup.sql
```

## Docker (si utilisé)

### Construction et exécution

```bash
# Construire l'image
docker build -t campusmaster:latest .

# Lancer le conteneur
docker run -p 8080:8080 --name campusmaster campusmaster:latest

# Lancer avec variables d'environnement
docker run -p 8080:8080 --env-file .env campusmaster:latest

# Lancer en arrière-plan
docker run -d -p 8080:8080 --name campusmaster campusmaster:latest

# Arrêter le conteneur
docker stop campusmaster

# Démarrer le conteneur
docker start campusmaster

# Voir les logs
docker logs campusmaster
docker logs -f campusmaster  # Suivre les logs

# Entrer dans le conteneur
docker exec -it campusmaster /bin/sh

# Supprimer le conteneur
docker rm campusmaster

# Supprimer l'image
docker rmi campusmaster:latest
```

### Docker Compose

```bash
# Démarrer tous les services
docker-compose up

# Démarrer en arrière-plan
docker-compose up -d

# Arrêter tous les services
docker-compose down

# Voir les logs
docker-compose logs -f

# Reconstruire les images
docker-compose build

# Reconstruire et démarrer
docker-compose up --build
``

### Health Check

```bash
# Vérifier l'état de santé
curl http://localhost:8080/api/actuator/health

# Obtenir les métriques
curl http://localhost:8080/api/actuator/metrics

# Obtenir les informations
curl http://localhost:8080/api/actuator/info
```

## Logs

### Voir les logs

```bash
# Logs en temps réel
tail -f logs/campusmaster.log

# Dernières lignes
tail -n 100 logs/campusmaster.log

# Rechercher dans les logs
grep "ERROR" logs/campusmaster.log
grep -i "user" logs/campusmaster.log

# Compter les erreurs
grep -c "ERROR" logs/campusmaster.log
```

## Performance

### Monitoring

```bash
# Vérifier l'utilisation mémoire
jstat -gc <pid>

# Thread dump
jstack <pid>

# Heap dump
jmap -dump:live,format=b,file=heap.bin <pid>

# Analyser la heap
jhat heap.bin
```

## Variables d'environnement

### Export temporaire