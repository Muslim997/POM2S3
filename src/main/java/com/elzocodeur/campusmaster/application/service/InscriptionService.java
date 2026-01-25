package com.elzocodeur.campusmaster.application.service;

import com.elzocodeur.campusmaster.application.dto.inscription.InscriptionDto;
import com.elzocodeur.campusmaster.domain.entity.Cours;
import com.elzocodeur.campusmaster.domain.entity.Etudiant;
import com.elzocodeur.campusmaster.domain.entity.Inscription;
import com.elzocodeur.campusmaster.infrastructure.persistence.repository.CoursRepository;
import com.elzocodeur.campusmaster.infrastructure.persistence.repository.EtudiantRepository;
import com.elzocodeur.campusmaster.infrastructure.persistence.repository.InscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InscriptionService {

    private final InscriptionRepository inscriptionRepository;
    private final EtudiantRepository etudiantRepository;
    private final CoursRepository coursRepository;

    public List<InscriptionDto> getAllInscriptions() {
        return inscriptionRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public List<InscriptionDto> getInscriptionsByEtudiant(Long etudiantId) {
        return inscriptionRepository.findByEtudiantId(etudiantId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Récupère les inscriptions par userId de l'étudiant (plus pratique pour le frontend)
     */
    public List<InscriptionDto> getInscriptionsByEtudiantUserId(Long userId) {
        Etudiant etudiant = etudiantRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Étudiant non trouvé pour cet utilisateur"));
        return inscriptionRepository.findByEtudiantId(etudiant.getId()).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public List<InscriptionDto> getInscriptionsByCours(Long coursId) {
        return inscriptionRepository.findByCoursId(coursId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public InscriptionDto inscrireEtudiant(Long etudiantId, Long coursId) {
        Etudiant etudiant = etudiantRepository.findById(etudiantId)
                .orElseThrow(() -> new RuntimeException("Étudiant non trouvé"));

        Cours cours = coursRepository.findById(coursId)
                .orElseThrow(() -> new RuntimeException("Cours non trouvé"));

        Inscription inscription = Inscription.builder()
                .dateInscription(LocalDate.now())
                .etudiant(etudiant)
                .cours(cours)
                .build();

        inscription = inscriptionRepository.save(inscription);
        return toDto(inscription);
    }

    /**
     * Inscrire un étudiant par userId (plus pratique pour le frontend)
     */
    @Transactional
    public InscriptionDto inscrireEtudiantByUserId(Long userId, Long coursId) {
        Etudiant etudiant = etudiantRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Étudiant non trouvé pour cet utilisateur"));

        Cours cours = coursRepository.findById(coursId)
                .orElseThrow(() -> new RuntimeException("Cours non trouvé"));

        Inscription inscription = Inscription.builder()
                .dateInscription(LocalDate.now())
                .etudiant(etudiant)
                .cours(cours)
                .build();

        inscription = inscriptionRepository.save(inscription);
        return toDto(inscription);
    }

    @Transactional
    public void desinscrireEtudiant(Long inscriptionId) {
        Inscription inscription = inscriptionRepository.findById(inscriptionId)
                .orElseThrow(() -> new RuntimeException("Inscription non trouvée"));
        inscriptionRepository.delete(inscription);
    }

    private InscriptionDto toDto(Inscription inscription) {
        return InscriptionDto.builder()
                .id(inscription.getId())
                .dateInscription(inscription.getDateInscription())
                .etudiantId(inscription.getEtudiant() != null ? inscription.getEtudiant().getId() : null)
                .etudiantUserId(inscription.getEtudiant() != null && inscription.getEtudiant().getUser() != null ?
                        inscription.getEtudiant().getUser().getId() : null)
                .etudiantNom(inscription.getEtudiant() != null && inscription.getEtudiant().getUser() != null ?
                        inscription.getEtudiant().getUser().getFirstName() + " " + inscription.getEtudiant().getUser().getLastName() : null)
                .coursId(inscription.getCours() != null ? inscription.getCours().getId() : null)
                .coursNom(inscription.getCours() != null ? inscription.getCours().getTitre() : null)
                .build();
    }
}
