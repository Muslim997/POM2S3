package com.elzocodeur.campusmaster.application.service;

import com.elzocodeur.campusmaster.application.dto.matiere.CreateMatiereRequest;
import com.elzocodeur.campusmaster.application.dto.matiere.MatiereDto;
import com.elzocodeur.campusmaster.domain.entity.Matiere;
import com.elzocodeur.campusmaster.domain.entity.Module;
import com.elzocodeur.campusmaster.infrastructure.persistence.repository.MatiereRepository;
import com.elzocodeur.campusmaster.infrastructure.persistence.repository.ModuleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MatiereService {

    private final MatiereRepository matiereRepository;
    private final ModuleRepository moduleRepository;

    public List<MatiereDto> getAllMatieres() {
        return matiereRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public List<MatiereDto> getAllMatieresActives() {
        return matiereRepository.findAllActives().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public MatiereDto getMatiereById(Long id) {
        Matiere matiere = matiereRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Matière non trouvée"));
        return toDto(matiere);
    }

    public MatiereDto getMatiereByCode(String code) {
        Matiere matiere = matiereRepository.findByCode(code)
                .orElseThrow(() -> new RuntimeException("Matière non trouvée"));
        return toDto(matiere);
    }

    public List<MatiereDto> getMatieresByModule(Long moduleId) {
        return matiereRepository.findByModuleId(moduleId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public MatiereDto createMatiere(CreateMatiereRequest request) {
        if (matiereRepository.existsByCode(request.getCode())) {
            throw new RuntimeException("Ce code de matière existe déjà");
        }

        Module module = moduleRepository.findById(request.getModuleId())
                .orElseThrow(() -> new RuntimeException("Module non trouvé"));

        Matiere matiere = Matiere.builder()
                .code(request.getCode())
                .libelle(request.getLibelle())
                .description(request.getDescription())
                .coefficient(request.getCoefficient())
                .volumeHoraire(request.getVolumeHoraire())
                .module(module)
                .actif(true)
                .build();

        matiere = matiereRepository.save(matiere);
        return toDto(matiere);
    }

    @Transactional
    public MatiereDto updateMatiere(Long id, CreateMatiereRequest request) {
        Matiere matiere = matiereRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Matière non trouvée"));

        if (!matiere.getCode().equals(request.getCode()) &&
                matiereRepository.existsByCode(request.getCode())) {
            throw new RuntimeException("Ce code de matière existe déjà");
        }

        Module module = moduleRepository.findById(request.getModuleId())
                .orElseThrow(() -> new RuntimeException("Module non trouvé"));

        matiere.setCode(request.getCode());
        matiere.setLibelle(request.getLibelle());
        matiere.setDescription(request.getDescription());
        matiere.setCoefficient(request.getCoefficient());
        matiere.setVolumeHoraire(request.getVolumeHoraire());
        matiere.setModule(module);

        matiere = matiereRepository.save(matiere);
        return toDto(matiere);
    }

    @Transactional
    public void deleteMatiere(Long id) {
        Matiere matiere = matiereRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Matière non trouvée"));
        matiereRepository.delete(matiere);
    }

    @Transactional
    public MatiereDto activerMatiere(Long id) {
        Matiere matiere = matiereRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Matière non trouvée"));
        matiere.setActif(true);
        matiere = matiereRepository.save(matiere);
        return toDto(matiere);
    }

    @Transactional
    public MatiereDto desactiverMatiere(Long id) {
        Matiere matiere = matiereRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Matière non trouvée"));
        matiere.setActif(false);
        matiere = matiereRepository.save(matiere);
        return toDto(matiere);
    }

    private MatiereDto toDto(Matiere matiere) {
        return MatiereDto.builder()
                .id(matiere.getId())
                .code(matiere.getCode())
                .libelle(matiere.getLibelle())
                .description(matiere.getDescription())
                .coefficient(matiere.getCoefficient())
                .volumeHoraire(matiere.getVolumeHoraire())
                .actif(matiere.getActif())
                .moduleId(matiere.getModule() != null ? matiere.getModule().getId() : null)
                .moduleLibelle(matiere.getModule() != null ? matiere.getModule().getLibelle() : null)
                .nombreCours(matiere.getCours() != null ? matiere.getCours().size() : 0)
                .createdAt(matiere.getCreatedAt())
                .build();
    }
}
