package com.elzocodeur.campusmaster.application.dto.stats;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CoursStatsDto {
    private Long coursId;
    private String coursNom;
    private Integer nombreEtudiants;
    private Integer nombreSupports;
    private Integer nombreDevoirs;
    private Integer devoirsEnAttente;
    private Integer devoirsEvalues;
    private Double moyenneGenerale;
    private Integer tauxRendu;
}
