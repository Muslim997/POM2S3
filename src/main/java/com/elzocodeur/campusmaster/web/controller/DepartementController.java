package com.elzocodeur.campusmaster.web.controller;

import com.elzocodeur.campusmaster.application.dto.departement.CreateDepartementRequest;
import com.elzocodeur.campusmaster.application.dto.departement.DepartementDto;
import com.elzocodeur.campusmaster.application.dto.departement.UpdateDepartementRequest;
import com.elzocodeur.campusmaster.application.service.DepartementService;
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

@RestController
@RequestMapping("/departements")
@RequiredArgsConstructor
@Tag(name = "Départements", description = "Gestion des départements")
@SecurityRequirement(name = "Bearer Authentication")
public class DepartementController {

    private final DepartementService departementService;

    @GetMapping
    @Operation(summary = "Liste de tous les départements")
    public ResponseEntity<List<DepartementDto>> getAllDepartements() {
        return ResponseEntity.ok(departementService.getAllDepartements());
    }

    @GetMapping("/paged")
    @Operation(summary = "Liste paginée des départements")
    public ResponseEntity<Page<DepartementDto>> getAllDepartementsPaged(Pageable pageable) {
        return ResponseEntity.ok(departementService.getAllDepartementsPaged(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Récupérer un département par ID")
    public ResponseEntity<DepartementDto> getDepartementById(@PathVariable Long id) {
        return ResponseEntity.ok(departementService.getDepartementById(id));
    }

    @PostMapping
    @Operation(summary = "Créer un nouveau département")
    public ResponseEntity<DepartementDto> createDepartement(
            @Valid @RequestBody CreateDepartementRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(departementService.createDepartement(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Mettre à jour un département")
    public ResponseEntity<DepartementDto> updateDepartement(
            @PathVariable Long id,
            @Valid @RequestBody UpdateDepartementRequest request) {
        return ResponseEntity.ok(departementService.updateDepartement(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un département")
    public ResponseEntity<Void> deleteDepartement(@PathVariable Long id) {
        departementService.deleteDepartement(id);
        return ResponseEntity.noContent().build();
    }
}
