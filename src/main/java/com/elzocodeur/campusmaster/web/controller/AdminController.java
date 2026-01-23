package com.elzocodeur.campusmaster.web.controller;

import com.elzocodeur.campusmaster.application.dto.annonce.AnnonceDto;
import com.elzocodeur.campusmaster.application.dto.cours.CoursDto;
import com.elzocodeur.campusmaster.application.dto.departement.CreateDepartementRequest;
import com.elzocodeur.campusmaster.application.dto.departement.DepartementDto;
import com.elzocodeur.campusmaster.application.dto.departement.UpdateDepartementRequest;
import com.elzocodeur.campusmaster.application.dto.devoir.DevoirDto;
import com.elzocodeur.campusmaster.application.dto.etudiant.EtudiantDto;
import com.elzocodeur.campusmaster.application.dto.matiere.CreateMatiereRequest;
import com.elzocodeur.campusmaster.application.dto.matiere.MatiereDto;
import com.elzocodeur.campusmaster.application.dto.module.CreateModuleRequest;
import com.elzocodeur.campusmaster.application.dto.module.ModuleDto;
import com.elzocodeur.campusmaster.application.dto.role.CreateRoleRequest;
import com.elzocodeur.campusmaster.application.dto.role.RoleDto;
import com.elzocodeur.campusmaster.application.dto.semestre.CreateSemestreRequest;
import com.elzocodeur.campusmaster.application.dto.semestre.SemestreDto;
import com.elzocodeur.campusmaster.application.dto.stats.StatistiquesAvanceesDto;
import com.elzocodeur.campusmaster.application.dto.support.SupportDto;
import com.elzocodeur.campusmaster.application.dto.user.CreateUserRequest;
import com.elzocodeur.campusmaster.application.dto.user.UpdateUserRequest;
import com.elzocodeur.campusmaster.application.dto.user.UserDto;
import com.elzocodeur.campusmaster.application.service.*;
import com.elzocodeur.campusmaster.domain.enums.UserRole;
import com.elzocodeur.campusmaster.domain.enums.UserStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@Tag(name = "Espace Administrateur", description = "Endpoints pour les administrateurs")
@SecurityRequirement(name = "Bearer Authentication")
public class AdminController {

    private final EtudiantService etudiantService;
    private final UserManagementService userManagementService;
    private final RoleService roleService;
    private final SemestreService semestreService;
    private final ModuleService moduleService;
    private final MatiereService matiereService;
    private final DepartementService departementService;
    private final ModerationService moderationService;
    private final StatistiquesAvanceesService statistiquesAvanceesService;

    // ============ GESTION DES UTILISATEURS ============

    @GetMapping("/users")
    @Operation(summary = "Liste de tous les utilisateurs (paginée)")
    public ResponseEntity<Page<UserDto>> getAllUsers(Pageable pageable) {
        return ResponseEntity.ok(userManagementService.getAllUsersPaged(pageable));
    }

