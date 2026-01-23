package com.elzocodeur.campusmaster.application.service;

import com.elzocodeur.campusmaster.application.dto.cours.CoursDto;
import com.elzocodeur.campusmaster.application.dto.cours.CreateCoursRequest;
import com.elzocodeur.campusmaster.domain.entity.Cours;
import com.elzocodeur.campusmaster.domain.entity.Departement;
import com.elzocodeur.campusmaster.domain.entity.Tuteur;
import com.elzocodeur.campusmaster.infrastructure.persistence.repository.CoursRepository;
import com.elzocodeur.campusmaster.infrastructure.persistence.repository.DepartementRepository;
import com.elzocodeur.campusmaster.infrastructure.persistence.repository.TuteurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CoursService {

    private final CoursRepository coursRepository;
    private final TuteurRepository tuteurRepository;
    private final DepartementRepository departementRepository;

    public List<CoursDto> getAllCours() {
        return coursRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public CoursDto getCoursById(Long id) {
        Cours cours = coursRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cours non trouvé"));
        return toDto(cours);
    }

    public List<CoursDto> getCoursByTuteur(Long tuteurId) {
        return coursRepository.findByTuteurId(tuteurId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public List<CoursDto> getCoursByDepartement(Long departementId) {
        return coursRepository.findByDepartementId(departementId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public CoursDto createCours(CreateCoursRequest request) {
        Tuteur tuteur = tuteurRepository.findById(request.getTuteurId())
                .orElseThrow(() -> new RuntimeException("Tuteur non trouvé"));

        Cours cours = Cours.builder()
                .titre(request.getTitre())
                .description(request.getDescription())
                .semestre(request.getSemestre())
                .tuteur(tuteur)
                .build();

        if (request.getDepartementId() != null) {
            Departement departement = departementRepository.findById(request.getDepartementId())
                    .orElseThrow(() -> new RuntimeException("Département non trouvé"));
            cours.setDepartement(departement);
        }

        cours = coursRepository.save(cours);
        return toDto(cours);
    }

    @Transactional
    public CoursDto updateCours(Long id, CreateCoursRequest request) {
        Cours cours = coursRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cours non trouvé"));

        cours.setTitre(request.getTitre());
        cours.setDescription(request.getDescription());
        cours.setSemestre(request.getSemestre());

        if (request.getTuteurId() != null) {
            Tuteur tuteur = tuteurRepository.findById(request.getTuteurId())
                    .orElseThrow(() -> new RuntimeException("Tuteur non trouvé"));
            cours.setTuteur(tuteur);
        }

        if (request.getDepartementId() != null) {
            Departement departement = departementRepository.findById(request.getDepartementId())
                    .orElseThrow(() -> new RuntimeException("Département non trouvé"));
            cours.setDepartement(departement);
        }

        cours = coursRepository.save(cours);
        return toDto(cours);
    }

    @Transactional
    public void deleteCours(Long id) {
        Cours cours = coursRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cours non trouvé"));
        coursRepository.delete(cours);
    }

    private CoursDto toDto(Cours cours) {
        return CoursDto.builder()
                .id(cours.getId())
                .titre(cours.getTitre())
                .description(cours.getDescription())
                .semestre(cours.getSemestre())
                .tuteurId(cours.getTuteur() != null ? cours.getTuteur().getId() : null)
                .tuteurNom(cours.getTuteur() != null && cours.getTuteur().getUser() != null ?
                        cours.getTuteur().getUser().getFirstName() + " " + cours.getTuteur().getUser().getLastName() : null)
                .departementId(cours.getDepartement() != null ? cours.getDepartement().getId() : null)
                .departementNom(cours.getDepartement() != null ? cours.getDepartement().getLibelle() : null)
                .nombreSupports(cours.getSupports() != null ? cours.getSupports().size() : 0)
                .nombreDevoirs(cours.getDevoirs() != null ? cours.getDevoirs().size() : 0)
                .createdAt(cours.getCreatedAt())
                .build();
    }
}
