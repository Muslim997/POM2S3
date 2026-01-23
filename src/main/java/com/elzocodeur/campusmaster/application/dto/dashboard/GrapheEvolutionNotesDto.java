package com.elzocodeur.campusmaster.application.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GrapheEvolutionNotesDto {

    private Long etudiantId;
    private String etudiantNom;
    private List<DataPoint> evolution;
    private Double moyenneGlobale;
    private Double moyenneMinimum;
    private Double moyenneMaximum;
    private String tendance; // HAUSSE, BAISSE, STABLE

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DataPoint {
        private LocalDate date;
        private Double note;
        private String matiere;
        private String typeEvaluation;
    }
}
