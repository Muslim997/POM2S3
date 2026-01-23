package com.elzocodeur.campusmaster.application.dto.stats;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatistiquesAvanceesDto {

    // Statistiques globales
    private Integer nombreEtudiants;
    private Integer nombreEtudiantsActifs;
    private Integer nombreEtudiantsInactifs;
    private Integer nombreEnseignants;
    private Integer nombreCours;
    private Integer nombreModules;

    // Statistiques sur les devoirs
    private Integer nombreDevoirsTotal;
    private Integer nombreDevoirsRendus;
    private Integer nombreDevoirsEnAttente;
    private Double tauxRemiseDevoirs;

    // Performance globale
    private Double moyenneGenerale;
    private Map<String, Double> performanceParMatiere;
    private Map<String, Integer> nombreEtudiantsParMatiere;

    // Taux de réussite
    private Double tauxReussite;
    private Integer nombreEtudiantsReussis;
    private Integer nombreEtudiantsEchoues;
}
