package com.elzocodeur.campusmaster.application.service;

import com.elzocodeur.campusmaster.application.dto.departement.CreateDepartementRequest;
import com.elzocodeur.campusmaster.application.dto.departement.DepartementDto;
import com.elzocodeur.campusmaster.application.dto.departement.UpdateDepartementRequest;
import com.elzocodeur.campusmaster.domain.entity.Departement;
import com.elzocodeur.campusmaster.domain.exception.business.ResourceNotFoundException;
import com.elzocodeur.campusmaster.infrastructure.persistence.repository.DepartementRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DepartementService {

    private final DepartementRepository departementRepository;

    public List<DepartementDto> getAllDepartements() {
        log.info("Récupération de tous les départements");
        return departementRepository.findAll().stream()
                .map(this::toDepartementDto)
                .collect(Collectors.toList());
    }

    public DepartementDto getDepartementById(Long id) {
        log.info("Récupération du département avec l'ID: {}", id);
        Departement departement = departementRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Département", id));
        return toDepartementDto(departement);
    }

    @Transactional
    public DepartementDto createDepartement(CreateDepartementRequest request) {
        log.info("Création d'un nouveau département: {}", request.getLibelle());

        Departement departement = Departement.builder()
                .libelle(request.getLibelle())
                .description(request.getDescription())
                .build();

        Departement saved = departementRepository.save(departement);
        log.info("Département créé avec succès avec l'ID: {}", saved.getId());

        return toDepartementDto(saved);
    }

    @Transactional
    public DepartementDto updateDepartement(Long id, UpdateDepartementRequest request) {
        log.info("Mise à jour du département avec l'ID: {}", id);

        Departement departement = departementRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Département", id));

        if (request.getLibelle() != null) {
            departement.setLibelle(request.getLibelle());
        }
        if (request.getDescription() != null) {
            departement.setDescription(request.getDescription());
        }

        Departement updated = departementRepository.save(departement);
        log.info("Département mis à jour avec succès");

        return toDepartementDto(updated);
    }

    @Transactional
    public void deleteDepartement(Long id) {
        log.info("Suppression du département avec l'ID: {}", id);

        Departement departement = departementRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Département", id));

        departementRepository.delete(departement);
        log.info("Département supprimé avec succès");
    }

    private DepartementDto toDepartementDto(Departement departement) {
        return DepartementDto.builder()
                .id(departement.getId())
                .libelle(departement.getLibelle())
                .description(departement.getDescription())
                .createdAt(departement.getCreatedAt())
                .updatedAt(departement.getUpdatedAt())
                .build();
    }
}
