package com.elzocodeur.campusmaster.web.controller;

import com.elzocodeur.campusmaster.application.dto.cours.CoursDto;
import com.elzocodeur.campusmaster.application.dto.devoir.DevoirDto;
import com.elzocodeur.campusmaster.application.dto.inscription.InscriptionDto;
import com.elzocodeur.campusmaster.application.dto.notification.NotificationDto;
import com.elzocodeur.campusmaster.application.dto.submit.CreateSubmitRequest;
import com.elzocodeur.campusmaster.application.dto.submit.SubmitDto;
import com.elzocodeur.campusmaster.application.dto.support.SupportDto;
import com.elzocodeur.campusmaster.application.service.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/etudiant")
@RequiredArgsConstructor
@Tag(name = "Espace Étudiant", description = "Endpoints pour les étudiants")
@SecurityRequirement(name = "Bearer Authentication")
public class EtudiantController {

    private final CoursService coursService;
    private final SupportService supportService;
    private final DevoirService devoirService;
    private final SubmitService submitService;
    private final InscriptionService inscriptionService;
    private final NotificationService notificationService;

    // ============ COURS ============

    @GetMapping("/cours")
    @Operation(summary = "Consulter tous les cours disponibles")
    public ResponseEntity<List<CoursDto>> getAllCours() {
        return ResponseEntity.ok(coursService.getAllCours());
    }

    @GetMapping("/cours/{id}")
    @Operation(summary = "Consulter un cours par ID")
    public ResponseEntity<CoursDto> getCoursById(@PathVariable Long id) {
        return ResponseEntity.ok(coursService.getCoursById(id));
    }

    @GetMapping("/mes-cours/{etudiantId}")
    @Operation(summary = "Consulter mes cours inscrits par etudiantId (ID table etudiants)")
    public ResponseEntity<List<InscriptionDto>> getMesCours(@PathVariable Long etudiantId) {
        return ResponseEntity.ok(inscriptionService.getInscriptionsByEtudiant(etudiantId));
    }

