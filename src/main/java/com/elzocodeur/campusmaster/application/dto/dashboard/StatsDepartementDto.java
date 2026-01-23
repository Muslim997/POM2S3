package com.elzocodeur.campusmaster.application.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatsDepartementDto {

    private Long departementId;
    private String departementNom;
    private Integer nombreEtudiants;
    private Integer nombreEnseignants;
    private Integer nombreCours;
    private Integer nombreModules;
    private Double moyenneGenerale;
    private Double tauxReussite;
    private Double tauxAssiduité;
    private Map<String, MatiereStatsDto> statsParMatiere;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MatiereStatsDto {
        private String matiereNom;
        private Integer nombreEtudiants;
        private Double moyenneMatiere;
        private Double tauxReussite;
        private Integer nombreDevoirs;
        private Integer devoirsRendus;
    }
}
