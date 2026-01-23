package com.elzocodeur.campusmaster.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "annonces")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Annonce extends BaseEntity {

    @Column(name = "titre", nullable = false, length = 200)
    private String titre;

    @Column(name = "contenu", columnDefinition = "TEXT", nullable = false)
    private String contenu;

    @Column(name = "date_publication")
    private LocalDateTime datePublication;

    @Column(name = "priorite")
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Priorite priorite = Priorite.NORMALE;

    @ManyToOne
    @JoinColumn(name = "cours_id")
    private Cours cours;

    @ManyToOne
    @JoinColumn(name = "tuteur_id", nullable = false)
    private Tuteur tuteur;

    public enum Priorite {
        BASSE,
        NORMALE,
        HAUTE,
        URGENTE
    }
}
