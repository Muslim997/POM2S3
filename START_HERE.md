# 🚀 Démarrage Rapide - CampusMaster

## ⚡ Option 1 : Démarrage avec H2 (Base de données en mémoire) - RECOMMANDÉ

La façon la plus rapide de tester l'application sans configurer PostgreSQL !

### Windows
```bash
start-h2.bat
```

### Linux/Mac
```bash
chmod +x start-h2.sh
./start-h2.sh
```

### Ou manuellement
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=h2
```

### Accès à l'application

Une fois démarré, accédez à :

- **Swagger UI** : http://localhost:8080/api/swagger-ui.html
- **H2 Console** : http://localhost:8080/api/h2-console
  - JDBC URL: `jdbc:h2:mem:campusmaster`
  - Username: `sa`
  - Password: (laisser vide)

## 📝 Tester l'API

### Créer un utilisateur

```bash
curl -X POST http://localhost:8080/api/v1/users \
  -H "Content-Type: application/json" \
  -d '{
    "username": "johndoe",
    "email": "john@example.com",
    "password": "SecurePass123!",
    "firstName": "John",
    "lastName": "Doe",
    "role": "STUDENT"
  }'
```

### Obtenir tous les utilisateurs

```bash
curl http://localhost:8080/api/v1/users
```

### Obtenir un utilisateur par ID

```bash
curl http://localhost:8080/api/v1/users/1
```

## 🐘 Option 2 : Démarrage avec PostgreSQL

### 1. Installer PostgreSQL

Téléchargez et installez PostgreSQL depuis : https://www.postgresql.org/download/

### 2. Créer la base de données

```bash
psql -U postgres
```

```sql
CREATE DATABASE campusmaster_dev;
\q
```

### 3. Configurer les identifiants

Créez un fichier `.env` à la racine du projet :

```properties
DATABASE_URL=jdbc:postgresql://localhost:5432/campusmaster_dev
DATABASE_USERNAME=postgres
DATABASE_PASSWORD=votre_mot_de_passe
```

### 4. Démarrer l'application

```bash
# Charger les variables d'environnement
# Linux/Mac
export $(cat .env | xargs)

# Windows PowerShell
Get-Content .env | ForEach-Object {
    $name, $value = $_.split('=')
    Set-Content env:\$name $value
}

# Démarrer
mvn spring-boot:run
```

## ❌ Problèmes ?

### Erreur d'authentification PostgreSQL

Consultez le fichier [TROUBLESHOOTING.md](TROUBLESHOOTING.md) pour les solutions détaillées.

**Solution rapide** : Utilisez H2 (Option 1) au lieu de PostgreSQL.

### Port 8080 déjà utilisé

```bash
# Changer le port
SERVER_PORT=8081 mvn spring-boot:run -Dspring-boot.run.profiles=h2
```

### Erreur de compilation

```bash
mvn clean install -U
```

## 📚 Documentation complète

- [README.md](README.md) - Documentation principale
- [QUICK_START.md](QUICK_START.md) - Guide de démarrage détaillé
- [ARCHITECTURE.md](ARCHITECTURE.md) - Documentation d'architecture
- [TROUBLESHOOTING.md](TROUBLESHOOTING.md) - Résolution de problèmes
- [COMMANDES.md](COMMANDES.md) - Référence des commandes

## 🎯 Prochaines étapes

1. ✅ Testez l'API avec Swagger UI
2. ✅ Créez quelques utilisateurs
3. ✅ Explorez la H2 Console
4. 📖 Lisez la [documentation complète](README.md)
5. 🏗️ Commencez à développer vos propres fonctionnalités

## 🆘 Besoin d'aide ?

- Consultez [TROUBLESHOOTING.md](TROUBLESHOOTING.md)
- Lisez [QUICK_START.md](QUICK_START.md)
- Vérifiez les logs dans `logs/campusmaster.log`

Bon développement ! 🎓
