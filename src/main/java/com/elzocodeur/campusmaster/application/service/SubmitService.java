package com.elzocodeur.campusmaster.application.service;

import com.elzocodeur.campusmaster.application.dto.submit.CreateSubmitRequest;
import com.elzocodeur.campusmaster.application.dto.submit.SubmitDto;
import com.elzocodeur.campusmaster.domain.entity.Devoir;
import com.elzocodeur.campusmaster.domain.entity.Etudiant;
import com.elzocodeur.campusmaster.domain.entity.Submit;
import com.elzocodeur.campusmaster.infrastructure.persistence.repository.DevoirRepository;
import com.elzocodeur.campusmaster.infrastructure.persistence.repository.EtudiantRepository;
import com.elzocodeur.campusmaster.infrastructure.persistence.repository.SubmitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SubmitService {

    private final SubmitRepository submitRepository;
    private final DevoirRepository devoirRepository;
    private final EtudiantRepository etudiantRepository;
    private final NotificationService notificationService;

    public List<SubmitDto> getAllSubmits() {
        return submitRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public SubmitDto getSubmitById(Long id) {
        Submit submit = submitRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Soumission non trouvée"));
        return toDto(submit);
    }

    public List<SubmitDto> getSubmitsByDevoir(Long devoirId) {
        return submitRepository.findByDevoirId(devoirId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public List<SubmitDto> getSubmitsByEtudiant(Long etudiantId) {
        return submitRepository.findByEtudiantId(etudiantId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public List<SubmitDto> getSubmitHistory(Long devoirId, Long etudiantId) {
        return submitRepository.findByDevoirIdAndEtudiantIdOrderByCreatedAtDesc(devoirId, etudiantId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public SubmitDto createSubmit(CreateSubmitRequest request, Long etudiantId) {
        Devoir devoir = devoirRepository.findById(request.getDevoirId())
                .orElseThrow(() -> new RuntimeException("Devoir non trouvé"));

        Etudiant etudiant = etudiantRepository.findById(etudiantId)
                .orElseThrow(() -> new RuntimeException("Étudiant non trouvé"));

        // Vérifier si la deadline est passée
        if (devoir.getDateLimite() != null && LocalDateTime.now().isAfter(devoir.getDateLimite())) {
            throw new RuntimeException("La date limite est dépassée");
        }

        Submit submit = Submit.builder()
                .dateSoumission(LocalDateTime.now())
                .fichierUrl(request.getFichierUrl())
                .devoir(devoir)
                .etudiant(etudiant)
                .build();

        submit = submitRepository.save(submit);
        return toDto(submit);
    }

    @Transactional
    public SubmitDto updateSubmit(Long id, CreateSubmitRequest request, Long etudiantId) {
        Submit submit = submitRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Soumission non trouvée"));

        // Vérifier que c'est bien l'étudiant propriétaire
        if (!submit.getEtudiant().getId().equals(etudiantId)) {
            throw new RuntimeException("Non autorisé à modifier cette soumission");
        }

        // Vérifier si la deadline est passée
        if (submit.getDevoir().getDateLimite() != null &&
            LocalDateTime.now().isAfter(submit.getDevoir().getDateLimite())) {
            throw new RuntimeException("La date limite est dépassée");
        }

        submit.setFichierUrl(request.getFichierUrl());
        submit.setDateSoumission(LocalDateTime.now());

        submit = submitRepository.save(submit);
        return toDto(submit);
    }

    @Transactional
    public SubmitDto evaluerSubmit(Long id, Double note, String feedback) {
        Submit submit = submitRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Soumission non trouvée"));

        submit.setNote(note);
        submit.setFeedback(feedback);

        submit = submitRepository.save(submit);

        // Notifier l'étudiant
        notificationService.notifierNotePubliee(submit);

        return toDto(submit);
    }

    @Transactional
    public void deleteSubmit(Long id) {
        Submit submit = submitRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Soumission non trouvée"));
        submitRepository.delete(submit);
    }

    private SubmitDto toDto(Submit submit) {
        return SubmitDto.builder()
                .id(submit.getId())
                .dateSoumission(submit.getDateSoumission())
                .note(submit.getNote())
                .feedback(submit.getFeedback())
                .fichierUrl(submit.getFichierUrl())
                .devoirId(submit.getDevoir() != null ? submit.getDevoir().getId() : null)
                .devoirTitre(submit.getDevoir() != null ? submit.getDevoir().getTitre() : null)
                .etudiantId(submit.getEtudiant() != null ? submit.getEtudiant().getId() : null)
                .etudiantNom(submit.getEtudiant() != null && submit.getEtudiant().getUser() != null ?
                        submit.getEtudiant().getUser().getFirstName() + " " + submit.getEtudiant().getUser().getLastName() : null)
                .createdAt(submit.getCreatedAt())
                .updatedAt(submit.getUpdatedAt())
                .build();
    }
}
