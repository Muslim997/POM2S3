package com.elzocodeur.campusmaster.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "conversations", indexes = {
        @Index(name = "idx_conv_participants", columnList = "participant1_id,participant2_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Conversation extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "participant1_id", nullable = false)
    private User participant1;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "participant2_id", nullable = false)
    private User participant2;

    @Column(name = "dernier_message_date")
    private LocalDateTime dernierMessageDate;

    @Column(name = "dernier_message_contenu", columnDefinition = "TEXT")
    private String dernierMessageContenu;

    @OneToMany(mappedBy = "conversation", cascade = CascadeType.ALL)
    @Builder.Default
    private Set<Message> messages = new HashSet<>();

    @Column(name = "archivee")
    @Builder.Default
    private Boolean archivee = false;
}
