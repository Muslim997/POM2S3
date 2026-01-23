package com.elzocodeur.campusmaster.domain.entity;

import com.elzocodeur.campusmaster.domain.enums.MessageType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "messages", indexes = {
        @Index(name = "idx_message_expediteur", columnList = "expediteur_id"),
        @Index(name = "idx_message_conversation", columnList = "conversation_id"),
        @Index(name = "idx_message_groupe", columnList = "groupe_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Message extends BaseEntity {

    @Column(name = "contenu", columnDefinition = "TEXT", nullable = false)
    private String contenu;

    @Column(name = "est_lu")
    @Builder.Default
    private Boolean estLu = false;

    @Column(name = "date_envoi", nullable = false)
    private LocalDateTime dateEnvoi;

    @Enumerated(EnumType.STRING)
    @Column(name = "type_message", nullable = false, length = 20)
    @Builder.Default
    private MessageType typeMessage = MessageType.PRIVE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "expediteur_id", nullable = false)
    private User expediteur;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "destinataire_id")
    private User destinataire;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conversation_id")
    private Conversation conversation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "groupe_id")
    private GroupeMessage groupe;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "message_tags",
            joinColumns = @JoinColumn(name = "message_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    @Builder.Default
    private Set<MessageTag> tags = new HashSet<>();

    @PrePersist
    protected void onCreate() {
        if (dateEnvoi == null) {
            dateEnvoi = LocalDateTime.now();
        }
    }
}
