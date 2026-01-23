package com.elzocodeur.campusmaster.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "submits")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Submit extends BaseEntity {

    @Column(name = "date_soumission")
    private LocalDateTime dateSoumission;

    @Column(name = "note")
    private Double note;

    @Column(name = "feedback", columnDefinition = "TEXT")
    private String feedback;

    @Column(name = "fichier_url", length = 500)
    private String fichierUrl;

    @ManyToOne
    @JoinColumn(name = "devoir_id", nullable = false)
    private Devoir devoir;

    @ManyToOne
    @JoinColumn(name = "etudiant_id", nullable = false)
    private Etudiant etudiant;
}
