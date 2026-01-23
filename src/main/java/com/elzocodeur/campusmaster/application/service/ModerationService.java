package com.elzocodeur.campusmaster.application.service;

import com.elzocodeur.campusmaster.application.dto.annonce.AnnonceDto;
import com.elzocodeur.campusmaster.application.dto.cours.CoursDto;
import com.elzocodeur.campusmaster.application.dto.devoir.DevoirDto;
import com.elzocodeur.campusmaster.application.dto.support.SupportDto;
import com.elzocodeur.campusmaster.domain.entity.Annonce;
import com.elzocodeur.campusmaster.domain.entity.Cours;
import com.elzocodeur.campusmaster.domain.entity.Devoir;
import com.elzocodeur.campusmaster.domain.entity.Support;
import com.elzocodeur.campusmaster.infrastructure.persistence.repository.AnnonceRepository;
import com.elzocodeur.campusmaster.infrastructure.persistence.repository.CoursRepository;
import com.elzocodeur.campusmaster.infrastructure.persistence.repository.DevoirRepository;
import com.elzocodeur.campusmaster.infrastructure.persistence.repository.SupportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ModerationService {

    private final CoursRepository coursRepository;
    private final SupportRepository supportRepository;
    private final DevoirRepository devoirRepository;
    private final AnnonceRepository annonceRepository;

    public List<CoursDto> getAllCoursForModeration() {
        return coursRepository.findAll().stream()
                .map(this::toCoursDto)
                .collect(Collectors.toList());
    }

    public List<SupportDto> getAllSupportsForModeration() {
        return supportRepository.findAll().stream()
                .map(this::toSupportDto)
                .collect(Collectors.toList());
    }

    public List<DevoirDto> getAllDevoirsForModeration() {
        return devoirRepository.findAll().stream()
                .map(this::toDevoirDto)
                .collect(Collectors.toList());
    }

    public List<AnnonceDto> getAllAnnoncesForModeration() {
        return annonceRepository.findAll().stream()
                .map(this::toAnnonceDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteCours(Long coursId) {
        Cours cours = coursRepository.findById(coursId)
                .orElseThrow(() -> new RuntimeException("Cours non trouvé"));
        coursRepository.delete(cours);
    }

    @Transactional
    public void deleteSupport(Long supportId) {
        Support support = supportRepository.findById(supportId)
                .orElseThrow(() -> new RuntimeException("Support non trouvé"));
        supportRepository.delete(support);
    }

    @Transactional
    public void deleteDevoir(Long devoirId) {
        Devoir devoir = devoirRepository.findById(devoirId)
                .orElseThrow(() -> new RuntimeException("Devoir non trouvé"));
        devoirRepository.delete(devoir);
    }

    @Transactional
    public void deleteAnnonce(Long annonceId) {
        Annonce annonce = annonceRepository.findById(annonceId)
                .orElseThrow(() -> new RuntimeException("Annonce non trouvée"));
        annonceRepository.delete(annonce);
    }

    public List<CoursDto> getCoursByDepartement(Long departementId) {
        return coursRepository.findByDepartementId(departementId).stream()
                .map(this::toCoursDto)
                .collect(Collectors.toList());
    }

    public List<SupportDto> getSupportsByCours(Long coursId) {
        return supportRepository.findByCoursId(coursId).stream()
                .map(this::toSupportDto)
                .collect(Collectors.toList());
    }

    public List<DevoirDto> getDevoirsByCours(Long coursId) {
        return devoirRepository.findByCoursId(coursId).stream()
                .map(this::toDevoirDto)
                .collect(Collectors.toList());
    }

    private CoursDto toCoursDto(Cours cours) {
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
                .createdAt(cours.getCreatedAt())
                .build();
    }

    private SupportDto toSupportDto(Support support) {
        return SupportDto.builder()
                .id(support.getId())
                .titre(support.getTitre())
                .description(support.getDescription())
                .typeFichier(support.getTypeFichier())
                .urlFichier(support.getUrlFichier())
                .coursId(support.getCours() != null ? support.getCours().getId() : null)
                .coursNom(support.getCours() != null ? support.getCours().getTitre() : null)
                .createdAt(support.getCreatedAt())
                .build();
    }

    private DevoirDto toDevoirDto(Devoir devoir) {
        return DevoirDto.builder()
                .id(devoir.getId())
                .titre(devoir.getTitre())
                .description(devoir.getDescription())
                .dateLimite(devoir.getDateLimite())
                .coursId(devoir.getCours() != null ? devoir.getCours().getId() : null)
                .coursNom(devoir.getCours() != null ? devoir.getCours().getTitre() : null)
                .createdAt(devoir.getCreatedAt())
                .build();
    }

    private AnnonceDto toAnnonceDto(Annonce annonce) {
        return AnnonceDto.builder()
                .id(annonce.getId())
                .titre(annonce.getTitre())
                .contenu(annonce.getContenu())
                .datePublication(annonce.getDatePublication())
                .priorite(annonce.getPriorite())
                .coursId(annonce.getCours() != null ? annonce.getCours().getId() : null)
                .coursNom(annonce.getCours() != null ? annonce.getCours().getTitre() : null)
                .tuteurId(annonce.getTuteur() != null ? annonce.getTuteur().getId() : null)
                .tuteurNom(annonce.getTuteur() != null && annonce.getTuteur().getUser() != null ?
                        annonce.getTuteur().getUser().getFirstName() + " " + annonce.getTuteur().getUser().getLastName() : null)
                .createdAt(annonce.getCreatedAt())
                .build();
    }
}
