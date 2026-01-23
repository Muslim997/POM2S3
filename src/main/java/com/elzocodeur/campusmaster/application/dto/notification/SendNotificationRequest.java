package com.elzocodeur.campusmaster.application.dto.notification;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SendNotificationRequest {

    @NotNull(message = "L'utilisateur est obligatoire")
    private Long userId;

    @NotBlank(message = "Le titre est obligatoire")
    private String title;

    @NotBlank(message = "Le message est obligatoire")
    private String message;

    @NotBlank(message = "Le type d'événement est obligatoire")
    private String eventType;

    @NotNull(message = "Au moins un canal est requis")
    private Set<String> channels;

    private String priority;
    private LocalDateTime scheduledAt;
    private String entityType;
    private Long entityId;
    private String actionUrl;
}
