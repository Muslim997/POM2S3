package com.elzocodeur.campusmaster.web.controller;

import com.elzocodeur.campusmaster.application.dto.dashboard.*;
import com.elzocodeur.campusmaster.application.service.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
@Tag(name = "Tableau de bord", description = "Endpoints pour le tableau de bord analytique")
@SecurityRequirement(name = "Bearer Authentication")
public class DashboardController {

    private final DashboardService dashboardService;
    private final GradeAnalysisService gradeAnalysisService;
    private final ActivityTrackingService activityTrackingService;
    private final KPIService kpiService;

    // ============ VUE D'ENSEMBLE ============

    @GetMapping("/summary")
    @Operation(summary = "Vue d'ensemble du tableau de bord")
    public ResponseEntity<DashboardSummaryDto> getDashboardSummary() {
        return ResponseEntity.ok(dashboardService.getDashboardSummary());
    }

    // ============ GRAPHE ÉVOLUTION DES NOTES ============

    @GetMapping("/notes/evolution/etudiant/{etudiantId}")
    @Operation(summary = "Évolution des notes d'un étudiant")
    public ResponseEntity<GrapheEvolutionNotesDto> getEvolutionNotesEtudiant(@PathVariable Long etudiantId) {
        return ResponseEntity.ok(gradeAnalysisService.getEvolutionNotes(etudiantId));
    }

    @GetMapping("/notes/evolution/cours/{coursId}")
    @Operation(summary = "Évolution des notes pour un cours (tous les étudiants)")
    public ResponseEntity<List<GrapheEvolutionNotesDto>> getEvolutionNotesCours(@PathVariable Long coursId) {
        return ResponseEntity.ok(gradeAnalysisService.getEvolutionNotesForCours(coursId));
    }

    // ============ ACTIVITÉ HEBDOMADAIRE ============

    @GetMapping("/activity/weekly")
    @Operation(summary = "Statistiques d'activité hebdomadaire (7 derniers jours)")
    public ResponseEntity<ActivityStatsDto> getWeeklyActivity() {
        return ResponseEntity.ok(activityTrackingService.getWeeklyActivityStats());
    }

    @GetMapping("/activity/period")
    @Operation(summary = "Statistiques d'activité pour une période donnée")
    public ResponseEntity<ActivityStatsDto> getActivityForPeriod(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(activityTrackingService.getActivityStats(startDate, endDate));
    }

    // ============ STATISTIQUES PAR DÉPARTEMENT ============

    @GetMapping("/stats/departement/{departementId}")
    @Operation(summary = "Statistiques détaillées par département")
    public ResponseEntity<StatsDepartementDto> getStatsDepartement(@PathVariable Long departementId) {
        return ResponseEntity.ok(dashboardService.getStatsDepartement(departementId));
    }

    // ============ KPI CONFIGURABLES ============

    @GetMapping("/kpi/all")
    @Operation(summary = "Tous les KPIs configurables")
    public ResponseEntity<List<KPIDto>> getAllKPIs() {
        return ResponseEntity.ok(kpiService.calculerTousLesKPIs());
    }
}
