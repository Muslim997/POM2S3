# MODULE TABLEAU DE BORD ANALYTIQUE - DOCUMENTATION COMPLÈTE

## Vue d'ensemble

Le module Tableau de bord analytique offre une suite complète d'outils d'analyse et de visualisation des données de la plateforme CampusMaster. Il permet aux administrateurs et enseignants de suivre en temps réel les performances académiques, l'activité des utilisateurs et les indicateurs clés de performance (KPI).

## Fonctionnalités principales

### 1. Vue d'ensemble du tableau de bord
Affiche un résumé complet avec les métriques essentielles et les alertes actives.

### 2. Graphes d'évolution des notes
Visualisation graphique de la progression académique des étudiants avec calcul de tendances.

### 3. Activité hebdomadaire
Suivi détaillé des pages vues, téléchargements et uploads avec historique quotidien.

### 4. Statistiques par département/matière
Analyses approfondies de la performance académique par département et matière.

### 5. KPI configurables
Indicateurs clés de performance personnalisables avec seuils d'alerte.

## Architecture

```
domain/
└── entity/
    ├── ActivityLog.java         # Log d'activité des utilisateurs
    └── KPIConfiguration.java    # Configuration des KPIs
application/
├── dto/
│   └── dashboard/
│       ├── GrapheEvolutionNotesDto.java
│       ├── ActivityStatsDto.java
│       ├── StatsDepartementDto.java
│       ├── KPIDto.java
│       └── DashboardSummaryDto.java
└── service/
    ├── ActivityTrackingService.java
    ├── GradeAnalysisService.java
    ├── KPIService.java
    └── DashboardService.java
web/
└── controller/
    └── DashboardController.java (8 endpoints)
```

## Endpoints REST

### 1. Vue d'ensemble

#### GET /dashboard/summary
Récupère un résumé complet du tableau de bord.

**Réponse:**
```json
{
  "nombreTotalEtudiants": 250,
  "nombreEtudiantsActifs": 230,
  "nombreEnseignants": 45,
  "nombreCours": 120,
  "activitesRecentes": 1543,
  "pagesVuesRecentes": 892,
  "telechargements": 234,
  "moyenneGenerale": 13.5,
  "tauxReussite": 75.5,
  "tauxAssiduité": 84.2,
  "kpisPrincipaux": [{
    "id": 1,
    "code": "TAUX_ASSIDUITE",
    "libelle": "Taux d'assiduité",
    "valeur": 84.2,
    "unite": "%",
    "statut": "NORMAL",
    "seuilAlerte": 70.0,
    "seuilCritique": 50.0,
    "tendance": "STABLE"
  }],
  "nombreAlertes": 2,
  "alertes": [{
    "type": "TAUX_RETARD",
    "message": "Taux de retard nécessite attention: 25.3%",
    "severite": "WARNING",
    "action": "Surveillance recommandée"
  }]
}
```

### 2. Graphe d'évolution des notes

#### GET /dashboard/notes/evolution/etudiant/{etudiantId}
Évolution temporelle des notes d'un étudiant spécifique.

**Réponse:**
```json
{
  "etudiantId": 1,
  "etudiantNom": "Jean Dupont",
  "evolution": [{
    "date": "2025-01-15",
    "note": 14.5,
    "matiere": "Programmation Java",
    "typeEvaluation": "TP1 - Classes et Objets"
  }, {
    "date": "2025-01-22",
    "note": 15.0,
    "matiere": "Programmation Java",
    "typeEvaluation": "TP2 - Héritage"
  }],
  "moyenneGlobale": 14.75,
  "moyenneMinimum": 12.0,
  "moyenneMaximum": 17.5,
  "tendance": "HAUSSE"
}
```

#### GET /dashboard/notes/evolution/cours/{coursId}
Évolution des notes de tous les étudiants d'un cours.

**Réponse:**
```json
[{
  "etudiantId": 1,
  "etudiantNom": "Jean Dupont",
  "evolution": [...],
  "moyenneGlobale": 14.75,
  "tendance": "HAUSSE"
}, {
  "etudiantId": 2,
  "etudiantNom": "Marie Martin",
  "evolution": [...],
  "moyenneGlobale": 13.2,
  "tendance": "STABLE"
}]
```

### 3. Activité hebdomadaire

#### GET /dashboard/activity/weekly
Statistiques d'activité des 7 derniers jours.

