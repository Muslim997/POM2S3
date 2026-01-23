package com.elzocodeur.campusmaster.application.service;

import com.elzocodeur.campusmaster.application.dto.annonce.AnnonceDto;
import com.elzocodeur.campusmaster.application.dto.annonce.CreateAnnonceRequest;
import com.elzocodeur.campusmaster.domain.entity.Annonce;
import com.elzocodeur.campusmaster.domain.entity.Cours;
import com.elzocodeur.campusmaster.domain.entity.Tuteur;
import com.elzocodeur.campusmaster.infrastructure.persistence.repository.AnnonceRepository;
import com.elzocodeur.campusmaster.infrastructure.persistence.repository.CoursRepository;
import com.elzocodeur.campusmaster.infrastructure.persistence.repository.TuteurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AnnonceService {

    private final AnnonceRepository annonceRepository;
    private final TuteurRepository tuteurRepository;
    private final CoursRepository coursRepository;
    private final NotificationService notificationService;

    public List<AnnonceDto> getAllAnnonces() {
        return annonceRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public AnnonceDto getAnnonceById(Long id) {
        Annonce annonce = annonceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Annonce non trouvée"));
        return toDto(annonce);
    }

    public List<AnnonceDto> getAnnoncesByCours(Long coursId) {
        return annonceRepository.findByCoursIdOrderByDatePublicationDesc(coursId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public List<AnnonceDto> getAnnoncesByTuteur(Long tuteurId) {
        return annonceRepository.findByTuteurIdOrderByDatePublicationDesc(tuteurId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public List<AnnonceDto> getAnnoncesGenerales() {
        return annonceRepository.findByCoursIsNullOrderByDatePublicationDesc().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public List<AnnonceDto> getAnnoncesImportantes() {
        return annonceRepository.findAnnoncesImportantes().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public AnnonceDto createAnnonce(CreateAnnonceRequest request) {
        Tuteur tuteur = tuteurRepository.findById(request.getTuteurId())
                .orElseThrow(() -> new RuntimeException("Tuteur non trouvé"));

        Annonce annonce = Annonce.builder()
                .titre(request.getTitre())
                .contenu(request.getContenu())
                .priorite(request.getPriorite())
                .datePublication(LocalDateTime.now())
                .tuteur(tuteur)
                .build();

        if (request.getCoursId() != null) {
            Cours cours = coursRepository.findById(request.getCoursId())
                    .orElseThrow(() -> new RuntimeException("Cours non trouvé"));
            annonce.setCours(cours);

            // Notifier les étudiants du cours
            notificationService.notifierNouvelleAnnonce(cours, annonce);
        } else {
            // Annonce générale - notifier tous les utilisateurs (à implémenter selon besoin)
        }

        annonce = annonceRepository.save(annonce);
        return toDto(annonce);
    }

    @Transactional
    public AnnonceDto updateAnnonce(Long id, CreateAnnonceRequest request) {
        Annonce annonce = annonceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Annonce non trouvée"));

        annonce.setTitre(request.getTitre());
        annonce.setContenu(request.getContenu());
        annonce.setPriorite(request.getPriorite());

        if (request.getCoursId() != null) {
            Cours cours = coursRepository.findById(request.getCoursId())
                    .orElseThrow(() -> new RuntimeException("Cours non trouvé"));
            annonce.setCours(cours);
        } else {
            annonce.setCours(null);
        }

        annonce = annonceRepository.save(annonce);
        return toDto(annonce);
    }

    @Transactional
    public void deleteAnnonce(Long id) {
        Annonce annonce = annonceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Annonce non trouvée"));
        annonceRepository.delete(annonce);
    }

    private AnnonceDto toDto(Annonce annonce) {
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
