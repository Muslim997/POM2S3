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
    @Operation(summary = "Consulter mes cours inscrits")
    public ResponseEntity<List<InscriptionDto>> getMesCours(@PathVariable Long etudiantId) {
        return ResponseEntity.ok(inscriptionService.getInscriptionsByEtudiant(etudiantId));
    }

    // ============ INSCRIPTIONS ============

    @PostMapping("/inscription/{etudiantId}/cours/{coursId}")
    @Operation(summary = "S'inscrire à un cours")
    public ResponseEntity<InscriptionDto> inscrireCours(
            @PathVariable Long etudiantId,
            @PathVariable Long coursId) {
        return ResponseEntity.ok(inscriptionService.inscrireEtudiant(etudiantId, coursId));
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
    @Operation(summary = "Soumettre un devoir")
    public ResponseEntity<SubmitDto> soumettreDevoir(
            @PathVariable Long etudiantId,
            @Valid @RequestBody CreateSubmitRequest request) {
        return ResponseEntity.ok(submitService.createSubmit(request, etudiantId));
    }

    @PutMapping("/submit/{id}/etudiant/{etudiantId}")
    @Operation(summary = "Modifier une soumission (versionning)")
    public ResponseEntity<SubmitDto> modifierSoumission(
            @PathVariable Long id,
            @PathVariable Long etudiantId,
            @Valid @RequestBody CreateSubmitRequest request) {
        return ResponseEntity.ok(submitService.updateSubmit(id, request, etudiantId));
    }

    @GetMapping("/submit/etudiant/{etudiantId}")
    @Operation(summary = "Consulter mes soumissions")
    public ResponseEntity<List<SubmitDto>> getMesSoumissions(@PathVariable Long etudiantId) {
        return ResponseEntity.ok(submitService.getSubmitsByEtudiant(etudiantId));
    }

    @GetMapping("/submit/devoir/{devoirId}/etudiant/{etudiantId}/historique")
    @Operation(summary = "Consulter l'historique de soumission (versionning)")
    public ResponseEntity<List<SubmitDto>> getHistoriqueSoumission(
            @PathVariable Long devoirId,
            @PathVariable Long etudiantId) {
        return ResponseEntity.ok(submitService.getSubmitHistory(devoirId, etudiantId));
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
