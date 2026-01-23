package com.elzocodeur.campusmaster.application.dto.stats;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EtudiantProgressDto {
    private Long etudiantId;
    private String etudiantNom;
    private String numeroEtudiant;
    private Integer nombreCoursInscrits;
    private Integer nombreDevoirsRendus;
    private Integer nombreDevoirsEnRetard;
    private Double moyenneGenerale;
    private Integer tauxAssiduité;
}
