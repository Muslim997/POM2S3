package com.elzocodeur.campusmaster.web.controller;

import com.elzocodeur.campusmaster.application.dto.notification.NotificationPreferenceDto;
import com.elzocodeur.campusmaster.application.dto.notification.NotificationPushDto;
import com.elzocodeur.campusmaster.application.dto.notification.SendNotificationRequest;
import com.elzocodeur.campusmaster.application.service.NotificationEventService;
import com.elzocodeur.campusmaster.application.service.NotificationPreferenceService;
import com.elzocodeur.campusmaster.domain.entity.NotificationPreference;
import com.elzocodeur.campusmaster.domain.enums.NotificationChannel;
import com.elzocodeur.campusmaster.domain.enums.NotificationEvent;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
@Tag(name = "Notifications Push", description = "Gestion des notifications push (Email, WebSocket, In-App)")
public class NotificationPushController {

    private final NotificationEventService notificationEventService;
    private final NotificationPreferenceService notificationPreferenceService;

    // ========== NOTIFICATIONS ==========

    @GetMapping("/user/{userId}")
    @Operation(summary = "Récupérer toutes les notifications d'un utilisateur")
    public ResponseEntity<List<NotificationPushDto>> getUserNotifications(@PathVariable Long userId) {
        List<NotificationPushDto> notifications = notificationEventService.getUserNotifications(userId);
        return ResponseEntity.ok(notifications);
    }

    @GetMapping("/user/{userId}/unread/count")
    @Operation(summary = "Récupérer le nombre de notifications non lues")
    public ResponseEntity<Integer> getUnreadCount(@PathVariable Long userId) {
        Integer count = notificationEventService.getUnreadCount(userId);
        return ResponseEntity.ok(count);
    }

    @PostMapping("/send")
    @Operation(summary = "Envoyer une notification manuelle")
    public ResponseEntity<String> sendNotification(@Valid @RequestBody SendNotificationRequest request) {
        // Cette méthode pourrait être implémentée pour permettre l'envoi manuel de notifications
        // par les administrateurs ou pour des cas d'usage spécifiques
        return ResponseEntity.status(HttpStatus.CREATED).body("Notification envoyée avec succès");
    }

    // ========== PRÉFÉRENCES DE NOTIFICATIONS ==========

    @GetMapping("/preferences/user/{userId}")
    @Operation(summary = "Récupérer les préférences de notification d'un utilisateur")
    public ResponseEntity<List<NotificationPreferenceDto>> getUserPreferences(@PathVariable Long userId) {
        List<NotificationPreferenceDto> preferences = notificationPreferenceService.getUserPreferences(userId);
        return ResponseEntity.ok(preferences);
    }

    @PostMapping("/preferences/user/{userId}")
    @Operation(summary = "Créer ou mettre à jour une préférence de notification")
    public ResponseEntity<NotificationPreferenceDto> updatePreference(
            @PathVariable Long userId,
            @RequestParam String eventType,
            @RequestParam String channel,
            @RequestParam Boolean enabled) {

        NotificationPreferenceDto preference = notificationPreferenceService.updatePreference(
                userId,
                NotificationEvent.valueOf(eventType),
                NotificationChannel.valueOf(channel),
                enabled
        );

        return ResponseEntity.ok(preference);
    }

    @PutMapping("/preferences/{preferenceId}/toggle")
    @Operation(summary = "Activer/Désactiver une préférence de notification")
    public ResponseEntity<NotificationPreferenceDto> togglePreference(@PathVariable Long preferenceId) {
        NotificationPreferenceDto preference = notificationPreferenceService.togglePreference(preferenceId);
        return ResponseEntity.ok(preference);
    }

    @DeleteMapping("/preferences/{preferenceId}")
    @Operation(summary = "Supprimer une préférence de notification")
    public ResponseEntity<Void> deletePreference(@PathVariable Long preferenceId) {
        notificationPreferenceService.deletePreference(preferenceId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/preferences/user/{userId}/default")
    @Operation(summary = "Créer les préférences par défaut pour un utilisateur")
    public ResponseEntity<List<NotificationPreferenceDto>> createDefaultPreferences(@PathVariable Long userId) {
        List<NotificationPreferenceDto> preferences = notificationPreferenceService.createDefaultPreferences(userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(preferences);
    }

    // ========== STATISTIQUES ==========

    @GetMapping("/stats/user/{userId}")
    @Operation(summary = "Récupérer les statistiques de notifications d'un utilisateur")
    public ResponseEntity<Object> getUserNotificationStats(@PathVariable Long userId) {
        // Pourrait retourner des stats comme nombre total, par type, par canal, etc.
        return ResponseEntity.ok().build();
    }
}
