package com.elzocodeur.campusmaster.web.controller;

import com.elzocodeur.campusmaster.application.dto.annonce.AnnonceDto;
import com.elzocodeur.campusmaster.application.dto.annonce.CreateAnnonceRequest;
import com.elzocodeur.campusmaster.application.dto.cours.CoursDto;
import com.elzocodeur.campusmaster.application.dto.cours.CreateCoursRequest;
import com.elzocodeur.campusmaster.application.dto.devoir.CreateDevoirRequest;
import com.elzocodeur.campusmaster.application.dto.devoir.DevoirDto;
import com.elzocodeur.campusmaster.application.dto.etudiant.EtudiantDto;
import com.elzocodeur.campusmaster.application.dto.stats.CoursStatsDto;
import com.elzocodeur.campusmaster.application.dto.stats.EtudiantProgressDto;
import com.elzocodeur.campusmaster.application.dto.support.CreateSupportRequest;
import com.elzocodeur.campusmaster.application.dto.support.SupportDto;
import com.elzocodeur.campusmaster.application.dto.submit.SubmitDto;
import com.elzocodeur.campusmaster.application.service.*;
import com.elzocodeur.campusmaster.application.service.AnnonceService;
import com.elzocodeur.campusmaster.application.service.CoursService;
import com.elzocodeur.campusmaster.application.service.DevoirService;
import com.elzocodeur.campusmaster.application.service.EtudiantService;
import com.elzocodeur.campusmaster.application.service.StatsService;
import com.elzocodeur.campusmaster.application.service.SubmitService;
import com.elzocodeur.campusmaster.application.service.SupportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/enseignant")
@RequiredArgsConstructor
@Tag(name = "Espace Enseignant", description = "Endpoints pour les enseignants")
@SecurityRequirement(name = "Bearer Authentication")
public class EnseignantController {

    private final CoursService coursService;
    private final SupportService supportService;
    private final DevoirService devoirService;
    private final SubmitService submitService;
    private final AnnonceService annonceService;
    private final EtudiantService etudiantService;
    private final StatsService statsService;

    // ============ GESTION DES COURS ============

    @GetMapping("/cours")
    @Operation(summary = "Consulter tous mes cours")
    public ResponseEntity<List<CoursDto>> getAllCours() {
        return ResponseEntity.ok(coursService.getAllCours());
    }

    @GetMapping("/cours/tuteur/{tuteurId}")
    @Operation(summary = "Consulter mes cours par tuteurId (ID table tuteurs)")
    public ResponseEntity<List<CoursDto>> getMesCours(@PathVariable Long tuteurId) {
        return ResponseEntity.ok(coursService.getCoursByTuteur(tuteurId));
    }

