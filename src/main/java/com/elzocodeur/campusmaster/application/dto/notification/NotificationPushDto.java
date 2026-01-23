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
public class NotificationPushDto {
    private Long id;
    private Long userId;
    private String userName;
    private String title;
    private String message;
    private String eventType;
    private String channel;
    private String priority;
    private Boolean sent;
    private LocalDateTime sentAt;
    private LocalDateTime scheduledAt;
    private String entityType;
    private Long entityId;
    private String actionUrl;
    private String errorMessage;
    private Integer retryCount;
    private LocalDateTime createdAt;
}
