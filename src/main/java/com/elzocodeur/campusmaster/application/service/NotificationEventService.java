package com.elzocodeur.campusmaster.application.service;

import com.elzocodeur.campusmaster.application.dto.notification.NotificationPushDto;
import com.elzocodeur.campusmaster.domain.entity.*;
import com.elzocodeur.campusmaster.domain.enums.NotificationChannel;
import com.elzocodeur.campusmaster.domain.enums.NotificationEvent;
import com.elzocodeur.campusmaster.domain.enums.NotificationPriority;
import com.elzocodeur.campusmaster.infrastructure.persistence.repository.InscriptionRepository;
import com.elzocodeur.campusmaster.infrastructure.persistence.repository.NotificationPreferenceRepository;
import com.elzocodeur.campusmaster.infrastructure.persistence.repository.NotificationPushRepository;
import com.elzocodeur.campusmaster.infrastructure.persistence.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationEventService {

    private final NotificationPushRepository notificationPushRepository;
    private final NotificationPreferenceRepository preferenceRepository;
    private final InscriptionRepository inscriptionRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final WebSocketNotificationService webSocketService;

    /**
     * Déclenche les notifications pour un nouveau devoir publié
     */
    @Async
    @Transactional
    public void onDevoirPublie(Devoir devoir) {
        log.info("Déclenchement des notifications pour devoir publié : {}", devoir.getTitre());

        // Récupérer tous les étudiants inscrits au cours
        List<Inscription> inscriptions = inscriptionRepository.findByCoursId(devoir.getCours().getId());

        for (Inscription inscription : inscriptions) {
            User etudiant = inscription.getEtudiant().getUser();

            // Vérifier les préférences de l'utilisateur
            List<NotificationChannel> enabledChannels = preferenceRepository.findEnabledChannels(
                    etudiant.getId(),
                    NotificationEvent.DEVOIR_PUBLIE
            );

            // Si aucune préférence définie, envoyer par tous les canaux par défaut
            if (enabledChannels.isEmpty()) {
                enabledChannels = List.of(NotificationChannel.EMAIL, NotificationChannel.WEB_PUSH, NotificationChannel.IN_APP);
            }

            String title = "Nouveau devoir : " + devoir.getTitre();
            String message = String.format(
                    "Un nouveau devoir a été publié dans le cours '%s'. Date limite : %s",
                    devoir.getCours().getTitre(),
                    devoir.getDateLimite()
            );

            sendNotification(
                    etudiant,
                    title,
                    message,
                    NotificationEvent.DEVOIR_PUBLIE,
                    enabledChannels,
                    NotificationPriority.HIGH,
                    "Devoir",
                    devoir.getId(),
                    "/devoirs/" + devoir.getId()
            );
        }
    }

    /**
     * Déclenche les notifications pour un devoir corrigé
     */
    @Async
    @Transactional
    public void onDevoirCorrige(Submit submit) {
        log.info("Déclenchement des notifications pour devoir corrigé : {}", submit.getDevoir().getTitre());

        User etudiant = submit.getEtudiant().getUser();

        List<NotificationChannel> enabledChannels = preferenceRepository.findEnabledChannels(
                etudiant.getId(),
                NotificationEvent.DEVOIR_CORRIGE
        );

        if (enabledChannels.isEmpty()) {
            enabledChannels = List.of(NotificationChannel.EMAIL, NotificationChannel.WEB_PUSH, NotificationChannel.IN_APP);
        }

        String title = "Devoir corrigé : " + submit.getDevoir().getTitre();
        String message = String.format(
                "Votre devoir '%s' a été corrigé. Note obtenue : %.2f/20",
                submit.getDevoir().getTitre(),
                submit.getNote()
        );

        sendNotification(
                etudiant,
                title,
                message,
                NotificationEvent.DEVOIR_CORRIGE,
                enabledChannels,
                NotificationPriority.NORMAL,
                "Submit",
                submit.getId(),
                "/devoirs/" + submit.getDevoir().getId() + "/submit/" + submit.getId()
        );
    }

    /**
     * Déclenche les notifications pour un nouveau message
     */
    @Async
    @Transactional
    public void onNouveauMessage(Message message) {
        log.info("Déclenchement des notifications pour nouveau message");

        User destinataire = message.getDestinataire();
        if (destinataire == null) return; // Message de groupe, géré différemment

        List<NotificationChannel> enabledChannels = preferenceRepository.findEnabledChannels(
                destinataire.getId(),
                NotificationEvent.NOUVEAU_MESSAGE
        );

        if (enabledChannels.isEmpty()) {
            enabledChannels = List.of(NotificationChannel.WEB_PUSH, NotificationChannel.IN_APP);
        }

        String title = "Nouveau message de " + message.getExpediteur().getFirstName() + " " +
                       message.getExpediteur().getLastName();
        String messageText = message.getContenu().length() > 100 ?
                message.getContenu().substring(0, 100) + "..." :
                message.getContenu();

        sendNotification(
                destinataire,
                title,
                messageText,
                NotificationEvent.NOUVEAU_MESSAGE,
                enabledChannels,
                NotificationPriority.NORMAL,
                "Message",
                message.getId(),
                "/messages/conversations/" + message.getConversation().getId()
        );
    }

    /**
     * Déclenche les notifications pour une deadline proche
     */
    @Async
    @Transactional
    public void onDeadlineProche(Devoir devoir, User etudiant) {
        log.info("Déclenchement des notifications pour deadline proche : {}", devoir.getTitre());

        List<NotificationChannel> enabledChannels = preferenceRepository.findEnabledChannels(
                etudiant.getId(),
                NotificationEvent.DEADLINE_PROCHE
        );

        if (enabledChannels.isEmpty()) {
            enabledChannels = List.of(NotificationChannel.EMAIL, NotificationChannel.WEB_PUSH, NotificationChannel.IN_APP);
        }

        String title = "Rappel : Deadline proche !";
        String message = String.format(
                "Le devoir '%s' doit être rendu avant le %s. Il vous reste peu de temps !",
                devoir.getTitre(),
                devoir.getDateLimite()
        );

        sendNotification(
                etudiant,
                title,
                message,
                NotificationEvent.DEADLINE_PROCHE,
                enabledChannels,
                NotificationPriority.URGENT,
                "Devoir",
                devoir.getId(),
                "/devoirs/" + devoir.getId()
        );
    }

    /**
     * Déclenche les notifications pour un nouveau cours
     */
    @Async
    @Transactional
    public void onNouveauCours(Cours cours) {
        log.info("Déclenchement des notifications pour nouveau cours : {}", cours.getTitre());

        List<Inscription> inscriptions = inscriptionRepository.findByCoursId(cours.getId());

        for (Inscription inscription : inscriptions) {
            User etudiant = inscription.getEtudiant().getUser();

            List<NotificationChannel> enabledChannels = preferenceRepository.findEnabledChannels(
                    etudiant.getId(),
                    NotificationEvent.NOUVEAU_COURS
            );

            if (enabledChannels.isEmpty()) {
                enabledChannels = List.of(NotificationChannel.IN_APP);
            }

            String title = "Nouveau contenu de cours";
            String message = String.format(
                    "Du nouveau contenu a été ajouté au cours '%s'",
                    cours.getTitre()
            );

            sendNotification(
                    etudiant,
                    title,
                    message,
                    NotificationEvent.NOUVEAU_COURS,
                    enabledChannels,
                    NotificationPriority.LOW,
                    "Cours",
                    cours.getId(),
                    "/cours/" + cours.getId()
            );
        }
    }

    /**
     * Déclenche les notifications pour une nouvelle annonce
     */
    @Async
    @Transactional
    public void onNouvelleAnnonce(Annonce annonce) {
        log.info("Déclenchement des notifications pour nouvelle annonce : {}", annonce.getTitre());

        List<Inscription> inscriptions = inscriptionRepository.findByCoursId(annonce.getCours().getId());

        for (Inscription inscription : inscriptions) {
            User etudiant = inscription.getEtudiant().getUser();

            List<NotificationChannel> enabledChannels = preferenceRepository.findEnabledChannels(
                    etudiant.getId(),
                    NotificationEvent.NOUVELLE_ANNONCE
            );

            if (enabledChannels.isEmpty()) {
                enabledChannels = List.of(NotificationChannel.WEB_PUSH, NotificationChannel.IN_APP);
            }

            String title = "Nouvelle annonce : " + annonce.getTitre();

            sendNotification(
                    etudiant,
                    title,
                    annonce.getContenu(),
                    NotificationEvent.NOUVELLE_ANNONCE,
                    enabledChannels,
                    NotificationPriority.HIGH,
                    "Annonce",
                    annonce.getId(),
                    "/cours/" + annonce.getCours().getId() + "/annonces"
            );
        }
    }

    /**
     * Méthode générique pour envoyer des notifications via plusieurs canaux
     */
    private void sendNotification(
            User user,
            String title,
            String message,
            NotificationEvent eventType,
            List<NotificationChannel> channels,
            NotificationPriority priority,
            String entityType,
            Long entityId,
            String actionUrl
    ) {
        for (NotificationChannel channel : channels) {
            try {
                NotificationPush notification = NotificationPush.builder()
                        .user(user)
                        .title(title)
                        .message(message)
                        .eventType(eventType)
                        .channel(channel)
                        .priority(priority)
                        .sent(false)
                        .entityType(entityType)
                        .entityId(entityId)
                        .actionUrl(actionUrl)
                        .build();

                notification = notificationPushRepository.save(notification);

                // Envoyer selon le canal
                switch (channel) {
                    case EMAIL:
                        emailService.sendNotificationEmail(user, notification);
                        break;
                    case WEB_PUSH:
                    case IN_APP:
                        webSocketService.sendNotificationToUser(user.getId(), notification);
                        break;
                }

                // Marquer comme envoyée
                notification.setSent(true);
                notification.setSentAt(LocalDateTime.now());
                notificationPushRepository.save(notification);

            } catch (Exception e) {
                log.error("Erreur lors de l'envoi de la notification via {} pour l'utilisateur {}: {}",
                        channel, user.getId(), e.getMessage());
            }
        }
    }

    /**
     * Convertit une NotificationPush en DTO
     */
    public NotificationPushDto toDto(NotificationPush notification) {
        return NotificationPushDto.builder()
                .id(notification.getId())
                .userId(notification.getUser().getId())
                .userName(notification.getUser().getFirstName() + " " + notification.getUser().getLastName())
                .title(notification.getTitle())
                .message(notification.getMessage())
                .eventType(notification.getEventType().name())
                .channel(notification.getChannel().name())
                .priority(notification.getPriority().name())
                .sent(notification.getSent())
                .sentAt(notification.getSentAt())
                .scheduledAt(notification.getScheduledAt())
                .entityType(notification.getEntityType())
                .entityId(notification.getEntityId())
                .actionUrl(notification.getActionUrl())
                .errorMessage(notification.getErrorMessage())
                .retryCount(notification.getRetryCount())
                .createdAt(notification.getCreatedAt())
                .build();
    }

    /**
     * Récupère les notifications d'un utilisateur
     */
    @Transactional(readOnly = true)
    public List<NotificationPushDto> getUserNotifications(Long userId) {
        return notificationPushRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Récupère le nombre de notifications non lues
     */
    @Transactional(readOnly = true)
    public Integer getUnreadCount(Long userId) {
        return notificationPushRepository.countPendingByUserId(userId);
    }
}
