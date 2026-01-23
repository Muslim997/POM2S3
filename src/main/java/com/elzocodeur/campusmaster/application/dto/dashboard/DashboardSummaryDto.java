package com.elzocodeur.campusmaster.application.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardSummaryDto {

    // Vue d'ensemble
    private Integer nombreTotalEtudiants;
    private Integer nombreEtudiantsActifs;
    private Integer nombreEnseignants;
    private Integer nombreCours;

    // Activité récente (7 derniers jours)
    private Long activitesRecentes;
    private Long pagesVuesRecentes;
    private Long telechargements;

    // Performance académique
    private Double moyenneGenerale;
    private Double tauxReussite;
    private Double tauxAssiduité;

    // KPIs principaux
    private List<KPIDto> kpisPrincipaux;

    // Alertes
    private Integer nombreAlertes;
    private List<AlertDto> alertes;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AlertDto {
        private String type;
        private String message;
        private String severite; // INFO, WARNING, ERROR, CRITICAL
        private String action;
    }
}
