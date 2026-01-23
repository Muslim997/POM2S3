package com.elzocodeur.campusmaster.domain.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "kpi_configurations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KPIConfiguration extends BaseEntity {

    @Column(name = "code", nullable = false, unique = true, length = 50)
    private String code;

    @Column(name = "libelle", nullable = false, length = 200)
    private String libelle;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "formula", columnDefinition = "TEXT")
    private String formula;

    @Column(name = "seuil_alerte")
    private Double seuilAlerte;

    @Column(name = "seuil_critique")
    private Double seuilCritique;

    @Column(name = "unite", length = 50)
    private String unite;

    @Column(name = "actif")
    @Builder.Default
    private Boolean actif = true;

    @Column(name = "ordre_affichage")
    private Integer ordreAffichage;
}
