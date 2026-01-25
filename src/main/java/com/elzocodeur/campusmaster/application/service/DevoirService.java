package com.elzocodeur.campusmaster.application.service;

import com.elzocodeur.campusmaster.application.dto.devoir.CreateDevoirRequest;
import com.elzocodeur.campusmaster.application.dto.devoir.DevoirDto;
import com.elzocodeur.campusmaster.domain.entity.Cours;
import com.elzocodeur.campusmaster.domain.entity.Devoir;
import com.elzocodeur.campusmaster.domain.entity.Tuteur;
import com.elzocodeur.campusmaster.infrastructure.persistence.repository.CoursRepository;
import com.elzocodeur.campusmaster.infrastructure.persistence.repository.DevoirRepository;
import com.elzocodeur.campusmaster.infrastructure.persistence.repository.SubmitRepository;
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
public class DevoirService {

    private final DevoirRepository devoirRepository;
    private final CoursRepository coursRepository;
    private final SubmitRepository submitRepository;
    private final TuteurRepository tuteurRepository;
    private final NotificationService notificationService;

    public List<DevoirDto> getAllDevoirs() {
        return devoirRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public DevoirDto getDevoirById(Long id) {
        Devoir devoir = devoirRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Devoir non trouvé"));
        return toDto(devoir);
    }

    public List<DevoirDto> getDevoirsByCours(Long coursId) {
        return devoirRepository.findByCoursId(coursId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public List<DevoirDto> getDevoirsActifsByCours(Long coursId) {
        return devoirRepository.findByCoursIdAndDateLimiteAfter(coursId, LocalDateTime.now()).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public List<DevoirDto> getDevoirsByPeriod(LocalDateTime start, LocalDateTime end) {
        return devoirRepository.findByDateLimiteBetween(start, end).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Récupère tous les devoirs d'un tuteur (via ses cours) par tuteurId (ID table tuteurs)
     */
    public List<DevoirDto> getDevoirsByTuteur(Long tuteurId) {
        // Récupérer tous les cours du tuteur
        List<Cours> cours = coursRepository.findByTuteurId(tuteurId);

        // Récupérer tous les devoirs de ces cours
        return cours.stream()
                .flatMap(c -> devoirRepository.findByCoursId(c.getId()).stream())
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Récupère tous les devoirs d'un tuteur (via ses cours) par userId (ID table users - recommandé)
     */
    public List<DevoirDto> getDevoirsByTuteurUserId(Long userId) {
        Tuteur tuteur = tuteurRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Tuteur non trouvé pour cet utilisateur"));

        return getDevoirsByTuteur(tuteur.getId());
    }

    @Transactional
    public DevoirDto createDevoir(CreateDevoirRequest request) {
        Cours cours = coursRepository.findById(request.getCoursId())
                .orElseThrow(() -> new RuntimeException("Cours non trouvé"));

        Devoir devoir = Devoir.builder()
                .titre(request.getTitre())
                .description(request.getDescription())
                .dateLimite(request.getDateLimite())
                .cours(cours)
                .build();

        devoir = devoirRepository.save(devoir);

        // Notifier les étudiants inscrits au cours
        notificationService.notifierNouveauDevoir(cours, devoir);

        return toDto(devoir);
    }

    @Transactional
    public DevoirDto updateDevoir(Long id, CreateDevoirRequest request) {
        Devoir devoir = devoirRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Devoir non trouvé"));

        devoir.setTitre(request.getTitre());
        devoir.setDescription(request.getDescription());
        devoir.setDateLimite(request.getDateLimite());

        if (request.getCoursId() != null && !request.getCoursId().equals(devoir.getCours().getId())) {
            Cours cours = coursRepository.findById(request.getCoursId())
                    .orElseThrow(() -> new RuntimeException("Cours non trouvé"));
            devoir.setCours(cours);
        }

        devoir = devoirRepository.save(devoir);
        return toDto(devoir);
    }

    @Transactional
    public void deleteDevoir(Long id) {
        Devoir devoir = devoirRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Devoir non trouvé"));
        devoirRepository.delete(devoir);
    }

    private DevoirDto toDto(Devoir devoir) {
        Cours cours = devoir.getCours();
        Tuteur tuteur = cours != null ? cours.getTuteur() : null;

        return DevoirDto.builder()
                .id(devoir.getId())
                .titre(devoir.getTitre())
                .description(devoir.getDescription())
                .dateLimite(devoir.getDateLimite())
                .coursId(cours != null ? cours.getId() : null)
                .coursNom(cours != null ? cours.getTitre() : null)
                .coursSemestre(cours != null ? cours.getSemestre() : null)
                .tuteurId(tuteur != null ? tuteur.getId() : null)
                .tuteurUserId(tuteur != null && tuteur.getUser() != null ? tuteur.getUser().getId() : null)
                .tuteurNom(tuteur != null && tuteur.getUser() != null ?
                        tuteur.getUser().getFirstName() + " " + tuteur.getUser().getLastName() : null)
                .nombreSubmissions(devoir.getSubmits() != null ? devoir.getSubmits().size() : 0)
                .createdAt(devoir.getCreatedAt())
                .build();
    }
}
