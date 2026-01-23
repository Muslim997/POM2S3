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
public class NotificationDto {
    private Long id;
    private String titre;
    private String contenu;
    private Boolean estLu;
    private LocalDateTime dateEnvoi;
    private Long userId;
    private LocalDateTime createdAt;
}