    @GetMapping("/cours/user/{userId}")
    @Operation(summary = "Consulter mes cours par userId (ID table users - recommandé)")
    public ResponseEntity<List<CoursDto>> getMesCoursByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(coursService.getCoursByTuteurUserId(userId));
    }

    @PostMapping("/cours")
    @Operation(summary = "Créer un nouveau cours")
    public ResponseEntity<CoursDto> createCours(@Valid @RequestBody CreateCoursRequest request) {
        return ResponseEntity.ok(coursService.createCours(request));
    }

    @PutMapping("/cours/{id}")
    @Operation(summary = "Modifier un cours")
    public ResponseEntity<CoursDto> updateCours(
            @PathVariable Long id,
            @Valid @RequestBody CreateCoursRequest request) {
        return ResponseEntity.ok(coursService.updateCours(id, request));
    }

    @DeleteMapping("/cours/{id}")
    @Operation(summary = "Supprimer un cours")
    public ResponseEntity<Void> deleteCours(@PathVariable Long id) {
        coursService.deleteCours(id);
        return ResponseEntity.noContent().build();
    }

    // ============ GESTION DES SUPPORTS ============

    @PostMapping("/supports")
    @Operation(summary = "Ajouter un support de cours")
    public ResponseEntity<SupportDto> createSupport(@Valid @RequestBody CreateSupportRequest request) {
        return ResponseEntity.ok(supportService.createSupport(request));
    }

    @PutMapping("/supports/{id}")
    @Operation(summary = "Modifier un support")
    public ResponseEntity<SupportDto> updateSupport(
            @PathVariable Long id,
            @Valid @RequestBody CreateSupportRequest request) {
        return ResponseEntity.ok(supportService.updateSupport(id, request));
    }

    @DeleteMapping("/supports/{id}")
    @Operation(summary = "Supprimer un support")
    public ResponseEntity<Void> deleteSupport(@PathVariable Long id) {
        supportService.deleteSupport(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/cours/{coursId}/supports")
    @Operation(summary = "Consulter les supports d'un cours")
    public ResponseEntity<List<SupportDto>> getSupportsByCours(@PathVariable Long coursId) {
        return ResponseEntity.ok(supportService.getSupportsByCours(coursId));
    }

    // ============ GESTION DES DEVOIRS ============

    @PostMapping("/devoirs")
    @Operation(summary = "Créer un devoir")
    public ResponseEntity<DevoirDto> createDevoir(@Valid @RequestBody CreateDevoirRequest request) {
        return ResponseEntity.ok(devoirService.createDevoir(request));
    }

    @PutMapping("/devoirs/{id}")
    @Operation(summary = "Modifier un devoir")
    public ResponseEntity<DevoirDto> updateDevoir(
            @PathVariable Long id,
            @Valid @RequestBody CreateDevoirRequest request) {
        return ResponseEntity.ok(devoirService.updateDevoir(id, request));
    }

    @DeleteMapping("/devoirs/{id}")
    @Operation(summary = "Supprimer un devoir")
    public ResponseEntity<Void> deleteDevoir(@PathVariable Long id) {
        devoirService.deleteDevoir(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/cours/{coursId}/devoirs")
    @Operation(summary = "Consulter les devoirs d'un cours")
    public ResponseEntity<List<DevoirDto>> getDevoirsByCours(@PathVariable Long coursId) {
        return ResponseEntity.ok(devoirService.getDevoirsByCours(coursId));
    }

    @GetMapping("/devoirs/tuteur/{tuteurId}")
    @Operation(summary = "Consulter tous mes devoirs par tuteurId (ID table tuteurs)")
    public ResponseEntity<List<DevoirDto>> getMesDevoirs(@PathVariable Long tuteurId) {
        return ResponseEntity.ok(devoirService.getDevoirsByTuteur(tuteurId));
    }

    @GetMapping("/devoirs/user/{userId}")
    @Operation(summary = "Consulter tous mes devoirs par userId (ID table users - recommandé)")
    public ResponseEntity<List<DevoirDto>> getMesDevoirsByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(devoirService.getDevoirsByTuteurUserId(userId));
    }

    // ============ ÉVALUATION DES SOUMISSIONS ============

    @GetMapping("/devoirs/{devoirId}/submissions")
    @Operation(summary = "Consulter toutes les soumissions d'un devoir")
    public ResponseEntity<List<SubmitDto>> getSubmissionsByDevoir(@PathVariable Long devoirId) {
        return ResponseEntity.ok(submitService.getSubmitsByDevoir(devoirId));
    }

    @PutMapping("/submit/{id}/evaluer")
    @Operation(summary = "Évaluer une soumission (note + feedback)")
    public ResponseEntity<SubmitDto> evaluerSubmission(
            @PathVariable Long id,
            @RequestParam Double note,
            @RequestParam(required = false) String feedback) {
        return ResponseEntity.ok(submitService.evaluerSubmit(id, note, feedback));
    }

    @GetMapping("/submit/{id}")
    @Operation(summary = "Consulter une soumission")
    public ResponseEntity<SubmitDto> getSubmitById(@PathVariable Long id) {
        return ResponseEntity.ok(submitService.getSubmitById(id));
    }

    // ============ GESTION DES ANNONCES ============

    @PostMapping("/annonces")
    @Operation(summary = "Publier une annonce")
    public ResponseEntity<AnnonceDto> createAnnonce(@Valid @RequestBody CreateAnnonceRequest request) {
        return ResponseEntity.ok(annonceService.createAnnonce(request));
    }

    @PutMapping("/annonces/{id}")
    @Operation(summary = "Modifier une annonce")
    public ResponseEntity<AnnonceDto> updateAnnonce(
            @PathVariable Long id,
            @Valid @RequestBody CreateAnnonceRequest request) {
        return ResponseEntity.ok(annonceService.updateAnnonce(id, request));
    }

    @DeleteMapping("/annonces/{id}")
    @Operation(summary = "Supprimer une annonce")
    public ResponseEntity<Void> deleteAnnonce(@PathVariable Long id) {
        annonceService.deleteAnnonce(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/annonces/tuteur/{tuteurId}")
    @Operation(summary = "Consulter mes annonces par tuteurId (ID table tuteurs)")
    public ResponseEntity<List<AnnonceDto>> getMesAnnonces(@PathVariable Long tuteurId) {
        return ResponseEntity.ok(annonceService.getAnnoncesByTuteur(tuteurId));
    }

    @GetMapping("/annonces/user/{userId}")
    @Operation(summary = "Consulter mes annonces par userId (ID table users - recommandé)")
    public ResponseEntity<List<AnnonceDto>> getMesAnnoncesByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(annonceService.getAnnoncesByTuteurUserId(userId));
    }

    @GetMapping("/cours/{coursId}/annonces")
    @Operation(summary = "Consulter les annonces d'un cours")
    public ResponseEntity<List<AnnonceDto>> getAnnoncesByCours(@PathVariable Long coursId) {
        return ResponseEntity.ok(annonceService.getAnnoncesByCours(coursId));
    }

    // ============ GESTION DES ÉTUDIANTS ============

    @GetMapping("/cours/{coursId}/etudiants")
    @Operation(summary = "Liste des étudiants d'un cours")
    public ResponseEntity<List<EtudiantDto>> getEtudiantsByCours(@PathVariable Long coursId) {
        return ResponseEntity.ok(etudiantService.getEtudiantsByCours(coursId));
    }

    @GetMapping("/etudiants/{id}/progress")
    @Operation(summary = "Consulter le progrès d'un étudiant par etudiantId (ID table etudiants)")
    public ResponseEntity<EtudiantProgressDto> getEtudiantProgress(@PathVariable Long id) {
        return ResponseEntity.ok(etudiantService.getEtudiantProgress(id));
    }

    @GetMapping("/etudiants/user/{userId}/progress")
    @Operation(summary = "Consulter le progrès d'un étudiant par userId (ID table users - recommandé)")
    public ResponseEntity<EtudiantProgressDto> getEtudiantProgressByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(etudiantService.getEtudiantProgressByUserId(userId));
    }

    // ============ STATISTIQUES ============

    @GetMapping("/cours/{coursId}/stats")
    @Operation(summary = "Statistiques d'un cours")
    public ResponseEntity<CoursStatsDto> getCoursStats(@PathVariable Long coursId) {
        return ResponseEntity.ok(statsService.getCoursStats(coursId));
    }

    @GetMapping("/tuteur/{tuteurId}/stats")
    @Operation(summary = "Statistiques de tous mes cours par tuteurId (ID table tuteurs)")
    public ResponseEntity<List<CoursStatsDto>> getMesCoursStats(@PathVariable Long tuteurId) {
        return ResponseEntity.ok(statsService.getCoursStatsByTuteur(tuteurId));
    }

    @GetMapping("/user/{userId}/stats")
    @Operation(summary = "Statistiques de tous mes cours par userId (ID table users - recommandé)")
    public ResponseEntity<List<CoursStatsDto>> getMesCoursStatsByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(statsService.getCoursStatsByTuteurUserId(userId));
    }
}
