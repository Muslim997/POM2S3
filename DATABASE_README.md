# CampusMaster - Configuration de la Base de Données

## 📋 Prérequis

- Base de données PostgreSQL (locale ou sur Render)
- Node.js et npm installés

## 🚀 Configuration

### 1. Variables d'environnement

Modifiez le fichier `.env` avec votre connexion PostgreSQL :

```env
DATABASE_URL=postgresql://username:password@host:port/database
JWT_SECRET=votre-cle-secrete-jwt-super-longue-et-complexe
```

**Pour Render PostgreSQL :**
- Allez dans votre dashboard Render
- Sélectionnez votre base de données PostgreSQL
- Copiez la `DATABASE_URL` depuis l'onglet "Connection"

### 2. Initialisation de la base de données

Exécutez ces commandes dans l'ordre :

```bash
# 1. Créer les tables et structure de base
npm run db:init

# 2. Créer les comptes de test
npm run db:test-accounts
```

## 👥 Comptes de Test

Après l'initialisation, vous pouvez vous connecter avec ces comptes :

### 👑 Administrateur
- **Email :** `admin@campusmaster.com`
- **Mot de passe :** `Admin123!`
- **Interface :** Gestion complète du système, utilisateurs, statistiques

### 👨‍🏫 Enseignant
- **Email :** `enseignant@campusmaster.com`
- **Mot de passe :** `Prof123!`
- **Interface :** Gestion des cours, devoirs, notes des étudiants

### 🎓 Étudiant
- **Email :** `etudiant@campusmaster.com`
- **Mot de passe :** `Student123!`
- **Interface :** Accès aux cours, soumission des devoirs

## 🔧 Scripts Disponibles

- `npm run db:init` - Initialise la structure complète de la base
- `npm run db:test-accounts` - Crée/met à jour les comptes de test
- `npm run dev` - Lance l'application en mode développement
- `npm run build` - Construit l'application pour la production

## 📝 Structure de la Base de Données

La base contient les tables suivantes :
- `profiles` - Utilisateurs (étudiants, enseignants, admins)
- `departments` - Départements académiques
- `subjects` - Matières/cours
- `assignments` - Devoirs
- `submissions` - Soumissions des étudiants
- `grades` - Notes et commentaires
- `enrollments` - Inscriptions aux cours
- `messages` - Messagerie interne
- `notifications` - Notifications système

## 🔒 Sécurité

- Mots de passe hashés avec bcrypt
- Authentification JWT
- Rôles utilisateurs (student, teacher, admin)

## 🚀 Déploiement

Pour le déploiement en production :
1. Configurez `DATABASE_URL` avec votre base PostgreSQL de production
2. Changez `JWT_SECRET` pour une clé sécurisée
3. Exécutez `npm run db:init` sur la base de production
4. Créez des comptes admin réels (ne gardez pas les comptes de test)

---

**Note :** Les comptes de test sont parfaits pour le développement et les démonstrations, mais créez des comptes réels pour la production.