package com.elzocodeur.campusmaster.application.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KPIDto {

    private Long id;
    private String code;
    private String libelle;
    private String description;
    private Double valeur;
    private String unite;
    private String statut; // NORMAL, ALERTE, CRITIQUE
    private Double seuilAlerte;
    private Double seuilCritique;
    private String tendance; // HAUSSE, BAISSE, STABLE
    private Double variationPourcentage;
    private LocalDateTime dateCalcul;
}
