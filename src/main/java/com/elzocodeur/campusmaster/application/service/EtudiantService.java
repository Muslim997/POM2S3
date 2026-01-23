package com.elzocodeur.campusmaster.application.service;

import com.elzocodeur.campusmaster.application.dto.etudiant.EtudiantDto;
import com.elzocodeur.campusmaster.application.dto.stats.EtudiantProgressDto;
import com.elzocodeur.campusmaster.domain.entity.Etudiant;
import com.elzocodeur.campusmaster.domain.entity.Submit;
import com.elzocodeur.campusmaster.domain.enums.UserStatus;
import com.elzocodeur.campusmaster.infrastructure.persistence.repository.EtudiantRepository;
import com.elzocodeur.campusmaster.infrastructure.persistence.repository.InscriptionRepository;
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
public class EtudiantService {

    private final EtudiantRepository etudiantRepository;
    private final InscriptionRepository inscriptionRepository;
    private final SubmitRepository submitRepository;

    public List<EtudiantDto> getAllEtudiants() {
        return etudiantRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public EtudiantDto getEtudiantById(Long id) {
        Etudiant etudiant = etudiantRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Étudiant non trouvé"));
        return toDto(etudiant);
    }

    public List<EtudiantDto> getEtudiantsByCours(Long coursId) {
        return inscriptionRepository.findByCoursId(coursId).stream()
                .map(inscription -> toDto(inscription.getEtudiant()))
                .collect(Collectors.toList());
    }

    public EtudiantProgressDto getEtudiantProgress(Long etudiantId) {
        Etudiant etudiant = etudiantRepository.findById(etudiantId)
                .orElseThrow(() -> new RuntimeException("Étudiant non trouvé"));

        int nombreCoursInscrits = inscriptionRepository.findByEtudiantId(etudiantId).size();
        List<Submit> submits = submitRepository.findByEtudiantId(etudiantId);

        int nombreDevoirsRendus = (int) submits.stream()
                .filter(s -> s.getDateSoumission() != null)
                .count();

        int nombreDevoirsEnRetard = (int) submits.stream()
                .filter(s -> s.getDevoir().getDateLimite() != null &&
                        s.getDateSoumission() != null &&
                        s.getDateSoumission().isAfter(s.getDevoir().getDateLimite()))
                .count();

        Double moyenneGenerale = submits.stream()
                .filter(s -> s.getNote() != null)
                .mapToDouble(Submit::getNote)
                .average()
                .orElse(0.0);

        int tauxAssiduité = nombreCoursInscrits > 0 ?
                (int) ((nombreDevoirsRendus * 100.0) / nombreCoursInscrits) : 0;

        return EtudiantProgressDto.builder()
                .etudiantId(etudiant.getId())
                .etudiantNom(etudiant.getUser().getFirstName() + " " + etudiant.getUser().getLastName())
                .numeroEtudiant(etudiant.getNumeroEtudiant())
                .nombreCoursInscrits(nombreCoursInscrits)
                .nombreDevoirsRendus(nombreDevoirsRendus)
                .nombreDevoirsEnRetard(nombreDevoirsEnRetard)
                .moyenneGenerale(moyenneGenerale)
                .tauxAssiduité(tauxAssiduité)
                .build();
    }

    @Transactional
    public EtudiantDto validerProfil(Long etudiantId) {
        Etudiant etudiant = etudiantRepository.findById(etudiantId)
                .orElseThrow(() -> new RuntimeException("Étudiant non trouvé"));

        etudiant.getUser().setStatus(UserStatus.ACTIVE);
        etudiantRepository.save(etudiant);

        return toDto(etudiant);
    }

    @Transactional
    public EtudiantDto suspendreEtudiant(Long etudiantId) {
        Etudiant etudiant = etudiantRepository.findById(etudiantId)
                .orElseThrow(() -> new RuntimeException("Étudiant non trouvé"));

        etudiant.getUser().setStatus(UserStatus.SUSPENDED);
        etudiantRepository.save(etudiant);

        return toDto(etudiant);
    }

    private EtudiantDto toDto(Etudiant etudiant) {
        return EtudiantDto.builder()
                .id(etudiant.getId())
                .numeroEtudiant(etudiant.getNumeroEtudiant())
                .userId(etudiant.getUser() != null ? etudiant.getUser().getId() : null)
                .username(etudiant.getUser() != null ? etudiant.getUser().getUsername() : null)
                .email(etudiant.getUser() != null ? etudiant.getUser().getEmail() : null)
                .firstName(etudiant.getUser() != null ? etudiant.getUser().getFirstName() : null)
                .lastName(etudiant.getUser() != null ? etudiant.getUser().getLastName() : null)
                .phoneNumber(etudiant.getUser() != null ? etudiant.getUser().getPhoneNumber() : null)
                .status(etudiant.getUser() != null ? etudiant.getUser().getStatus().name() : null)
                .departementId(etudiant.getDepartement() != null ? etudiant.getDepartement().getId() : null)
                .departementNom(etudiant.getDepartement() != null ? etudiant.getDepartement().getLibelle() : null)
                .profilValide(etudiant.getUser() != null && etudiant.getUser().getStatus() == UserStatus.ACTIVE)
                .build();
    }
}
