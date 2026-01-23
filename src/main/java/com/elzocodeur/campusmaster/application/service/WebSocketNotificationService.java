package com.elzocodeur.campusmaster.application.service;

import com.elzocodeur.campusmaster.application.dto.notification.WebSocketNotificationDto;
import com.elzocodeur.campusmaster.domain.entity.NotificationPush;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class WebSocketNotificationService {

    private final SimpMessagingTemplate messagingTemplate;

    /**
     * Envoie une notification en temps réel à un utilisateur spécifique via WebSocket
     */
    public void sendNotificationToUser(Long userId, NotificationPush notification) {
        try {
            WebSocketNotificationDto dto = buildWebSocketNotificationDto(notification);

            // Envoie à la destination privée de l'utilisateur
            String destination = "/queue/notifications";
            messagingTemplate.convertAndSendToUser(
                userId.toString(),
                destination,
                dto
            );

            log.info("Notification WebSocket envoyée à l'utilisateur {} : {}", userId, notification.getTitle());
        } catch (Exception e) {
            log.error("Erreur lors de l'envoi de la notification WebSocket à l'utilisateur {}: {}",
                    userId, e.getMessage());
        }
    }

    /**
     * Envoie une notification broadcast à tous les utilisateurs connectés
     */
    public void sendBroadcastNotification(NotificationPush notification) {
        try {
            WebSocketNotificationDto dto = buildWebSocketNotificationDto(notification);

            messagingTemplate.convertAndSend("/topic/notifications", dto);

            log.info("Notification broadcast envoyée : {}", notification.getTitle());
        } catch (Exception e) {
            log.error("Erreur lors de l'envoi de la notification broadcast: {}", e.getMessage());
        }
    }

    /**
     * Envoie une notification personnalisée
     */
    public void sendCustomNotification(Long userId, String title, String message, String eventType) {
        try {
            WebSocketNotificationDto dto = WebSocketNotificationDto.builder()
                    .title(title)
                    .message(message)
                    .eventType(eventType)
                    .priority("NORMAL")
                    .timestamp(LocalDateTime.now())
                    .build();

            messagingTemplate.convertAndSendToUser(
                userId.toString(),
                "/queue/notifications",
                dto
            );

            log.info("Notification personnalisée envoyée à l'utilisateur {} : {}", userId, title);
        } catch (Exception e) {
            log.error("Erreur lors de l'envoi de la notification personnalisée: {}", e.getMessage());
        }
    }

    /**
     * Envoie un compteur de notifications non lues à un utilisateur
     */
    public void sendUnreadCountUpdate(Long userId, Integer unreadCount) {
        try {
            messagingTemplate.convertAndSendToUser(
                userId.toString(),
                "/queue/unread-count",
                unreadCount
            );

            log.debug("Compteur de notifications non lues envoyé à l'utilisateur {} : {}", userId, unreadCount);
        } catch (Exception e) {
            log.error("Erreur lors de l'envoi du compteur de notifications: {}", e.getMessage());
        }
    }

    private WebSocketNotificationDto buildWebSocketNotificationDto(NotificationPush notification) {
        return WebSocketNotificationDto.builder()
                .id(notification.getId())
                .title(notification.getTitle())
                .message(notification.getMessage())
                .eventType(notification.getEventType().name())
                .priority(notification.getPriority().name())
                .entityType(notification.getEntityType())
                .entityId(notification.getEntityId())
                .actionUrl(notification.getActionUrl())
                .timestamp(notification.getCreatedAt())
                .build();
    }
}
