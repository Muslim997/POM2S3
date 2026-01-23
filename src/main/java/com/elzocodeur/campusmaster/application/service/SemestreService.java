package com.elzocodeur.campusmaster.application.service;

import com.elzocodeur.campusmaster.application.dto.semestre.CreateSemestreRequest;
import com.elzocodeur.campusmaster.application.dto.semestre.SemestreDto;
import com.elzocodeur.campusmaster.domain.entity.Semestre;
import com.elzocodeur.campusmaster.infrastructure.persistence.repository.SemestreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SemestreService {

    private final SemestreRepository semestreRepository;

    public List<SemestreDto> getAllSemestres() {
        return semestreRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public List<SemestreDto> getAllSemestresActifs() {
        return semestreRepository.findAllActifs().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public SemestreDto getSemestreById(Long id) {
        Semestre semestre = semestreRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Semestre non trouvé"));
        return toDto(semestre);
    }

    public SemestreDto getSemestreByCode(String code) {
        Semestre semestre = semestreRepository.findByCode(code)
                .orElseThrow(() -> new RuntimeException("Semestre non trouvé"));
        return toDto(semestre);
    }

    public List<SemestreDto> getSemestresByAnneeAcademique(String anneeAcademique) {
        return semestreRepository.findByAnneeAcademique(anneeAcademique).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public SemestreDto createSemestre(CreateSemestreRequest request) {
        if (semestreRepository.existsByCode(request.getCode())) {
            throw new RuntimeException("Ce code de semestre existe déjà");
        }

        Semestre semestre = Semestre.builder()
                .code(request.getCode())
                .libelle(request.getLibelle())
                .anneeAcademique(request.getAnneeAcademique())
                .dateDebut(request.getDateDebut())
                .dateFin(request.getDateFin())
                .actif(true)
                .build();

        semestre = semestreRepository.save(semestre);
        return toDto(semestre);
    }

    @Transactional
    public SemestreDto updateSemestre(Long id, CreateSemestreRequest request) {
        Semestre semestre = semestreRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Semestre non trouvé"));

        if (!semestre.getCode().equals(request.getCode()) &&
                semestreRepository.existsByCode(request.getCode())) {
            throw new RuntimeException("Ce code de semestre existe déjà");
        }

        semestre.setCode(request.getCode());
        semestre.setLibelle(request.getLibelle());
        semestre.setAnneeAcademique(request.getAnneeAcademique());
        semestre.setDateDebut(request.getDateDebut());
        semestre.setDateFin(request.getDateFin());

        semestre = semestreRepository.save(semestre);
        return toDto(semestre);
    }

    @Transactional
    public void deleteSemestre(Long id) {
        Semestre semestre = semestreRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Semestre non trouvé"));
        semestreRepository.delete(semestre);
    }

    @Transactional
    public SemestreDto activerSemestre(Long id) {
        Semestre semestre = semestreRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Semestre non trouvé"));
        semestre.setActif(true);
        semestre = semestreRepository.save(semestre);
        return toDto(semestre);
    }

    @Transactional
    public SemestreDto desactiverSemestre(Long id) {
        Semestre semestre = semestreRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Semestre non trouvé"));
        semestre.setActif(false);
        semestre = semestreRepository.save(semestre);
        return toDto(semestre);
    }

    private SemestreDto toDto(Semestre semestre) {
        return SemestreDto.builder()
                .id(semestre.getId())
                .code(semestre.getCode())
                .libelle(semestre.getLibelle())
                .anneeAcademique(semestre.getAnneeAcademique())
                .dateDebut(semestre.getDateDebut())
                .dateFin(semestre.getDateFin())
                .actif(semestre.getActif())
                .nombreModules(semestre.getModules() != null ? semestre.getModules().size() : 0)
                .createdAt(semestre.getCreatedAt())
                .build();
    }
}
