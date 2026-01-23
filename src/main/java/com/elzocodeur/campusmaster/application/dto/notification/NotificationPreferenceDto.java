package com.elzocodeur.campusmaster.application.dto.notification;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationPreferenceDto {
    private Long id;
    private Long userId;
    private String eventType;
    private String channel;
    private Boolean enabled;
}
