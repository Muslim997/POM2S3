# Module de Notifications Push - Documentation Complète

## Vue d'ensemble

Le module de notifications push de CampusMaster permet d'envoyer des notifications en temps réel aux utilisateurs via trois canaux :

- **EMAIL** : Notifications par email avec templates HTML personnalisés
- **WEB_PUSH** : Notifications en temps réel via WebSocket
- **IN_APP** : Notifications dans l'application consultables dans l'interface

Les notifications sont déclenchées automatiquement suite à des événements clés de la plateforme.

## Architecture

### Enums

#### 1. NotificationChannel
Définit les canaux de notification disponibles.

**Fichier** : `domain/enums/NotificationChannel.java`

**Valeurs** :
- `EMAIL` : Envoi par email
- `WEB_PUSH` : Notification WebSocket en temps réel
- `IN_APP` : Notification in-app (consultable dans l'interface)

#### 2. NotificationEvent
Définit les types d'événements déclenchant des notifications.

**Fichier** : `domain/enums/NotificationEvent.java`

**Valeurs** :
```java
DEVOIR_PUBLIE        // Nouveau devoir publié
DEVOIR_CORRIGE       // Devoir corrigé avec note
NOUVEAU_MESSAGE      // Nouveau message privé reçu
DEADLINE_PROCHE      // Deadline de devoir proche
NOUVEAU_COURS        // Nouveau contenu de cours
NOUVELLE_ANNONCE     // Nouvelle annonce publiée
NOUVELLE_INSCRIPTION // Nouvelle inscription à un cours
GROUPE_CREE          // Nouveau groupe de discussion créé
MEMBRE_AJOUTE        // Ajouté à un groupe
```

#### 3. NotificationPriority
Définit le niveau de priorité des notifications.

**Fichier** : `domain/enums/NotificationPriority.java`

**Valeurs** :
- `LOW` : Priorité basse
- `NORMAL` : Priorité normale (par défaut)
- `HIGH` : Priorité haute
- `URGENT` : Priorité urgente

### Entités du Domaine

#### 1. NotificationPush
Entité principale représentant une notification.

**Fichier** : `domain/entity/NotificationPush.java`

**Champs principaux** :
- `user` (User) : Destinataire de la notification
- `title` (String) : Titre de la notification
- `message` (String) : Contenu du message
- `eventType` (NotificationEvent) : Type d'événement
- `channel` (NotificationChannel) : Canal de diffusion
- `priority` (NotificationPriority) : Niveau de priorité
- `sent` (Boolean) : Statut d'envoi
- `sentAt` (LocalDateTime) : Date/heure d'envoi
- `scheduledAt` (LocalDateTime) : Date/heure planifiée (optionnel)
- `entityType` (String) : Type d'entité liée
- `entityId` (Long) : ID de l'entité liée
- `actionUrl` (String) : URL d'action pour la notification
- `errorMessage` (String) : Message d'erreur si échec
- `retryCount` (Integer) : Nombre de tentatives de renvoi

**Relations** :
```java
@ManyToOne → User
```

**Index** :
- `idx_notif_push_user` sur user_id
- `idx_notif_push_event` sur event_type
- `idx_notif_push_sent` sur sent
- `idx_notif_push_channel` sur channel
- `idx_notif_push_scheduled` sur scheduled_at

#### 2. NotificationPreference
Préférences de notification par utilisateur, événement et canal.

**Fichier** : `domain/entity/NotificationPreference.java`

**Champs principaux** :
- `user` (User) : Utilisateur
- `eventType` (NotificationEvent) : Type d'événement
- `channel` (NotificationChannel) : Canal de notification
- `enabled` (Boolean) : Activé/Désactivé

**Relations** :
```java
@ManyToOne → User
```

**Contraintes** :
- `uk_notif_pref_user_event_channel` : Unique par (user_id, event_type, channel)

**Index** :
- `idx_notif_pref_user` sur user_id

### DTOs (Data Transfer Objects)

#### 1. NotificationPushDto
Représentation complète d'une notification.

**Fichier** : `application/dto/notification/NotificationPushDto.java`

**Champs** :
```java
Long id
Long userId
String userName
String title
String message
String eventType
String channel
String priority
Boolean sent
LocalDateTime sentAt
LocalDateTime scheduledAt
String entityType
Long entityId
String actionUrl
String errorMessage
Integer retryCount
LocalDateTime createdAt
```

#### 2. SendNotificationRequest
Requête pour envoyer une notification manuellement.

**Fichier** : `application/dto/notification/SendNotificationRequest.java`

**Champs** :
```java
@NotNull Long userId
@NotBlank String title
@NotBlank String message
@NotBlank String eventType
@NotNull Set<String> channels
String priority
LocalDateTime scheduledAt
String entityType
Long entityId
String actionUrl
```

#### 3. NotificationPreferenceDto
Représentation d'une préférence de notification.

**Fichier** : `application/dto/notification/NotificationPreferenceDto.java`

**Champs** :
```java
Long id
Long userId
String eventType
String channel
Boolean enabled
```

#### 4. WebSocketNotificationDto
DTO optimisé pour les notifications WebSocket.

**Fichier** : `application/dto/notification/WebSocketNotificationDto.java`

**Champs** :
```java
Long id
String title
String message
String eventType
String priority
String entityType
Long entityId
String actionUrl
LocalDateTime timestamp
```

### Repositories

#### 1. NotificationPushRepository
**Fichier** : `infrastructure/persistence/repository/NotificationPushRepository.java`

**Méthodes principales** :
```java
// Récupération par utilisateur
List<NotificationPush> findByUserIdOrderByCreatedAtDesc(Long userId)
List<NotificationPush> findByUserIdAndSentFalseOrderByCreatedAtDesc(Long userId)

// Notifications en attente
List<NotificationPush> findPendingNotifications(LocalDateTime now)
Integer countPendingByUserId(Long userId)

// Par événement/canal
List<NotificationPush> findByEventTypeAndSentTrue(NotificationEvent eventType)
List<NotificationPush> findByChannelAndSentFalse(NotificationChannel channel)

// Notifications récentes
List<NotificationPush> findRecentNotifications(Long userId, LocalDateTime since)

// Gestion des échecs
List<NotificationPush> findFailedNotificationsForRetry(Integer maxRetries)

// Par entité liée
List<NotificationPush> findByEntity(String entityType, Long entityId)

// Nettoyage
void deleteByUserIdAndSentTrueAndSentAtBefore(Long userId, LocalDateTime before)
```

#### 2. NotificationPreferenceRepository
**Fichier** : `infrastructure/persistence/repository/NotificationPreferenceRepository.java`

**Méthodes principales** :
```java
// Préférences utilisateur
List<NotificationPreference> findByUserId(Long userId)

// Préférence spécifique
Optional<NotificationPreference> findByUserIdAndEventTypeAndChannel(
    Long userId, NotificationEvent eventType, NotificationChannel channel)

// Préférences activées
List<NotificationPreference> findEnabledPreferences(Long userId, NotificationEvent eventType)
List<NotificationChannel> findEnabledChannels(Long userId, NotificationEvent eventType)

// Suppression
void deleteByUserId(Long userId)
```

### Services

#### 1. EmailService
**Fichier** : `application/service/EmailService.java`

Service d'envoi d'emails avec templates HTML.

**Méthodes principales** :

##### Envoi générique
```java
void sendSimpleEmail(String to, String subject, String text)
void sendHtmlEmail(String to, String subject, String htmlContent)
void sendNotificationEmail(User user, NotificationPush notification)
```

##### Envois spécifiques
```java
void sendDevoirPublieEmail(User user, String devoirTitre, String coursNom, String deadline)
void sendDevoirCorrigeEmail(User user, String devoirTitre, Double note)
void sendDeadlineProche(User user, String devoirTitre, String deadline)
```

**Templates email** :
- Template HTML responsive avec styles intégrés
- Couleurs selon la priorité (URGENT: rouge, HIGH: orange, NORMAL: bleu, LOW: gris)
- Bouton d'action si URL fournie
- Footer avec lien vers préférences

**Caractéristiques** :
- Envoi asynchrone avec `@Async`
- Gestion des erreurs avec logging
- Templates personnalisables

#### 2. WebSocketNotificationService
**Fichier** : `application/service/WebSocketNotificationService.java`

Service de notifications en temps réel via WebSocket.

**Méthodes principales** :

##### Envoi à un utilisateur
```java
void sendNotificationToUser(Long userId, NotificationPush notification)
```
- Destination : `/user/{userId}/queue/notifications`

##### Broadcast à tous
```java
void sendBroadcastNotification(NotificationPush notification)
```
- Destination : `/topic/notifications`

##### Notification personnalisée
```java
void sendCustomNotification(Long userId, String title, String message, String eventType)
```

##### Compteur de notifications
```java
void sendUnreadCountUpdate(Long userId, Integer unreadCount)
```
- Destination : `/user/{userId}/queue/unread-count`

**Architecture WebSocket** :
- Utilise STOMP over WebSocket
- SockJS comme fallback pour anciens navigateurs
- Destinations privées par utilisateur (`/user/{userId}/queue/...`)
- Destinations publiques pour broadcasts (`/topic/...`)

#### 3. NotificationEventService
**Fichier** : `application/service/NotificationEventService.java`

Service central pour déclencher les notifications selon les événements.

**Gestionnaires d'événements** :

##### 1. Devoir publié
```java
void onDevoirPublie(Devoir devoir)
```
- Notifie tous les étudiants inscrits au cours
- Canaux : EMAIL, WEB_PUSH, IN_APP
- Priorité : HIGH
- Contenu : Titre du devoir, cours, deadline

##### 2. Devoir corrigé
```java
void onDevoirCorrige(Submit submit)
```
- Notifie l'étudiant concerné
- Canaux : EMAIL, WEB_PUSH, IN_APP
- Priorité : NORMAL
- Contenu : Titre du devoir, note obtenue

##### 3. Nouveau message
```java
void onNouveauMessage(Message message)
```
- Notifie le destinataire
- Canaux : WEB_PUSH, IN_APP (pas d'email par défaut)
- Priorité : NORMAL
- Contenu : Expéditeur, aperçu du message

##### 4. Deadline proche
```java
void onDeadlineProche(Devoir devoir, User etudiant)
```
- Notifie l'étudiant
- Canaux : EMAIL, WEB_PUSH, IN_APP
- Priorité : URGENT
- Contenu : Devoir, temps restant

##### 5. Nouveau cours
```java
void onNouveauCours(Cours cours)
```
- Notifie les étudiants inscrits
- Canaux : IN_APP
- Priorité : LOW
- Contenu : Titre du cours

##### 6. Nouvelle annonce
```java
void onNouvelleAnnonce(Annonce annonce)
```
- Notifie les étudiants du cours
- Canaux : WEB_PUSH, IN_APP
- Priorité : HIGH
- Contenu : Titre et contenu de l'annonce

**Fonctionnalités** :
- Vérification des préférences utilisateur
- Envoi asynchrone avec `@Async`
- Support multi-canaux
- Gestion des erreurs par canal
- Tracking de l'envoi (sent, sentAt)

**Méthodes utilitaires** :
```java
List<NotificationPushDto> getUserNotifications(Long userId)
Integer getUnreadCount(Long userId)
NotificationPushDto toDto(NotificationPush notification)
```

#### 4. NotificationPreferenceService
**Fichier** : `application/service/NotificationPreferenceService.java`

Service de gestion des préférences de notification.

**Méthodes principales** :

##### Récupération
```java
List<NotificationPreferenceDto> getUserPreferences(Long userId)
```

##### Mise à jour
```java
NotificationPreferenceDto updatePreference(
    Long userId, NotificationEvent eventType,
    NotificationChannel channel, Boolean enabled)
```

##### Toggle
```java
NotificationPreferenceDto togglePreference(Long preferenceId)
```

##### Suppression
```java
void deletePreference(Long preferenceId)
```

##### Création des préférences par défaut
```java
List<NotificationPreferenceDto> createDefaultPreferences(Long userId)
```

**Préférences par défaut** :
- **Événements importants** (DEVOIR_PUBLIE, DEADLINE_PROCHE, NOUVELLE_ANNONCE) :
  - EMAIL : Activé
  - WEB_PUSH : Activé
  - IN_APP : Activé

- **Autres événements** (DEVOIR_CORRIGE, NOUVEAU_MESSAGE, etc.) :
  - EMAIL : Désactivé
  - WEB_PUSH : Activé
  - IN_APP : Activé

### Configuration

#### 1. WebSocketConfig
**Fichier** : `infrastructure/config/WebSocketConfig.java`

Configuration des WebSockets avec STOMP.

**Configuration** :
```java
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    // Broker pour /topic (broadcast) et /queue (privé)
    config.enableSimpleBroker("/topic", "/queue")

    // Préfixe pour messages client → serveur
    config.setApplicationDestinationPrefixes("/app")

    // Préfixe pour destinations privées
    config.setUserDestinationPrefix("/user")

    // Endpoint WebSocket
    registry.addEndpoint("/ws")
            .setAllowedOriginPatterns("*")
            .withSockJS()
}
```

**Endpoints** :
- Connexion WebSocket : `ws://localhost:8080/ws`
- Avec SockJS : `http://localhost:8080/ws`

**Destinations** :
- Broadcast : `/topic/notifications`
- Privé : `/user/{userId}/queue/notifications`
- Compteur : `/user/{userId}/queue/unread-count`

#### 2. AsyncConfig
**Fichier** : `infrastructure/config/AsyncConfig.java`

Configuration pour l'exécution asynchrone des notifications.

**Configuration** :
```java
@Configuration
@EnableAsync
public class AsyncConfig implements AsyncConfigurer {

    ThreadPoolTaskExecutor executor
    - corePoolSize: 5
    - maxPoolSize: 10
    - queueCapacity: 25
    - threadNamePrefix: "NotificationAsync-"
}
```

### Controller - API REST

#### NotificationPushController
**Fichier** : `web/controller/NotificationPushController.java`

**Base URL** : `/notifications`

**Tag Swagger** : "Notifications Push"

---

### API Endpoints - Notifications

#### 1. Récupérer les notifications d'un utilisateur
```
GET /notifications/user/{userId}
```

**Response** : `200 OK`
```json
[
  {
    "id": 1,
    "userId": 5,
    "userName": "Dupont Jean",
    "title": "Nouveau devoir : Projet IA",
    "message": "Un nouveau devoir a été publié dans le cours 'Intelligence Artificielle'. Date limite : 2024-02-01",
    "eventType": "DEVOIR_PUBLIE",
    "channel": "EMAIL",
    "priority": "HIGH",
    "sent": true,
    "sentAt": "2024-01-15T10:30:00",
    "entityType": "Devoir",
    "entityId": 12,
    "actionUrl": "/devoirs/12",
    "createdAt": "2024-01-15T10:29:55"
  }
]
```

#### 2. Compter les notifications non lues
```
GET /notifications/user/{userId}/unread/count
```

**Response** : `200 OK`
```json
5
```

#### 3. Envoyer une notification manuelle
```
POST /notifications/send
```

**Request Body** :
```json
{
  "userId": 5,
  "title": "Rappel important",
  "message": "N'oubliez pas la réunion de demain à 14h",
  "eventType": "NOUVELLE_ANNONCE",
  "channels": ["EMAIL", "WEB_PUSH"],
  "priority": "HIGH",
  "actionUrl": "/events/123"
}
```

**Response** : `201 Created`
```json
"Notification envoyée avec succès"
```

---

### API Endpoints - Préférences

#### 4. Récupérer les préférences d'un utilisateur
```
GET /notifications/preferences/user/{userId}
```

**Response** : `200 OK`
```json
[
  {
    "id": 1,
    "userId": 5,
    "eventType": "DEVOIR_PUBLIE",
    "channel": "EMAIL",
    "enabled": true
  },
  {
    "id": 2,
    "userId": 5,
    "eventType": "DEVOIR_PUBLIE",
    "channel": "WEB_PUSH",
    "enabled": true
  }
]
```

#### 5. Mettre à jour une préférence
```
POST /notifications/preferences/user/{userId}?eventType=DEVOIR_PUBLIE&channel=EMAIL&enabled=false
```

**Response** : `200 OK`
```json
{
  "id": 1,
  "userId": 5,
  "eventType": "DEVOIR_PUBLIE",
  "channel": "EMAIL",
  "enabled": false
}
```

#### 6. Activer/Désactiver une préférence
```
PUT /notifications/preferences/{preferenceId}/toggle
```

**Response** : `200 OK` - NotificationPreferenceDto

#### 7. Supprimer une préférence
```
DELETE /notifications/preferences/{preferenceId}
```

**Response** : `204 No Content`

#### 8. Créer les préférences par défaut
```
POST /notifications/preferences/user/{userId}/default
```

**Response** : `201 Created` - List<NotificationPreferenceDto>

#### 9. Récupérer les statistiques
```
GET /notifications/stats/user/{userId}
```

**Response** : `200 OK`

---

## Utilisation - Côté Frontend

### Connexion WebSocket avec SockJS et STOMP

#### JavaScript (avec SockJS et STOMP.js)
```javascript
// Installation
// npm install sockjs-client @stomp/stompjs

import SockJS from 'sockjs-client';
import { Client } from '@stomp/stompjs';

// Connexion
const socket = new SockJS('http://localhost:8080/ws');
const stompClient = new Client({
  webSocketFactory: () => socket,
  debug: (str) => console.log(str),
  reconnectDelay: 5000,
  heartbeatIncoming: 4000,
  heartbeatOutgoing: 4000
});

// Connexion
stompClient.onConnect = (frame) => {
  console.log('Connected: ' + frame);

  // S'abonner aux notifications privées
  const userId = 5; // ID de l'utilisateur connecté
  stompClient.subscribe(`/user/${userId}/queue/notifications`, (notification) => {
    const data = JSON.parse(notification.body);
    console.log('Nouvelle notification:', data);

    // Afficher la notification dans l'UI
    showNotification(data);
  });

  // S'abonner au compteur de notifications
  stompClient.subscribe(`/user/${userId}/queue/unread-count`, (message) => {
    const count = JSON.parse(message.body);
    updateUnreadBadge(count);
  });

  // S'abonner aux broadcasts (optionnel)
  stompClient.subscribe('/topic/notifications', (notification) => {
    const data = JSON.parse(notification.body);
    console.log('Notification broadcast:', data);
  });
};

stompClient.onStompError = (frame) => {
  console.error('Broker error: ' + frame.headers['message']);
  console.error('Details: ' + frame.body);
};

stompClient.activate();

// Fonctions d'affichage
function showNotification(notification) {
  // Utiliser une bibliothèque comme Toastify, SweetAlert, etc.
  const toast = document.createElement('div');
  toast.className = `notification priority-${notification.priority.toLowerCase()}`;
  toast.innerHTML = `
    <h3>${notification.title}</h3>
    <p>${notification.message}</p>
    ${notification.actionUrl ? `<a href="${notification.actionUrl}">Voir</a>` : ''}
  `;
  document.body.appendChild(toast);

  setTimeout(() => toast.remove(), 5000);
}

function updateUnreadBadge(count) {
  const badge = document.getElementById('notif-badge');
  badge.textContent = count;
  badge.style.display = count > 0 ? 'block' : 'none';
}
```

#### React Example
```jsx
import { useEffect, useState } from 'react';
import SockJS from 'sockjs-client';
import { Client } from '@stomp/stompjs';

function NotificationComponent({ userId }) {
  const [notifications, setNotifications] = useState([]);
  const [unreadCount, setUnreadCount] = useState(0);
  const [stompClient, setStompClient] = useState(null);

  useEffect(() => {
    const socket = new SockJS('http://localhost:8080/ws');
    const client = new Client({
      webSocketFactory: () => socket,
      onConnect: () => {
        // S'abonner aux notifications
        client.subscribe(`/user/${userId}/queue/notifications`, (message) => {
          const notification = JSON.parse(message.body);
          setNotifications(prev => [notification, ...prev]);
        });

        // S'abonner au compteur
        client.subscribe(`/user/${userId}/queue/unread-count`, (message) => {
          const count = JSON.parse(message.body);
          setUnreadCount(count);
        });
      }
    });

    client.activate();
    setStompClient(client);

    return () => {
      if (client) client.deactivate();
    };
  }, [userId]);

  return (
    <div className="notification-center">
      <div className="notification-badge">{unreadCount}</div>
      <div className="notification-list">
        {notifications.map(notif => (
          <div key={notif.id} className={`notification priority-${notif.priority}`}>
            <h4>{notif.title}</h4>
            <p>{notif.message}</p>
            {notif.actionUrl && <a href={notif.actionUrl}>Voir détails</a>}
          </div>
        ))}
      </div>
    </div>
  );
}
```

### Gestion des préférences

```javascript
// Récupérer les préférences
async function getUserPreferences(userId) {
  const response = await fetch(`/notifications/preferences/user/${userId}`);
  return await response.json();
}

// Modifier une préférence
async function togglePreference(userId, eventType, channel, enabled) {
  const response = await fetch(
    `/notifications/preferences/user/${userId}?eventType=${eventType}&channel=${channel}&enabled=${enabled}`,
    { method: 'POST' }
  );
  return await response.json();
}

// Créer les préférences par défaut
async function createDefaultPreferences(userId) {
  const response = await fetch(
    `/notifications/preferences/user/${userId}/default`,
    { method: 'POST' }
  );
  return await response.json();
}
```

## Déclenchement Automatique

### Intégration dans les services existants

Pour déclencher automatiquement les notifications, il faut appeler le `NotificationEventService` depuis les services métier :

#### Exemple : DevoirService
```java
@Service
@RequiredArgsConstructor
public class DevoirService {

    private final DevoirRepository devoirRepository;
    private final NotificationEventService notificationEventService;

    @Transactional
    public Devoir publierDevoir(Long coursId, CreateDevoirRequest request) {
        // Créer le devoir
        Devoir devoir = // ... création du devoir
        devoir = devoirRepository.save(devoir);

        // Déclencher les notifications
        notificationEventService.onDevoirPublie(devoir);

        return devoir;
    }

    @Transactional
    public Submit corrigerDevoir(Long submitId, Double note, String commentaire) {
        Submit submit = // ... correction du devoir
        submit = submitRepository.save(submit);

        // Déclencher les notifications
        notificationEventService.onDevoirCorrige(submit);

        return submit;
    }
}
```

#### Exemple : MessageriePriveeService
```java
@Service
@RequiredArgsConstructor
public class MessageriePriveeService {

    private final MessageRepository messageRepository;
    private final NotificationEventService notificationEventService;

    @Transactional
    public MessageDto envoyerMessagePrive(SendMessageRequest request) {
        Message message = // ... création du message
        message = messageRepository.save(message);

        // Déclencher les notifications
        notificationEventService.onNouveauMessage(message);

        return toMessageDto(message);
    }
}
```

### Scheduled Task pour Deadlines Proches

Créer un service pour vérifier les deadlines proches :

```java
@Service
@RequiredArgsConstructor
public class DeadlineReminderService {

    private final DevoirRepository devoirRepository;
    private final SubmitRepository submitRepository;
    private final InscriptionRepository inscriptionRepository;
    private final NotificationEventService notificationEventService;

    // Exécuté tous les jours à 8h
    @Scheduled(cron = "0 0 8 * * ?")
    public void checkUpcomingDeadlines() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime twoDaysLater = now.plusDays(2);

        // Trouver les devoirs avec deadline dans les 2 prochains jours
        List<Devoir> devoirsProches = devoirRepository
            .findByDateLimiteBetween(now, twoDaysLater);

        for (Devoir devoir : devoirsProches) {
            // Trouver les étudiants qui n'ont pas encore rendu
            List<Inscription> inscriptions = inscriptionRepository
                .findByCoursId(devoir.getCours().getId());

            for (Inscription inscription : inscriptions) {
                User etudiant = inscription.getEtudiant().getUser();

                // Vérifier si l'étudiant a déjà rendu
                boolean dejaRendu = submitRepository
                    .existsByDevoirIdAndEtudiantId(devoir.getId(), inscription.getEtudiant().getId());

                if (!dejaRendu) {
                    // Envoyer la notification de rappel
                    notificationEventService.onDeadlineProche(devoir, etudiant);
                }
            }
        }
    }
}
```

## Configuration Email (application.properties)

```properties
# Configuration Email SMTP
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=votre-email@gmail.com
spring.mail.password=votre-app-password
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
spring.mail.from=noreply@campusmaster.com

# Nom de l'application pour les emails
app.name=CampusMaster
```

**Pour Gmail** :
1. Activer l'authentification à 2 facteurs
2. Générer un "Mot de passe d'application"
3. Utiliser ce mot de passe dans `spring.mail.password`

**Alternatives** :
- **SendGrid** : Service d'envoi d'emails professionnel
- **Amazon SES** : Service AWS pour emails
- **Mailgun** : Service d'emailing

## Schéma de Base de Données

```
┌─────────────────────┐         ┌────────────────────────────┐
│   User              │◄────────┤  NotificationPush          │
│                     │         │                            │
│ - id                │         │ - id                       │
│ - email             │         │ - user_id                  │
│ - firstName         │         │ - title                    │
│ - lastName          │         │ - message                  │
└─────────────────────┘         │ - event_type               │
         ▲                      │ - channel                  │
         │                      │ - priority                 │
         │                      │ - sent                     │
         │                      │ - sent_at                  │
         │                      │ - scheduled_at             │
         │                      │ - entity_type              │
┌────────┴──────────────┐       │ - entity_id                │
│ NotificationPreference│       │ - action_url               │
│                       │       │ - error_message            │
│ - id                  │       │ - retry_count              │
│ - user_id             │       └────────────────────────────┘
│ - event_type          │
│ - channel             │
│ - enabled             │
└───────────────────────┘
```

## Résumé des Endpoints

### Notifications (3 endpoints)
1. GET `/notifications/user/{userId}` - Lister notifications
2. GET `/notifications/user/{userId}/unread/count` - Compter non lues
3. POST `/notifications/send` - Envoyer manuelle

### Préférences (6 endpoints)
4. GET `/notifications/preferences/user/{userId}` - Lister préférences
5. POST `/notifications/preferences/user/{userId}` - Créer/Modifier préférence
6. PUT `/notifications/preferences/{preferenceId}/toggle` - Toggle
7. DELETE `/notifications/preferences/{preferenceId}` - Supprimer
8. POST `/notifications/preferences/user/{userId}/default` - Créer défaut
9. GET `/notifications/stats/user/{userId}` - Statistiques

**Total : 9 endpoints**

## Événements Supportés

| Événement | Canaux par défaut | Priorité | Déclencheur |
|-----------|-------------------|----------|-------------|
| DEVOIR_PUBLIE | EMAIL, WEB_PUSH, IN_APP | HIGH | Création d'un devoir |
| DEVOIR_CORRIGE | EMAIL, WEB_PUSH, IN_APP | NORMAL | Submit corrigé |
| NOUVEAU_MESSAGE | WEB_PUSH, IN_APP | NORMAL | Message reçu |
| DEADLINE_PROCHE | EMAIL, WEB_PUSH, IN_APP | URGENT | Scheduled task |
| NOUVEAU_COURS | IN_APP | LOW | Contenu ajouté |
| NOUVELLE_ANNONCE | WEB_PUSH, IN_APP | HIGH | Annonce publiée |
| NOUVELLE_INSCRIPTION | IN_APP | NORMAL | Inscription créée |
| GROUPE_CREE | WEB_PUSH, IN_APP | NORMAL | Groupe créé |
| MEMBRE_AJOUTE | WEB_PUSH, IN_APP | NORMAL | Ajout au groupe |

## Évolutions Possibles

### Court Terme
1. **Push Notifications Web** : Support des notifications navigateur via Web Push API
2. **Notifications SMS** : Intégration Twilio pour SMS critiques
3. **Templates personnalisables** : Éditeur de templates email
4. **Statistiques avancées** : Taux d'ouverture, clics, conversions

### Moyen Terme
1. **Notifications conditionnelles** : Règles métier complexes
2. **Digest quotidien/hebdomadaire** : Résumé des notifications
3. **Canaux additionnels** : Slack, Microsoft Teams, Discord
4. **A/B Testing** : Test de templates différents

### Long Terme
1. **IA pour optimisation** : Meilleur moment d'envoi basé sur engagement
2. **Notifications multi-langues** : Support i18n
3. **Prédictions** : Notifications proactives basées sur comportement
4. **Analytics temps réel** : Dashboard de monitoring

## Sécurité et Bonnes Pratiques

### Sécurité
1. **Validation** : Tous les inputs sont validés
2. **CORS** : Configurer correctement les origines autorisées pour WebSocket
3. **Rate limiting** : Limiter le nombre de notifications par utilisateur
4. **Sanitization** : Nettoyer les contenus HTML dans les emails

### Performance
1. **Async** : Envois asynchrones pour ne pas bloquer les requêtes
2. **Batch** : Regrouper les envois emails quand possible
3. **Caching** : Cache des préférences utilisateur
4. **Index** : Index sur toutes les colonnes fréquemment recherchées

### Monitoring
1. **Logging** : Logger tous les envois et échecs
2. **Métriques** : Suivre taux de succès/échec par canal
3. **Alertes** : Alertes si taux d'échec > seuil
4. **Retention** : Archiver/supprimer anciennes notifications

## Conclusion

Le module de notifications push de CampusMaster offre un système complet et robuste pour :

- **Multi-canal** : Email, WebSocket, In-App
- **Temps réel** : Notifications instantanées via WebSocket
- **Personnalisable** : Préférences par utilisateur, événement et canal
- **Événements automatiques** : Déclenchement sur 9 types d'événements
- **Scalable** : Architecture asynchrone et performante
- **Extensible** : Facile d'ajouter de nouveaux canaux ou événements

Le système est prêt pour la production et peut être facilement étendu selon les besoins futurs.
