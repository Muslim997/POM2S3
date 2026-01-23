package com.elzocodeur.campusmaster.application.dto.notification;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WebSocketNotificationDto {
    private Long id;
    private String title;
    private String message;
    private String eventType;
    private String priority;
    private String entityType;
    private Long entityId;
    private String actionUrl;
    private LocalDateTime timestamp;
}
