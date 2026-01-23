package com.elzocodeur.campusmaster.domain.entity;

import com.elzocodeur.campusmaster.domain.enums.NotificationChannel;
import com.elzocodeur.campusmaster.domain.enums.NotificationEvent;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "notification_preferences",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_notif_pref_user_event_channel",
                        columnNames = {"user_id", "event_type", "channel"})
        },
        indexes = {
                @Index(name = "idx_notif_pref_user", columnList = "user_id")
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationPreference extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false, length = 50)
    private NotificationEvent eventType;

    @Enumerated(EnumType.STRING)
    @Column(name = "channel", nullable = false, length = 20)
    private NotificationChannel channel;

    @Column(name = "enabled", nullable = false)
    @Builder.Default
    private Boolean enabled = true;
}
