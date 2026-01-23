package com.elzocodeur.campusmaster.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "semestres")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Semestre extends BaseEntity {

    @Column(name = "code", nullable = false, unique = true, length = 20)
    private String code;

    @Column(name = "libelle", nullable = false, length = 100)
    private String libelle;

    @Column(name = "annee_academique", nullable = false, length = 20)
    private String anneeAcademique;

    @Column(name = "date_debut")
    private LocalDate dateDebut;

    @Column(name = "date_fin")
    private LocalDate dateFin;

    @Column(name = "actif")
    @Builder.Default
    private Boolean actif = true;

    @OneToMany(mappedBy = "semestre", cascade = CascadeType.ALL)
    @Builder.Default
    private Set<Module> modules = new HashSet<>();
}