**Réponse:**
```json
{
  "startDate": "2025-01-10",
  "endDate": "2025-01-16",
  "totalActivities": 1543,
  "totalPageViews": 892,
  "totalDownloads": 234,
  "totalUploads": 156,
  "dailyActivities": [{
    "date": "2025-01-10",
    "pageViews": 125,
    "downloads": 34,
    "uploads": 22,
    "totalActivities": 181
  }, {
    "date": "2025-01-11",
    "pageViews": 142,
    "downloads": 41,
    "uploads": 25,
    "totalActivities": 208
  }],
  "activitiesByType": {
    "PAGE_VIEW": 892,
    "FILE_DOWNLOAD": 234,
    "FILE_UPLOAD": 156,
    "LOGIN": 198,
    "SUBMIT_ASSIGNMENT": 63
  },
  "topPages": [{
    "name": "Cours #5",
    "count": 89,
    "type": "Cours"
  }],
  "topDownloads": [{
    "name": "Support #12",
    "count": 45,
    "type": "Support"
  }]
}
```

#### GET /dashboard/activity/period?startDate=2025-01-01&endDate=2025-01-31
Statistiques pour une période personnalisée.

**Paramètres:**
- `startDate`: Date de début (format: YYYY-MM-DD)
- `endDate`: Date de fin (format: YYYY-MM-DD)

### 4. Statistiques par département

#### GET /dashboard/stats/departement/{departementId}
Statistiques détaillées d'un département.

**Réponse:**
```json
{
  "departementId": 1,
  "departementNom": "Informatique",
  "nombreEtudiants": 120,
  "nombreEnseignants": 15,
  "nombreCours": 45,
  "nombreModules": 8,
  "moyenneGenerale": 13.8,
  "tauxReussite": 78.5,
  "tauxAssiduité": 85.3,
  "statsParMatiere": {
    "Programmation Java": {
      "matiereNom": "Programmation Java",
      "nombreEtudiants": 85,
      "moyenneMatiere": 14.2,
      "tauxReussite": 82.4,
      "nombreDevoirs": 12,
      "devoirsRendus": 980
    },
    "Base de données": {
      "matiereNom": "Base de données",
      "nombreEtudiants": 75,
      "moyenneMatiere": 13.1,
      "tauxReussite": 74.7,
      "nombreDevoirs": 10,
      "devoirsRendus": 720
    }
  }
}
```

### 5. KPI configurables

#### GET /dashboard/kpi/all
Liste de tous les KPIs avec leurs valeurs actuelles.

**Réponse:**
```json
[{
  "id": 1,
  "code": "TAUX_ASSIDUITE",
  "libelle": "Taux d'assiduité",
  "description": "Pourcentage de devoirs rendus par rapport aux devoirs attendus",
  "valeur": 84.2,
  "unite": "%",
  "statut": "NORMAL",
  "seuilAlerte": 70.0,
  "seuilCritique": 50.0,
  "tendance": "STABLE",
  "variationPourcentage": 0.0,
  "dateCalcul": "2025-01-16T15:30:00"
}, {
  "id": 2,
  "code": "TAUX_RETARD",
  "libelle": "Taux de retard",
  "description": "Pourcentage de devoirs rendus en retard",
  "valeur": 15.3,
  "unite": "%",
  "statut": "ALERTE",
  "seuilAlerte": 20.0,
  "seuilCritique": 40.0,
  "tendance": "STABLE"
}, {
  "id": 3,
  "code": "TAUX_REUSSITE",
  "libelle": "Taux de réussite",
  "description": "Pourcentage d'étudiants avec note >= 10",
  "valeur": 75.5,
  "unite": "%",
  "statut": "NORMAL",
  "seuilAlerte": 60.0,
  "seuilCritique": 40.0,
  "tendance": "HAUSSE"
}]
```

## KPIs disponibles

| Code | Libellé | Description | Formule |
|------|---------|-------------|---------|
| TAUX_ASSIDUITE | Taux d'assiduité | % de devoirs rendus | (Devoirs rendus / Devoirs attendus) × 100 |
| TAUX_RETARD | Taux de retard | % de devoirs en retard | (Devoirs retard / Total rendus) × 100 |
| TAUX_REUSSITE | Taux de réussite | % notes >= 10 | (Notes >= 10 / Total notes) × 100 |
| MOYENNE_GENERALE | Moyenne générale | Moyenne de toutes les notes | Σ(notes) / n |
| TAUX_COMPLETION | Taux de complétion | % de devoirs complétés | (Devoirs rendus / Total devoirs) × 100 |
| TAUX_ACTIVITE | Taux d'activité | % utilisateurs actifs | (Actifs / Total) × 100 |

## Statuts des KPIs

- **NORMAL**: La valeur est dans les limites acceptables
- **ALERTE**: La valeur est sous le seuil d'alerte (nécessite surveillance)
- **CRITIQUE**: La valeur est sous le seuil critique (intervention requise)