    @GetMapping("/users/{id}")
    @Operation(summary = "Détails d'un utilisateur")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userManagementService.getUserById(id));
    }

    @GetMapping("/users/search")
    @Operation(summary = "Rechercher des utilisateurs")
    public ResponseEntity<Page<UserDto>> searchUsers(
            @RequestParam String keyword,
            Pageable pageable) {
        return ResponseEntity.ok(userManagementService.searchUsers(keyword, pageable));
    }

    @GetMapping("/users/role/{role}")
    @Operation(summary = "Utilisateurs par rôle")
    public ResponseEntity<Page<UserDto>> getUsersByRole(
            @PathVariable UserRole role,
            Pageable pageable) {
        return ResponseEntity.ok(userManagementService.getUsersByRole(role, pageable));
    }

    @GetMapping("/users/status/{status}")
    @Operation(summary = "Utilisateurs par statut")
    public ResponseEntity<Page<UserDto>> getUsersByStatus(
            @PathVariable UserStatus status,
            Pageable pageable) {
        return ResponseEntity.ok(userManagementService.getUsersByStatus(status, pageable));
    }

    @PostMapping("/users")
    @Operation(summary = "Créer un utilisateur")
    public ResponseEntity<UserDto> createUser(@Valid @RequestBody CreateUserRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userManagementService.createUser(request));
    }

    @PutMapping("/users/{id}")
    @Operation(summary = "Modifier un utilisateur")
    public ResponseEntity<UserDto> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserRequest request) {
        return ResponseEntity.ok(userManagementService.updateUser(id, request));
    }

    @DeleteMapping("/users/{id}")
    @Operation(summary = "Supprimer un utilisateur")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userManagementService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/users/{id}/activate")
    @Operation(summary = "Activer un utilisateur")
    public ResponseEntity<UserDto> activateUser(@PathVariable Long id) {
        return ResponseEntity.ok(userManagementService.activateUser(id));
    }

    @PutMapping("/users/{id}/suspend")
    @Operation(summary = "Suspendre un utilisateur")
    public ResponseEntity<UserDto> suspendUser(@PathVariable Long id) {
        return ResponseEntity.ok(userManagementService.suspendUser(id));
    }

    @PutMapping("/users/{id}/role/{role}")
    @Operation(summary = "Changer le rôle d'un utilisateur")
    public ResponseEntity<UserDto> changeUserRole(
            @PathVariable Long id,
            @PathVariable UserRole role) {
        return ResponseEntity.ok(userManagementService.changeUserRole(id, role));
    }

    // ============ GESTION DES RÔLES ============

    @GetMapping("/roles")
    @Operation(summary = "Liste de tous les rôles")
    public ResponseEntity<List<RoleDto>> getAllRoles() {
        return ResponseEntity.ok(roleService.getAllRoles());
    }

    @GetMapping("/roles/{id}")
    @Operation(summary = "Détails d'un rôle")
    public ResponseEntity<RoleDto> getRoleById(@PathVariable Long id) {
        return ResponseEntity.ok(roleService.getRoleById(id));
    }

    @PostMapping("/roles")
    @Operation(summary = "Créer un rôle")
    public ResponseEntity<RoleDto> createRole(@Valid @RequestBody CreateRoleRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(roleService.createRole(request));
    }

    @PutMapping("/roles/{id}")
    @Operation(summary = "Modifier un rôle")
    public ResponseEntity<RoleDto> updateRole(
            @PathVariable Long id,
            @Valid @RequestBody CreateRoleRequest request) {
        return ResponseEntity.ok(roleService.updateRole(id, request));
    }

    @DeleteMapping("/roles/{id}")
    @Operation(summary = "Supprimer un rôle")
    public ResponseEntity<Void> deleteRole(@PathVariable Long id) {
        roleService.deleteRole(id);
        return ResponseEntity.noContent().build();
    }

    // ============ GESTION DES SEMESTRES ============

    @GetMapping("/semestres")
    @Operation(summary = "Liste de tous les semestres")
    public ResponseEntity<List<SemestreDto>> getAllSemestres() {
        return ResponseEntity.ok(semestreService.getAllSemestres());
    }

    @GetMapping("/semestres/actifs")
    @Operation(summary = "Liste des semestres actifs")
    public ResponseEntity<List<SemestreDto>> getAllSemestresActifs() {
        return ResponseEntity.ok(semestreService.getAllSemestresActifs());
    }

    @GetMapping("/semestres/{id}")
    @Operation(summary = "Détails d'un semestre")
    public ResponseEntity<SemestreDto> getSemestreById(@PathVariable Long id) {
        return ResponseEntity.ok(semestreService.getSemestreById(id));
    }

    @GetMapping("/semestres/annee/{anneeAcademique}")
    @Operation(summary = "Semestres par année académique")
    public ResponseEntity<List<SemestreDto>> getSemestresByAnnee(@PathVariable String anneeAcademique) {
        return ResponseEntity.ok(semestreService.getSemestresByAnneeAcademique(anneeAcademique));
    }

    @PostMapping("/semestres")
    @Operation(summary = "Créer un semestre")
    public ResponseEntity<SemestreDto> createSemestre(@Valid @RequestBody CreateSemestreRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(semestreService.createSemestre(request));
    }

    @PutMapping("/semestres/{id}")
    @Operation(summary = "Modifier un semestre")
    public ResponseEntity<SemestreDto> updateSemestre(
            @PathVariable Long id,
            @Valid @RequestBody CreateSemestreRequest request) {
        return ResponseEntity.ok(semestreService.updateSemestre(id, request));
    }

    @DeleteMapping("/semestres/{id}")
    @Operation(summary = "Supprimer un semestre")
    public ResponseEntity<Void> deleteSemestre(@PathVariable Long id) {
        semestreService.deleteSemestre(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/semestres/{id}/activer")
    @Operation(summary = "Activer un semestre")
    public ResponseEntity<SemestreDto> activerSemestre(@PathVariable Long id) {
        return ResponseEntity.ok(semestreService.activerSemestre(id));
    }

    @PutMapping("/semestres/{id}/desactiver")
    @Operation(summary = "Désactiver un semestre")
    public ResponseEntity<SemestreDto> desactiverSemestre(@PathVariable Long id) {
        return ResponseEntity.ok(semestreService.desactiverSemestre(id));
    }

    // ============ GESTION DES DÉPARTEMENTS ============

    @GetMapping("/departements")
    @Operation(summary = "Liste de tous les départements")
    public ResponseEntity<List<DepartementDto>> getAllDepartements() {
        return ResponseEntity.ok(departementService.getAllDepartements());
    }

    @GetMapping("/departements/paged")
    @Operation(summary = "Liste paginée des départements")
    public ResponseEntity<Page<DepartementDto>> getAllDepartementsPaged(Pageable pageable) {
        return ResponseEntity.ok(departementService.getAllDepartementsPaged(pageable));
    }

    @GetMapping("/departements/{id}")
    @Operation(summary = "Détails d'un département")
    public ResponseEntity<DepartementDto> getDepartementById(@PathVariable Long id) {
        return ResponseEntity.ok(departementService.getDepartementById(id));
    }

    @PostMapping("/departements")
    @Operation(summary = "Créer un département")
    public ResponseEntity<DepartementDto> createDepartement(
            @Valid @RequestBody CreateDepartementRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(departementService.createDepartement(request));
    }

    @PutMapping("/departements/{id}")
    @Operation(summary = "Mettre à jour un département")
    public ResponseEntity<DepartementDto> updateDepartement(
            @PathVariable Long id,
            @Valid @RequestBody UpdateDepartementRequest request) {
        return ResponseEntity.ok(departementService.updateDepartement(id, request));
    }

    @DeleteMapping("/departements/{id}")
    @Operation(summary = "Supprimer un département")
    public ResponseEntity<Void> deleteDepartement(@PathVariable Long id) {
        departementService.deleteDepartement(id);
        return ResponseEntity.noContent().build();
    }

    // ============ GESTION DES MODULES ============

    @GetMapping("/modules")
    @Operation(summary = "Liste de tous les modules")
    public ResponseEntity<List<ModuleDto>> getAllModules() {
        return ResponseEntity.ok(moduleService.getAllModules());
    }

    @GetMapping("/modules/actifs")
    @Operation(summary = "Liste des modules actifs")
    public ResponseEntity<List<ModuleDto>> getAllModulesActifs() {
        return ResponseEntity.ok(moduleService.getAllModulesActifs());
    }

    @GetMapping("/modules/{id}")
    @Operation(summary = "Détails d'un module")
    public ResponseEntity<ModuleDto> getModuleById(@PathVariable Long id) {
        return ResponseEntity.ok(moduleService.getModuleById(id));
    }

    @GetMapping("/modules/semestre/{semestreId}")
    @Operation(summary = "Modules par semestre")
    public ResponseEntity<List<ModuleDto>> getModulesBySemestre(@PathVariable Long semestreId) {
        return ResponseEntity.ok(moduleService.getModulesBySemestre(semestreId));
    }

    @PostMapping("/modules")
    @Operation(summary = "Créer un module")
    public ResponseEntity<ModuleDto> createModule(@Valid @RequestBody CreateModuleRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(moduleService.createModule(request));
    }

    @PutMapping("/modules/{id}")
    @Operation(summary = "Modifier un module")
    public ResponseEntity<ModuleDto> updateModule(
            @PathVariable Long id,
            @Valid @RequestBody CreateModuleRequest request) {
        return ResponseEntity.ok(moduleService.updateModule(id, request));
    }

    @DeleteMapping("/modules/{id}")
    @Operation(summary = "Supprimer un module")
    public ResponseEntity<Void> deleteModule(@PathVariable Long id) {
        moduleService.deleteModule(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/modules/{id}/activer")
    @Operation(summary = "Activer un module")
    public ResponseEntity<ModuleDto> activerModule(@PathVariable Long id) {
        return ResponseEntity.ok(moduleService.activerModule(id));
    }

    @PutMapping("/modules/{id}/desactiver")
    @Operation(summary = "Désactiver un module")
    public ResponseEntity<ModuleDto> desactiverModule(@PathVariable Long id) {
        return ResponseEntity.ok(moduleService.desactiverModule(id));
    }

    // ============ GESTION DES MATIÈRES ============

    @GetMapping("/matieres")
    @Operation(summary = "Liste de toutes les matières")
    public ResponseEntity<List<MatiereDto>> getAllMatieres() {
        return ResponseEntity.ok(matiereService.getAllMatieres());
    }

    @GetMapping("/matieres/actives")
    @Operation(summary = "Liste des matières actives")
    public ResponseEntity<List<MatiereDto>> getAllMatieresActives() {
        return ResponseEntity.ok(matiereService.getAllMatieresActives());
    }

    @GetMapping("/matieres/{id}")
    @Operation(summary = "Détails d'une matière")
    public ResponseEntity<MatiereDto> getMatiereById(@PathVariable Long id) {
        return ResponseEntity.ok(matiereService.getMatiereById(id));
    }

    @GetMapping("/matieres/module/{moduleId}")
    @Operation(summary = "Matières par module")
    public ResponseEntity<List<MatiereDto>> getMatieresByModule(@PathVariable Long moduleId) {
        return ResponseEntity.ok(matiereService.getMatieresByModule(moduleId));
    }

    @PostMapping("/matieres")
    @Operation(summary = "Créer une matière")
    public ResponseEntity<MatiereDto> createMatiere(@Valid @RequestBody CreateMatiereRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(matiereService.createMatiere(request));
    }

    @PutMapping("/matieres/{id}")
    @Operation(summary = "Modifier une matière")
    public ResponseEntity<MatiereDto> updateMatiere(
            @PathVariable Long id,
            @Valid @RequestBody CreateMatiereRequest request) {
        return ResponseEntity.ok(matiereService.updateMatiere(id, request));
    }

    @DeleteMapping("/matieres/{id}")
    @Operation(summary = "Supprimer une matière")
    public ResponseEntity<Void> deleteMatiere(@PathVariable Long id) {
        matiereService.deleteMatiere(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/matieres/{id}/activer")
    @Operation(summary = "Activer une matière")
    public ResponseEntity<MatiereDto> activerMatiere(@PathVariable Long id) {
        return ResponseEntity.ok(matiereService.activerMatiere(id));
    }

    @PutMapping("/matieres/{id}/desactiver")
    @Operation(summary = "Désactiver une matière")
    public ResponseEntity<MatiereDto> desactiverMatiere(@PathVariable Long id) {
        return ResponseEntity.ok(matiereService.desactiverMatiere(id));
    }

    // ============ MODÉRATION DES CONTENUS ============

    @GetMapping("/moderation/cours")
    @Operation(summary = "Liste de tous les cours pour modération")
    public ResponseEntity<List<CoursDto>> getAllCoursForModeration() {
        return ResponseEntity.ok(moderationService.getAllCoursForModeration());
    }

    @GetMapping("/moderation/supports")
    @Operation(summary = "Liste de tous les supports pour modération")
    public ResponseEntity<List<SupportDto>> getAllSupportsForModeration() {
        return ResponseEntity.ok(moderationService.getAllSupportsForModeration());
    }

    @GetMapping("/moderation/devoirs")
    @Operation(summary = "Liste de tous les devoirs pour modération")
    public ResponseEntity<List<DevoirDto>> getAllDevoirsForModeration() {
        return ResponseEntity.ok(moderationService.getAllDevoirsForModeration());
    }

    @GetMapping("/moderation/annonces")
    @Operation(summary = "Liste de toutes les annonces pour modération")
    public ResponseEntity<List<AnnonceDto>> getAllAnnoncesForModeration() {
        return ResponseEntity.ok(moderationService.getAllAnnoncesForModeration());
    }

    @DeleteMapping("/moderation/cours/{id}")
    @Operation(summary = "Supprimer un cours")
    public ResponseEntity<Void> deleteCours(@PathVariable Long id) {
        moderationService.deleteCours(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/moderation/supports/{id}")
    @Operation(summary = "Supprimer un support")
    public ResponseEntity<Void> deleteSupport(@PathVariable Long id) {
        moderationService.deleteSupport(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/moderation/devoirs/{id}")
    @Operation(summary = "Supprimer un devoir")
    public ResponseEntity<Void> deleteDevoir(@PathVariable Long id) {
        moderationService.deleteDevoir(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/moderation/annonces/{id}")
    @Operation(summary = "Supprimer une annonce")
    public ResponseEntity<Void> deleteAnnonce(@PathVariable Long id) {
        moderationService.deleteAnnonce(id);
        return ResponseEntity.noContent().build();
    }

    // ============ STATISTIQUES AVANCÉES ============

    @GetMapping("/statistiques")
    @Operation(summary = "Statistiques globales avancées")
    public ResponseEntity<StatistiquesAvanceesDto> getStatistiquesGlobales() {
        return ResponseEntity.ok(statistiquesAvanceesService.getStatistiquesGlobales());
    }

    @GetMapping("/statistiques/periode/{anneeAcademique}")
    @Operation(summary = "Statistiques par période")
    public ResponseEntity<Map<String, Object>> getStatistiquesByPeriode(@PathVariable String anneeAcademique) {
        return ResponseEntity.ok(statistiquesAvanceesService.getStatistiquesByPeriode(anneeAcademique));
    }

    @GetMapping("/statistiques/departement/{departementId}")
    @Operation(summary = "Statistiques par département")
    public ResponseEntity<Map<String, Object>> getStatistiquesByDepartement(@PathVariable Long departementId) {
        return ResponseEntity.ok(statistiquesAvanceesService.getStatistiquesByDepartement(departementId));
    }

    // ============ VALIDATION DES PROFILS ÉTUDIANTS ============

    @GetMapping("/etudiants")
    @Operation(summary = "Liste de tous les étudiants")
    public ResponseEntity<List<EtudiantDto>> getAllEtudiants() {
        return ResponseEntity.ok(etudiantService.getAllEtudiants());
    }

    @GetMapping("/etudiants/{id}")
    @Operation(summary = "Détails d'un étudiant")
    public ResponseEntity<EtudiantDto> getEtudiantById(@PathVariable Long id) {
        return ResponseEntity.ok(etudiantService.getEtudiantById(id));
    }

    @PutMapping("/etudiants/{id}/valider")
    @Operation(summary = "Valider le profil d'un étudiant")
    public ResponseEntity<EtudiantDto> validerProfil(@PathVariable Long id) {
        return ResponseEntity.ok(etudiantService.validerProfil(id));
    }

    @PutMapping("/etudiants/{id}/suspendre")
    @Operation(summary = "Suspendre un étudiant")
    public ResponseEntity<EtudiantDto> suspendreEtudiant(@PathVariable Long id) {
        return ResponseEntity.ok(etudiantService.suspendreEtudiant(id));
    }
}