    @GetMapping("/mes-cours/user/{userId}")
    @Operation(summary = "Consulter mes cours inscrits par userId (ID table users - recommandé)")
    public ResponseEntity<List<InscriptionDto>> getMesCoursByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(inscriptionService.getInscriptionsByEtudiantUserId(userId));
    }

    // ============ INSCRIPTIONS ============

    @PostMapping("/inscription/{etudiantId}/cours/{coursId}")
    @Operation(summary = "S'inscrire à un cours par etudiantId (ID table etudiants)")
    public ResponseEntity<InscriptionDto> inscrireCours(
            @PathVariable Long etudiantId,
            @PathVariable Long coursId) {
        return ResponseEntity.ok(inscriptionService.inscrireEtudiant(etudiantId, coursId));
    }

    @PostMapping("/inscription/user/{userId}/cours/{coursId}")
    @Operation(summary = "S'inscrire à un cours par userId (ID table users - recommandé)")
    public ResponseEntity<InscriptionDto> inscrireCoursByUserId(
            @PathVariable Long userId,
            @PathVariable Long coursId) {
        return ResponseEntity.ok(inscriptionService.inscrireEtudiantByUserId(userId, coursId));
    }

    @DeleteMapping("/inscription/{inscriptionId}")
    @Operation(summary = "Se désinscrire d'un cours")
    public ResponseEntity<Void> desinscrireCours(@PathVariable Long inscriptionId) {
        inscriptionService.desinscrireEtudiant(inscriptionId);
        return ResponseEntity.noContent().build();
    }

    // ============ SUPPORTS ============

    @GetMapping("/cours/{coursId}/supports")
    @Operation(summary = "Consulter les supports d'un cours")
    public ResponseEntity<List<SupportDto>> getSupportsByCours(@PathVariable Long coursId) {
        return ResponseEntity.ok(supportService.getSupportsByCours(coursId));
    }

    @GetMapping("/supports/{id}")
    @Operation(summary = "Télécharger un support (obtenir l'URL)")
    public ResponseEntity<SupportDto> getSupportById(@PathVariable Long id) {
        return ResponseEntity.ok(supportService.getSupportById(id));
    }

    @GetMapping("/cours/{coursId}/supports/type/{type}")
    @Operation(summary = "Filtrer les supports par type (PDF, PPT, VIDEO)")
    public ResponseEntity<List<SupportDto>> getSupportsByType(
            @PathVariable Long coursId,
            @PathVariable String type) {
        return ResponseEntity.ok(supportService.getSupportsByCoursAndType(coursId, type));
    }

    // ============ DEVOIRS ============

    @GetMapping("/cours/{coursId}/devoirs")
    @Operation(summary = "Consulter les devoirs d'un cours")
    public ResponseEntity<List<DevoirDto>> getDevoirsByCours(@PathVariable Long coursId) {
        return ResponseEntity.ok(devoirService.getDevoirsByCours(coursId));
    }

    @GetMapping("/cours/{coursId}/devoirs/actifs")
    @Operation(summary = "Consulter les devoirs actifs (non expirés)")
    public ResponseEntity<List<DevoirDto>> getDevoirsActifs(@PathVariable Long coursId) {
        return ResponseEntity.ok(devoirService.getDevoirsActifsByCours(coursId));
    }

    @GetMapping("/devoirs/{id}")
    @Operation(summary = "Consulter un devoir par ID")
    public ResponseEntity<DevoirDto> getDevoirById(@PathVariable Long id) {
        return ResponseEntity.ok(devoirService.getDevoirById(id));
    }

    // ============ SOUMISSIONS ============

    @PostMapping("/submit/{etudiantId}")
    @Operation(summary = "Soumettre un devoir par etudiantId (ID table etudiants)")
    public ResponseEntity<SubmitDto> soumettreDevoir(
            @PathVariable Long etudiantId,
            @Valid @RequestBody CreateSubmitRequest request) {
        return ResponseEntity.ok(submitService.createSubmit(request, etudiantId));
    }

    @PostMapping("/submit/user/{userId}")
    @Operation(summary = "Soumettre un devoir par userId (ID table users - recommandé)")
    public ResponseEntity<SubmitDto> soumettreDevoi0rByUserId(
            @PathVariable Long userId,
            @Valid @RequestBody CreateSubmitRequest request) {
        return ResponseEntity.ok(submitService.createSubmitByUserId(request, userId));
    }

    @PutMapping("/submit/{id}/etudiant/{etudiantId}")
    @Operation(summary = "Modifier une soumission par etudiantId (ID table etudiants)")
    public ResponseEntity<SubmitDto> modifierSoumission(
            @PathVariable Long id,
            @PathVariable Long etudiantId,
            @Valid @RequestBody CreateSubmitRequest request) {
        return ResponseEntity.ok(submitService.updateSubmit(id, request, etudiantId));
    }

    @PutMapping("/submit/{id}/user/{userId}")
    @Operation(summary = "Modifier une soumission par userId (ID table users - recommandé)")
    public ResponseEntity<SubmitDto> modifierSoumissionByUserId(
            @PathVariable Long id,
            @PathVariable Long userId,
            @Valid @RequestBody CreateSubmitRequest request) {
        return ResponseEntity.ok(submitService.updateSubmitByUserId(id, request, userId));
    }

    @GetMapping("/submit/etudiant/{etudiantId}")
    @Operation(summary = "Consulter mes soumissions par etudiantId (ID table etudiants)")
    public ResponseEntity<List<SubmitDto>> getMesSoumissions(@PathVariable Long etudiantId) {
        return ResponseEntity.ok(submitService.getSubmitsByEtudiant(etudiantId));
    }

    @GetMapping("/submit/user/{userId}")
    @Operation(summary = "Consulter mes soumissions par userId (ID table users - recommandé)")
    public ResponseEntity<List<SubmitDto>> getMesSoumissionsByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(submitService.getSubmitsByEtudiantUserId(userId));
    }

    @GetMapping("/submit/devoir/{devoirId}/etudiant/{etudiantId}/historique")
    @Operation(summary = "Consulter l'historique par etudiantId (ID table etudiants)")
    public ResponseEntity<List<SubmitDto>> getHistoriqueSoumission(
            @PathVariable Long devoirId,
            @PathVariable Long etudiantId) {
        return ResponseEntity.ok(submitService.getSubmitHistory(devoirId, etudiantId));
    }

    @GetMapping("/submit/devoir/{devoirId}/user/{userId}/historique")
    @Operation(summary = "Consulter l'historique par userId (ID table users - recommandé)")
    public ResponseEntity<List<SubmitDto>> getHistoriqueSoumissionByUserId(
            @PathVariable Long devoirId,
            @PathVariable Long userId) {
        return ResponseEntity.ok(submitService.getSubmitHistoryByUserId(devoirId, userId));
    }

    @GetMapping("/submit/{id}")
    @Operation(summary = "Consulter une soumission avec note et feedback")
    public ResponseEntity<SubmitDto> getSubmitById(@PathVariable Long id) {
        return ResponseEntity.ok(submitService.getSubmitById(id));
    }

    // ============ NOTIFICATIONS ============

    @GetMapping("/notifications/{userId}")
    @Operation(summary = "Consulter mes notifications")
    public ResponseEntity<List<NotificationDto>> getNotifications(@PathVariable Long userId) {
        return ResponseEntity.ok(notificationService.getNotificationsByUser(userId));
    }

    @GetMapping("/notifications/{userId}/non-lues")
    @Operation(summary = "Consulter mes notifications non lues")
    public ResponseEntity<List<NotificationDto>> getNotificationsNonLues(@PathVariable Long userId) {
        return ResponseEntity.ok(notificationService.getUnreadNotifications(userId));
    }

    @PutMapping("/notifications/{id}/lire")
    @Operation(summary = "Marquer une notification comme lue")
    public ResponseEntity<Void> marquerCommeLue(@PathVariable Long id) {
        notificationService.markAsRead(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/notifications/{userId}/tout-lire")
    @Operation(summary = "Marquer toutes les notifications comme lues")
    public ResponseEntity<Void> marquerToutCommeLu(@PathVariable Long userId) {
        notificationService.markAllAsRead(userId);
        return ResponseEntity.noContent().build();
    }
}