## Tendances

- **HAUSSE**: Amélioration progressive (moyenne récente > moyenne ancienne)
- **BAISSE**: Dégradation progressive (moyenne récente < moyenne ancienne)
- **STABLE**: Pas de variation significative (différence < 1.0)

## Types d'activités trackées

| Type | Description |
|------|-------------|
| PAGE_VIEW | Consultation d'une page |
| FILE_DOWNLOAD | Téléchargement d'un fichier |
| FILE_UPLOAD | Upload d'un fichier |
| SUBMIT_ASSIGNMENT | Soumission d'un devoir |
| LOGIN | Connexion |
| LOGOUT | Déconnexion |
| COURSE_ACCESS | Accès à un cours |
| ANNOUNCEMENT_VIEW | Lecture d'une annonce |

## Cas d'usage

### 1. Suivi de la progression d'un étudiant

```bash
# 1. Consulter l'évolution des notes
GET /dashboard/notes/evolution/etudiant/1

# 2. Analyser la tendance
# → HAUSSE: L'étudiant s'améliore
# → BAISSE: Nécessite intervention
# → STABLE: Performance constante
```

### 2. Analyse de l'activité hebdomadaire

```bash
# 1. Récupérer les stats de la semaine
GET /dashboard/activity/weekly

# 2. Identifier les pics d'activité
# → Analyser les dailyActivities
# → Identifier les jours les plus actifs

# 3. Vérifier les contenus populaires
# → Consulter topPages et topDownloads
```

### 3. Monitoring des départements

```bash
# 1. Stats globales du département
GET /dashboard/stats/departement/1

# 2. Comparer les matières
# → Identifier les matières en difficulté
# → Comparer les moyennes et taux de réussite

# 3. Actions ciblées
# → Focus sur les matières avec faible performance
```

### 4. Surveillance des KPIs

```bash
# 1. Récupérer tous les KPIs
GET /dashboard/kpi/all

# 2. Identifier les alertes
# → Filtrer les KPIs avec statut ALERTE ou CRITIQUE

# 3. Prendre des mesures
# → Interventions basées sur les KPIs critiques
```

## Récapitulatif technique

### Endpoints créés: 8

| Endpoint | Méthode | Description |
|----------|---------|-------------|
| /dashboard/summary | GET | Vue d'ensemble |
| /dashboard/notes/evolution/etudiant/{id} | GET | Évolution notes étudiant |
| /dashboard/notes/evolution/cours/{id} | GET | Évolution notes cours |
| /dashboard/activity/weekly | GET | Activité hebdomadaire |
| /dashboard/activity/period | GET | Activité période personnalisée |
| /dashboard/stats/departement/{id} | GET | Stats département |
| /dashboard/kpi/all | GET | Tous les KPIs |

### Entités créées: 2
- **ActivityLog**: Log de toutes les activités utilisateurs
- **KPIConfiguration**: Configuration des indicateurs

### Services créés: 4
- **ActivityTrackingService**: Suivi et analyse d'activité
- **GradeAnalysisService**: Analyse des notes et tendances
- **KPIService**: Calcul des indicateurs
- **DashboardService**: Orchestration du tableau de bord

### DTOs créés: 5
- GrapheEvolutionNotesDto
- ActivityStatsDto
- StatsDepartementDto
- KPIDto
- DashboardSummaryDto

## Sécurité

Tous les endpoints requièrent une authentification Bearer JWT:
```http
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

Rôles autorisés: `ADMIN`, `TEACHER`

## Performance

- Les statistiques sont calculées à la demande (pas de cache)
- Les requêtes complexes utilisent des streams Java pour l'optimisation
- Index sur les tables:
  - `activity_logs`: user_id, activity_type, activity_date
  - `kpi_configurations`: code, actif, ordre_affichage

## Évolutions futures possibles

1. **Cache Redis** pour les statistiques fréquemment consultées
2. **Notifications automatiques** basées sur les seuils KPI
3. **Export PDF/Excel** des rapports
4. **Graphes interactifs** (intégration Chart.js/D3.js)
5. **Prédictions ML** des tendances futures
6. **Alertes temps réel** via WebSocket

## Conclusion

Le module Tableau de bord analytique offre:
- ✅ 8 endpoints REST complets
- ✅ Analyse en temps réel des performances
- ✅ KPIs configurables et personnalisables
- ✅ Tracking complet de l'activité
- ✅ Visualisation de l'évolution des notes
- ✅ Statistiques par département/matière
- ✅ Système d'alertes intelligent
- ✅ Architecture propre et évolutive

Compilation réussie avec **130+ fichiers Java** sans erreurs.
