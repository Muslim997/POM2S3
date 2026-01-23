# Module de Messagerie Interne - Documentation Complète

## Vue d'ensemble

Le module de messagerie interne de CampusMaster permet une communication fluide entre les utilisateurs de la plateforme (étudiants, enseignants, administrateurs). Il supporte deux types de messagerie :

- **Messagerie privée** : Communication bidirectionnelle entre deux utilisateurs
- **Messagerie de groupe** : Discussions de groupe organisées par matière ou cours
- **Système de tags** : Catégorisation des messages avec des tags prédéfinis (#urgent, #annonce, #projet)

## Architecture

### Entités du Domaine

#### 1. Message
Entité centrale représentant un message (privé ou de groupe).

**Fichier** : `domain/entity/Message.java`

**Champs principaux** :
- `contenu` (String) : Contenu du message
- `estLu` (Boolean) : Indicateur de lecture
- `dateEnvoi` (LocalDateTime) : Date et heure d'envoi
- `typeMessage` (MessageType) : PRIVE ou GROUPE
- `expediteur` (User) : Utilisateur expéditeur
- `destinataire` (User) : Utilisateur destinataire (pour messages privés)
- `conversation` (Conversation) : Conversation associée (pour messages privés)
- `groupe` (GroupeMessage) : Groupe associé (pour messages de groupe)
- `tags` (Set<MessageTag>) : Tags associés au message

**Relations** :
```java
@ManyToOne → User (expéditeur)
@ManyToOne → User (destinataire)
@ManyToOne → Conversation
@ManyToOne → GroupeMessage
@ManyToMany → MessageTag
```

**Index** :
- `idx_message_expediteur` sur expediteur_id
- `idx_message_destinataire` sur destinataire_id
- `idx_message_conversation` sur conversation_id
- `idx_message_groupe` sur groupe_id
- `idx_message_date_envoi` sur date_envoi
- `idx_message_est_lu` sur est_lu

#### 2. Conversation
Représente une conversation privée entre deux utilisateurs.

**Fichier** : `domain/entity/Conversation.java`

**Champs principaux** :
- `participant1` (User) : Premier participant
- `participant2` (User) : Second participant
- `dernierMessageDate` (LocalDateTime) : Date du dernier message
- `dernierMessageContenu` (String) : Aperçu du dernier message
- `messages` (Set<Message>) : Tous les messages de la conversation
- `archivee` (Boolean) : Indicateur d'archivage

**Relations** :
```java
@ManyToOne → User (participant1)
@ManyToOne → User (participant2)
@OneToMany → Message
```

**Index** :
- `idx_conversation_participant1` sur participant1_id
- `idx_conversation_participant2` sur participant2_id
- `idx_conversation_date` sur dernier_message_date

#### 3. GroupeMessage
Représente un groupe de discussion lié à une matière ou un cours.

**Fichier** : `domain/entity/GroupeMessage.java`

**Champs principaux** :
- `nom` (String) : Nom du groupe
- `description` (String) : Description du groupe
- `matiere` (Matiere) : Matière associée (optionnel)
- `cours` (Cours) : Cours associé (optionnel)
- `createur` (User) : Créateur du groupe
- `membres` (Set<User>) : Membres du groupe
- `messages` (Set<Message>) : Messages du groupe
- `actif` (Boolean) : Statut d'activation

**Relations** :
```java
@ManyToOne → Matiere
@ManyToOne → Cours
@ManyToOne → User (créateur)
@ManyToMany → User (membres)
@OneToMany → Message
```

**Index** :
- `idx_groupe_matiere` sur matiere_id
- `idx_groupe_cours` sur cours_id
- `idx_groupe_createur` sur createur_id
- `idx_groupe_actif` sur actif

#### 4. MessageTag
Représente un tag pour catégoriser les messages.

**Fichier** : `domain/entity/MessageTag.java`

**Champs principaux** :
- `nom` (String) : Nom du tag (unique, ex: "urgent", "annonce", "projet")
- `couleur` (String) : Code couleur hexadécimal pour l'affichage
- `description` (String) : Description du tag

**Relations** :
```java
@ManyToMany → Message
```

**Contraintes** :
- `uk_tag_nom` : Nom du tag unique

#### 5. MessageType (Enum)
Type énuméré définissant les types de messages.

**Fichier** : `domain/enums/MessageType.java`

**Valeurs** :
- `PRIVE` : Message privé entre deux utilisateurs
- `GROUPE` : Message dans un groupe de discussion

### DTOs (Data Transfer Objects)

#### 1. MessageDto
Représentation d'un message pour l'API.

**Fichier** : `application/dto/message/MessageDto.java`

**Champs** :
```java
Long id
String contenu
Boolean estLu
LocalDateTime dateEnvoi
String typeMessage
Long expediteurId
String expediteurNom
Long destinataireId
String destinataireNom
Long conversationId
Long groupeId
String groupeNom
Set<String> tags
```

#### 2. SendMessageRequest
Requête pour envoyer un message.

**Fichier** : `application/dto/message/SendMessageRequest.java`

**Champs** :
```java
@NotBlank String contenu
@NotNull Long expediteurId
Long destinataireId  // Pour message privé
Long groupeId        // Pour message de groupe
Set<String> tags
```

**Validation** :
- `contenu` : obligatoire
- `expediteurId` : obligatoire
- Un seul parmi `destinataireId` ou `groupeId` doit être fourni

#### 3. ConversationDto
Représentation d'une conversation pour l'API.

**Fichier** : `application/dto/message/ConversationDto.java`

**Champs** :
```java
Long id
Long participant1Id
String participant1Nom
Long participant2Id
String participant2Nom
LocalDateTime dernierMessageDate
String dernierMessageContenu
Integer nombreMessagesNonLus
Boolean archivee
```

#### 4. GroupeMessageDto
Représentation d'un groupe de discussion pour l'API.

**Fichier** : `application/dto/message/GroupeMessageDto.java`

**Champs** :
```java
Long id
String nom
String description
Long matiereId
String matiereNom
Long coursId
String coursNom
Long createurId
String createurNom
Integer nombreMembres
Integer nombreMessages
Boolean actif
LocalDateTime createdAt
```

#### 5. CreateGroupeRequest
Requête pour créer un nouveau groupe.

**Fichier** : `application/dto/message/CreateGroupeRequest.java`

**Champs** :
```java
@NotBlank String nom
String description
Long matiereId
Long coursId
@NotNull Long createurId
Set<Long> membresIds
```

**Validation** :
- `nom` : obligatoire
- `createurId` : obligatoire
- Au moins un parmi `matiereId` ou `coursId` est recommandé

### Repositories

#### 1. MessageRepository
**Fichier** : `infrastructure/repository/MessageRepository.java`

**Méthodes principales** :
```java
// Récupération par conversation/groupe
List<Message> findByConversationIdOrderByDateEnvoiDesc(Long conversationId)
List<Message> findByGroupeIdOrderByDateEnvoiDesc(Long groupeId)

// Messages non lus
List<Message> findByDestinataireIdAndEstLuFalse(Long destinataireId)
Integer countMessagesNonLus(Long userId)
Integer countMessagesNonLusByConversation(Long conversationId, Long userId)
Integer countMessagesNonLusByGroupe(Long groupeId)

// Recherche par tags
List<Message> findByTagNom(String tagNom)
List<Message> findByTags(List<MessageTag> tags)

// Messages par utilisateur
List<Message> findMessagesPrivesByUser(Long userId)
List<Message> findMessagesGroupeByUser(Long userId)
```

#### 2. ConversationRepository
**Fichier** : `infrastructure/repository/ConversationRepository.java`

**Méthodes principales** :
```java
// Recherche par participants
Optional<Conversation> findByParticipants(Long user1Id, Long user2Id)

// Conversations d'un utilisateur
List<Conversation> findByUserId(Long userId)
List<Conversation> findActiveConversationsByUserId(Long userId)
List<Conversation> findArchivedConversationsByUserId(Long userId)

// Comptage
Integer countByUserId(Long userId)
```

#### 3. GroupeMessageRepository
**Fichier** : `infrastructure/repository/GroupeMessageRepository.java`

**Méthodes principales** :
```java
// Recherche par matière/cours
List<GroupeMessage> findByMatiereId(Long matiereId)
List<GroupeMessage> findByCoursId(Long coursId)
List<GroupeMessage> findActiveByMatiereId(Long matiereId)
List<GroupeMessage> findActiveByCoursId(Long coursId)

// Groupes d'un utilisateur
List<GroupeMessage> findActiveGroupesByMembreId(Long userId)
List<GroupeMessage> findAllGroupesByMembreId(Long userId)

// Vérification de membership
Boolean isUserMembre(Long groupeId, Long userId)

// Statistiques
Integer countMembresByGroupeId(Long groupeId)
Integer countMessagesByGroupeId(Long groupeId)
```

#### 4. MessageTagRepository
**Fichier** : `infrastructure/repository/MessageTagRepository.java`

**Méthodes principales** :
```java
// Recherche par nom
Optional<MessageTag> findByNom(String nom)
List<MessageTag> findByNomIn(Set<String> noms)
Boolean existsByNom(String nom)

// Recherche
List<MessageTag> searchByNom(String keyword)

// Statistiques
Integer countMessagesByTagId(Long tagId)
```

### Services

#### 1. MessageriePriveeService
**Fichier** : `application/service/MessageriePriveeService.java`

**Fonctionnalités** :

##### Envoi de messages
```java
MessageDto envoyerMessagePrive(SendMessageRequest request)
```
- Trouve ou crée la conversation entre expéditeur et destinataire
- Crée le message avec les tags éventuels
- Met à jour la conversation avec le dernier message

##### Gestion des conversations
```java
List<ConversationDto> getConversationsByUser(Long userId)
ConversationDto getConversation(Long conversationId, Long userId)
List<MessageDto> getMessagesConversation(Long conversationId, Long userId)
```

##### Marquage de lecture
```java
void marquerCommeLu(Long messageId, Long userId)
void marquerTousCommeLus(Long conversationId, Long userId)
```

##### Archivage
```java
void archiverConversation(Long conversationId, Long userId)
void desarchiverConversation(Long conversationId, Long userId)
```

##### Messages non lus
```java
Integer getNombreMessagesNonLus(Long userId)
List<MessageDto> getMessagesNonLus(Long userId)
```

##### Suppression
```java
void supprimerMessage(Long messageId, Long userId)
```
- Seul l'expéditeur peut supprimer son message

#### 2. MessagerieGroupeService
**Fichier** : `application/service/MessagerieGroupeService.java`

**Fonctionnalités** :

##### Création et gestion de groupes
```java
GroupeMessageDto creerGroupe(CreateGroupeRequest request)
void modifierGroupe(Long groupeId, String nom, String description, Long userId)
void desactiverGroupe(Long groupeId, Long userId)
void activerGroupe(Long groupeId, Long userId)
void supprimerGroupe(Long groupeId, Long userId)
```
- Seul le créateur peut modifier, désactiver ou supprimer le groupe

##### Envoi de messages
```java
MessageDto envoyerMessageGroupe(SendMessageRequest request)
```
- Vérifie que l'expéditeur est membre du groupe
- Crée le message avec les tags éventuels

##### Récupération de groupes
```java
List<GroupeMessageDto> getGroupesByUser(Long userId)
List<GroupeMessageDto> getGroupesByMatiere(Long matiereId)
List<GroupeMessageDto> getGroupesByCours(Long coursId)
GroupeMessageDto getGroupe(Long groupeId, Long userId)
List<MessageDto> getMessagesGroupe(Long groupeId, Long userId)
```

##### Gestion des membres
```java
void ajouterMembres(Long groupeId, Set<Long> membresIds, Long requesterId)
void retirerMembre(Long groupeId, Long membreId, Long requesterId)
```
- Seul le créateur peut ajouter/retirer des membres
- Un membre peut se retirer lui-même
- Le créateur ne peut pas être retiré

### Controller - API REST

#### MessageController
**Fichier** : `web/controller/MessageController.java`

**Base URL** : `/messages`

**Tag Swagger** : "Messagerie"

---

### API Endpoints - Messagerie Privée

#### 1. Envoyer un message privé
```
POST /messages/prive
```

**Request Body** :
```json
{
  "contenu": "Bonjour, j'ai une question sur le cours",
  "expediteurId": 1,
  "destinataireId": 2,
  "tags": ["urgent"]
}
```

**Response** : `201 Created`
```json
{
  "id": 15,
  "contenu": "Bonjour, j'ai une question sur le cours",
  "estLu": false,
  "dateEnvoi": "2024-01-15T10:30:00",
  "typeMessage": "PRIVE",
  "expediteurId": 1,
  "expediteurNom": "Dupont Jean",
  "destinataireId": 2,
  "destinataireNom": "Martin Sophie",
  "conversationId": 3,
  "tags": ["urgent"]
}
```

#### 2. Récupérer les conversations d'un utilisateur
```
GET /messages/conversations/user/{userId}
```

**Response** : `200 OK`
```json
[
  {
    "id": 3,
    "participant1Id": 1,
    "participant1Nom": "Dupont Jean",
    "participant2Id": 2,
    "participant2Nom": "Martin Sophie",
    "dernierMessageDate": "2024-01-15T10:30:00",
    "dernierMessageContenu": "Bonjour, j'ai une question...",
    "nombreMessagesNonLus": 2,
    "archivee": false
  }
]
```

#### 3. Récupérer une conversation spécifique
```
GET /messages/conversations/{conversationId}/user/{userId}
```

**Response** : `200 OK` - ConversationDto

#### 4. Récupérer les messages d'une conversation
```
GET /messages/conversations/{conversationId}/messages/user/{userId}
```

**Response** : `200 OK` - List<MessageDto>

#### 5. Marquer un message comme lu
```
PUT /messages/{messageId}/lire/user/{userId}
```

**Response** : `204 No Content`

#### 6. Marquer tous les messages d'une conversation comme lus
```
PUT /messages/conversations/{conversationId}/lire-tous/user/{userId}
```

**Response** : `204 No Content`

#### 7. Archiver une conversation
```
PUT /messages/conversations/{conversationId}/archiver/user/{userId}
```

**Response** : `204 No Content`

#### 8. Désarchiver une conversation
```
PUT /messages/conversations/{conversationId}/desarchiver/user/{userId}
```

**Response** : `204 No Content`

#### 9. Récupérer les messages non lus
```
GET /messages/non-lus/user/{userId}
```

**Response** : `200 OK` - List<MessageDto>

#### 10. Compter les messages non lus
```
GET /messages/non-lus/count/user/{userId}
```

**Response** : `200 OK`
```json
5
```

#### 11. Supprimer un message
```
DELETE /messages/{messageId}/user/{userId}
```

**Response** : `204 No Content`

**Note** : Seul l'expéditeur peut supprimer son message.

---

### API Endpoints - Messagerie de Groupe

#### 12. Créer un groupe
```
POST /messages/groupes
```

**Request Body** :
```json
{
  "nom": "Groupe Mathématiques S1",
  "description": "Discussion pour le cours de maths",
  "matiereId": 5,
  "createurId": 1,
  "membresIds": [2, 3, 4]
}
```

**Response** : `201 Created`
```json
{
  "id": 10,
  "nom": "Groupe Mathématiques S1",
  "description": "Discussion pour le cours de maths",
  "matiereId": 5,
  "matiereNom": "Mathématiques Avancées",
  "createurId": 1,
  "createurNom": "Dupont Jean",
  "nombreMembres": 4,
  "nombreMessages": 0,
  "actif": true,
  "createdAt": "2024-01-15T09:00:00"
}
```

#### 13. Envoyer un message dans un groupe
```
POST /messages/groupes/message
```

**Request Body** :
```json
{
  "contenu": "Nouvelle annonce importante !",
  "expediteurId": 1,
  "groupeId": 10,
  "tags": ["annonce"]
}
```

**Response** : `201 Created` - MessageDto

#### 14. Récupérer les groupes d'un utilisateur
```
GET /messages/groupes/user/{userId}
```

**Response** : `200 OK` - List<GroupeMessageDto>

#### 15. Récupérer les groupes d'une matière
```
GET /messages/groupes/matiere/{matiereId}
```

**Response** : `200 OK` - List<GroupeMessageDto>

#### 16. Récupérer les groupes d'un cours
```
GET /messages/groupes/cours/{coursId}
```

**Response** : `200 OK` - List<GroupeMessageDto>

#### 17. Récupérer un groupe spécifique
```
GET /messages/groupes/{groupeId}/user/{userId}
```

**Response** : `200 OK` - GroupeMessageDto

#### 18. Récupérer les messages d'un groupe
```
GET /messages/groupes/{groupeId}/messages/user/{userId}
```

**Response** : `200 OK` - List<MessageDto>

#### 19. Ajouter des membres à un groupe
```
POST /messages/groupes/{groupeId}/membres/user/{userId}
```

**Request Body** :
```json
[5, 6, 7]
```

**Response** : `204 No Content`

**Note** : Seul le créateur ou un membre existant peut ajouter des membres.

#### 20. Retirer un membre d'un groupe
```
DELETE /messages/groupes/{groupeId}/membres/{membreId}/user/{userId}
```

**Response** : `204 No Content`

**Note** : Seul le créateur peut retirer d'autres membres. Un membre peut se retirer lui-même.

#### 21. Désactiver un groupe
```
PUT /messages/groupes/{groupeId}/desactiver/user/{userId}
```

**Response** : `204 No Content`

**Note** : Seul le créateur peut désactiver le groupe.

#### 22. Activer un groupe
```
PUT /messages/groupes/{groupeId}/activer/user/{userId}
```

**Response** : `204 No Content`

**Note** : Seul le créateur peut activer le groupe.

#### 23. Modifier un groupe
```
PUT /messages/groupes/{groupeId}/user/{userId}?nom=Nouveau Nom&description=Nouvelle description
```

**Response** : `204 No Content`

**Note** : Seul le créateur peut modifier le groupe.

#### 24. Supprimer un groupe
```
DELETE /messages/groupes/{groupeId}/user/{userId}
```

**Response** : `204 No Content`

**Note** : Seul le créateur peut supprimer le groupe. Tous les messages du groupe seront également supprimés.

---

## Système de Tags

### Tags Prédéfinis

Il est recommandé de créer les tags suivants au démarrage de l'application :

1. **#urgent**
   - Couleur : `#FF0000` (rouge)
   - Description : Messages nécessitant une attention immédiate

2. **#annonce**
   - Couleur : `#0066CC` (bleu)
   - Description : Annonces officielles

3. **#projet**
   - Couleur : `#00AA00` (vert)
   - Description : Messages liés aux projets

4. **#question**
   - Couleur : `#FF9900` (orange)
   - Description : Questions nécessitant une réponse

5. **#important**
   - Couleur : `#CC0099` (violet)
   - Description : Information importante

### Utilisation des Tags

Les tags peuvent être ajoutés lors de l'envoi d'un message :

```json
{
  "contenu": "Rappel : rendu du projet pour demain",
  "expediteurId": 1,
  "groupeId": 10,
  "tags": ["urgent", "projet"]
}
```

## Règles de Sécurité et de Gestion

### Messagerie Privée

1. **Accès aux conversations** :
   - Un utilisateur ne peut accéder qu'aux conversations dont il est participant
   - Vérification systématique de l'appartenance avant toute opération

2. **Suppression de messages** :
   - Seul l'expéditeur peut supprimer ses messages
   - La suppression est définitive

3. **Marquage de lecture** :
   - Seul le destinataire peut marquer un message comme lu

4. **Archivage** :
   - Chaque participant peut archiver indépendamment la conversation
   - L'archivage n'affecte que la vue de l'utilisateur qui archive

### Messagerie de Groupe

1. **Création de groupes** :
   - Tout utilisateur peut créer un groupe
   - Le créateur devient automatiquement membre du groupe
   - Le créateur a des privilèges spéciaux

2. **Privilèges du créateur** :
   - Modifier le nom et la description
   - Ajouter/retirer des membres
   - Activer/désactiver le groupe
   - Supprimer le groupe

3. **Envoi de messages** :
   - Seuls les membres peuvent envoyer des messages
   - Vérification systématique du membership

4. **Gestion des membres** :
   - Le créateur peut ajouter/retirer des membres
   - Un membre peut se retirer lui-même
   - Le créateur ne peut pas être retiré

5. **Suppression de groupe** :
   - Seul le créateur peut supprimer le groupe
   - Tous les messages sont supprimés avec le groupe

## Cas d'Usage

### Cas d'Usage 1 : Étudiant contacte un enseignant
```
1. L'étudiant envoie un message privé à l'enseignant
   POST /messages/prive

2. L'enseignant reçoit une notification

3. L'enseignant consulte ses messages non lus
   GET /messages/non-lus/user/{enseignantId}

4. L'enseignant ouvre la conversation
   GET /messages/conversations/{conversationId}/messages/user/{enseignantId}

5. Les messages sont marqués comme lus automatiquement
   PUT /messages/conversations/{conversationId}/lire-tous/user/{enseignantId}

6. L'enseignant répond
   POST /messages/prive
```

### Cas d'Usage 2 : Création d'un groupe de travail
```
1. Un étudiant crée un groupe pour une matière
   POST /messages/groupes
   {
     "nom": "Groupe Projet IA",
     "matiereId": 5,
     "createurId": 1,
     "membresIds": [2, 3, 4]
   }

2. Les membres reçoivent une notification

3. Un membre envoie un message dans le groupe
   POST /messages/groupes/message
   {
     "contenu": "Première réunion demain 14h",
     "expediteurId": 2,
     "groupeId": 10,
     "tags": ["annonce"]
   }

4. Tous les membres voient le message
   GET /messages/groupes/{groupeId}/messages/user/{membreId}
```

### Cas d'Usage 3 : Annonce urgente dans un groupe
```
1. L'enseignant envoie une annonce urgente
   POST /messages/groupes/message
   {
     "contenu": "Examen reporté au 20 janvier",
     "expediteurId": 1,
     "groupeId": 10,
     "tags": ["urgent", "annonce"]
   }

2. Les étudiants consultent les messages urgents
   Filtrage côté client sur les messages avec tag "urgent"

3. L'annonce est visible pour tous les membres du groupe
```

## Optimisations et Performance

### Index de Base de Données

Tous les champs fréquemment utilisés dans les requêtes sont indexés :
- ID des expéditeurs/destinataires
- ID des conversations/groupes
- Dates d'envoi
- Status de lecture
- Membership des groupes

### Requêtes Optimisées

Les repositories utilisent des requêtes JPQL optimisées :
- Jointures efficaces pour réduire les requêtes N+1
- Comptage via COUNT au lieu de récupération complète
- Tri au niveau de la base de données

### Pagination

Pour de grandes quantités de messages, il est recommandé d'implémenter la pagination :
```java
Page<Message> findByConversationIdOrderByDateEnvoiDesc(
    Long conversationId, Pageable pageable
)
```

## Évolutions Possibles

### Court Terme
1. **Notifications en temps réel** : WebSocket pour notifications instantanées
2. **Pièces jointes** : Support de fichiers attachés aux messages
3. **Réactions** : Ajout de réactions emoji aux messages
4. **Recherche** : Recherche full-text dans les messages

### Moyen Terme
1. **Messages vocaux** : Enregistrement et envoi de messages audio
2. **Appels vidéo** : Intégration WebRTC pour les appels
3. **Traduction automatique** : Messages multilingues
4. **Modération automatique** : Détection de contenu inapproprié

### Long Terme
1. **Chatbot IA** : Assistant virtuel pour répondre aux questions
2. **Analyse de sentiment** : Détection des émotions dans les messages
3. **Résumés automatiques** : IA pour résumer les longues conversations
4. **Suggestions intelligentes** : Réponses suggérées basées sur le contexte

## Statistiques et Métriques

### Métriques à Suivre
1. **Volume de messages** :
   - Nombre total de messages par jour/semaine/mois
   - Messages privés vs. messages de groupe
   - Distribution par tag

2. **Engagement utilisateur** :
   - Nombre d'utilisateurs actifs
   - Temps de réponse moyen
   - Taux de lecture des messages

3. **Performance des groupes** :
   - Nombre de groupes actifs
   - Taille moyenne des groupes
   - Messages par groupe

4. **Qualité du service** :
   - Temps de latence des messages
   - Taux de succès des envois
   - Taux d'erreur

## Schéma de Base de Données

```
┌─────────────┐         ┌──────────────┐         ┌─────────────┐
│   User      │◄────────┤  Message     │────────►│ MessageTag  │
│             │         │              │         │             │
│ - id        │         │ - id         │         │ - id        │
│ - username  │         │ - contenu    │         │ - nom       │
│ - email     │         │ - estLu      │         │ - couleur   │
└─────────────┘         │ - dateEnvoi  │         └─────────────┘
      ▲                 │ - type       │
      │                 └──────────────┘
      │                    │         │
      │                    │         │
      │         ┌──────────┘         └──────────┐
      │         │                                │
      │    ┌────▼──────────┐           ┌────────▼────────┐
      │    │ Conversation  │           │ GroupeMessage   │
      │    │               │           │                 │
      └────┤ - id          │           │ - id            │
           │ - participant1│           │ - nom           │
           │ - participant2│           │ - description   │
           │ - dernierMsg  │           │ - actif         │
           │ - archivee    │           │ - matiere       │
           └───────────────┘           │ - cours         │
                                       │ - createur      │
                                       └─────────────────┘
```

## Résumé des Endpoints

### Messagerie Privée (11 endpoints)
1. POST `/messages/prive` - Envoyer message
2. GET `/messages/conversations/user/{userId}` - Lister conversations
3. GET `/messages/conversations/{conversationId}/user/{userId}` - Détails conversation
4. GET `/messages/conversations/{conversationId}/messages/user/{userId}` - Messages conversation
5. PUT `/messages/{messageId}/lire/user/{userId}` - Marquer message lu
6. PUT `/messages/conversations/{conversationId}/lire-tous/user/{userId}` - Marquer tous lus
7. PUT `/messages/conversations/{conversationId}/archiver/user/{userId}` - Archiver
8. PUT `/messages/conversations/{conversationId}/desarchiver/user/{userId}` - Désarchiver
9. GET `/messages/non-lus/user/{userId}` - Messages non lus
10. GET `/messages/non-lus/count/user/{userId}` - Compter non lus
11. DELETE `/messages/{messageId}/user/{userId}` - Supprimer message

### Messagerie de Groupe (13 endpoints)
12. POST `/messages/groupes` - Créer groupe
13. POST `/messages/groupes/message` - Envoyer message groupe
14. GET `/messages/groupes/user/{userId}` - Groupes d'un utilisateur
15. GET `/messages/groupes/matiere/{matiereId}` - Groupes par matière
16. GET `/messages/groupes/cours/{coursId}` - Groupes par cours
17. GET `/messages/groupes/{groupeId}/user/{userId}` - Détails groupe
18. GET `/messages/groupes/{groupeId}/messages/user/{userId}` - Messages groupe
19. POST `/messages/groupes/{groupeId}/membres/user/{userId}` - Ajouter membres
20. DELETE `/messages/groupes/{groupeId}/membres/{membreId}/user/{userId}` - Retirer membre
21. PUT `/messages/groupes/{groupeId}/desactiver/user/{userId}` - Désactiver
22. PUT `/messages/groupes/{groupeId}/activer/user/{userId}` - Activer
23. PUT `/messages/groupes/{groupeId}/user/{userId}` - Modifier groupe
24. DELETE `/messages/groupes/{groupeId}/user/{userId}` - Supprimer groupe

**Total : 24 endpoints**

## Conclusion

Le module de messagerie interne de CampusMaster offre une solution complète et robuste pour la communication au sein de la plateforme éducative. Il est conçu avec les meilleures pratiques en matière de :

- **Sécurité** : Vérifications d'accès systématiques
- **Performance** : Indexation optimale et requêtes efficaces
- **Extensibilité** : Architecture modulaire et évolutive
- **Maintenabilité** : Code propre et bien documenté

Le système est prêt pour une utilisation en production et peut facilement être étendu avec de nouvelles fonctionnalités selon les besoins futurs.
