# CampusMaster - Plateforme pédagogique UCAD

Une plateforme digitale moderne pour la gestion des cours et devoirs à l'Université Cheikh Anta Diop de Dakar (UCAD).

## 🚀 Fonctionnalités

### Pour les Étudiants
- 📚 Accès aux programmes des Masters 2 (Informatique, Mathématiques, Physique)
- 📝 Gestion et soumission des devoirs
- 💬 Messagerie interne avec les enseignants
- 📊 Suivi des notes et progression académique
- 🔔 Notifications en temps réel

### Pour les Enseignants
- 👥 Gestion des cours et contenus pédagogiques
- 📋 Création et correction des devoirs
- 📈 Tableaux de bord analytiques
- 💬 Communication avec les étudiants
- 📊 Statistiques de performance

### Pour les Administrateurs
- 👤 Gestion complète des utilisateurs
- 📊 Analytics avancés de la plateforme
- ⚙️ Configuration système
- 📈 Tableaux de bord administratifs

## 🛠️ Technologies utilisées

- **Frontend**: Next.js 14, React, TypeScript, Tailwind CSS
- **Backend**: Next.js API Routes
- **Base de données**: Supabase (PostgreSQL)
- **Authentification**: Supabase Auth
- **UI Components**: Lucide React (icônes)
- **Styling**: Tailwind CSS
- **Déploiement**: Vercel

## 📋 Prérequis

- Node.js 18+
- npm ou yarn
- Compte Supabase

## 🚀 Installation et développement

1. **Cloner le repository**
   ```bash
   git clone https://github.com/Muslim997/POM2S3.git
   cd POM2S3
   ```

2. **Installer les dépendances**
   ```bash
   npm install
   ```

3. **Configuration de l'environnement**
   ```bash
   cp .env.example .env.local
   ```
   Remplir les variables d'environnement nécessaires (Supabase URL, clés API, etc.)

4. **Lancer le serveur de développement**
   ```bash
   npm run dev
   ```

5. **Ouvrir [http://localhost:3000](http://localhost:3000)**

## 📊 Comptes de démonstration

- **Étudiant**: `student@campus.com` / `password`
- **Enseignant**: `teacher@campus.com` / `password`
- **Administrateur**: `admin@campus.com` / `password`

## 🏗️ Structure du projet

```
├── app/                    # Pages Next.js (App Router)
│   ├── api/               # API Routes
│   ├── dashboard/         # Tableau de bord
│   ├── courses/           # Gestion des cours
│   ├── assignments/       # Gestion des devoirs
│   ├── messages/          # Messagerie
│   └── ...
├── components/            # Composants réutilisables
├── lib/                   # Utilitaires et configurations
├── database/              # Scripts et schémas de base de données
└── public/                # Assets statiques
```

## 🔧 Scripts disponibles

- `npm run dev` - Lancer le serveur de développement
- `npm run build` - Build de production
- `npm run start` - Lancer le serveur de production
- `npm run lint` - Vérification du code

## 🚀 Déploiement

Le projet est configuré pour un déploiement facile sur Vercel :

1. Connecter votre compte GitHub à Vercel
2. Importer le repository
3. Configurer les variables d'environnement
4. Déployer !

## 📝 Base de données

Le schéma de la base de données est défini dans `database/schema.sql`. Les migrations Supabase sont dans `supabase/migrations/`.

## 🤝 Contribution

1. Fork le projet
2. Créer une branche feature (`git checkout -b feature/AmazingFeature`)
3. Commit les changements (`git commit -m 'Add some AmazingFeature'`)
4. Push vers la branche (`git push origin feature/AmazingFeature`)
5. Ouvrir une Pull Request

## 📄 Licence

Ce projet est sous licence MIT - voir le fichier [LICENSE](LICENSE) pour plus de détails.

## 👥 Équipe

- **Développeur principal**: [Votre nom]
- **Institution**: Université Cheikh Anta Diop de Dakar (UCAD)
- **Programme**: Master 2 - Ouverture et Professionnalisation

## 📞 Support

Pour toute question ou problème, veuillez ouvrir une issue sur GitHub.

---

**CampusMaster UCAD** - Révolutionnons l'enseignement supérieur au Sénégal ! 🇸🇳
"# POM2S3"  
