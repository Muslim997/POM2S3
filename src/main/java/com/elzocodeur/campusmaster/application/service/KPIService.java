package com.elzocodeur.campusmaster.application.service;

import com.elzocodeur.campusmaster.application.dto.dashboard.KPIDto;
import com.elzocodeur.campusmaster.domain.entity.KPIConfiguration;
import com.elzocodeur.campusmaster.domain.entity.Submit;
import com.elzocodeur.campusmaster.domain.enums.UserStatus;
import com.elzocodeur.campusmaster.infrastructure.persistence.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class KPIService {

    private final KPIConfigurationRepository kpiConfigurationRepository;
    private final EtudiantRepository etudiantRepository;
    private final SubmitRepository submitRepository;
    private final DevoirRepository devoirRepository;
    private final UserRepository userRepository;
    private final InscriptionRepository inscriptionRepository;

    public List<KPIDto> calculerTousLesKPIs() {
        List<KPIConfiguration> configurations = kpiConfigurationRepository.findAllActifsOrdered();
        List<KPIDto> kpis = new ArrayList<>();

        for (KPIConfiguration config : configurations) {
            Double valeur = calculerValeurKPI(config.getCode());
            KPIDto kpi = buildKPIDto(config, valeur);
            kpis.add(kpi);
        }

        return kpis;
    }

    private Double calculerValeurKPI(String code) {
        switch (code) {
            case "TAUX_ASSIDUITE":
                return calculerTauxAssiduité();
            case "TAUX_RETARD":
                return calculerTauxRetard();
            case "TAUX_REUSSITE":
                return calculerTauxReussite();
            case "MOYENNE_GENERALE":
                return calculerMoyenneGenerale();
            case "TAUX_COMPLETION":
                return calculerTauxCompletion();
            case "TAUX_ACTIVITE":
                return calculerTauxActivité();
            default:
                return 0.0;
        }
    }

    private Double calculerTauxAssiduité() {
        long totalInscriptions = inscriptionRepository.count();
        if (totalInscriptions == 0) return 0.0;

        long totalDevoirs = devoirRepository.count();
        if (totalDevoirs == 0) return 100.0;

        long devoirsRendus = submitRepository.findAll().stream()
                .filter(s -> s.getDateSoumission() != null)
                .count();

        long devoirsAttendus = totalInscriptions * totalDevoirs;

        return devoirsAttendus > 0 ? (devoirsRendus * 100.0) / devoirsAttendus : 0.0;
    }

    private Double calculerTauxRetard() {
        List<Submit> submitsRendus = submitRepository.findAll().stream()
                .filter(s -> s.getDateSoumission() != null)
                .toList();

        if (submitsRendus.isEmpty()) return 0.0;

        long retards = submitsRendus.stream()
                .filter(s -> s.getDevoir().getDateLimite() != null &&
                            s.getDateSoumission().isAfter(s.getDevoir().getDateLimite()))
                .count();

        return (retards * 100.0) / submitsRendus.size();
    }

    private Double calculerTauxReussite() {
        List<Submit> submitsNotes = submitRepository.findAll().stream()
                .filter(s -> s.getNote() != null)
                .toList();

        if (submitsNotes.isEmpty()) return 0.0;

        long reussis = submitsNotes.stream()
                .filter(s -> s.getNote() >= 10)
                .count();

        return (reussis * 100.0) / submitsNotes.size();
    }

    private Double calculerMoyenneGenerale() {
        return submitRepository.findAll().stream()
                .filter(s -> s.getNote() != null)
                .mapToDouble(Submit::getNote)
                .average()
                .orElse(0.0);
    }

    private Double calculerTauxCompletion() {
        long totalDevoirs = devoirRepository.count();
        if (totalDevoirs == 0) return 100.0;

        long devoirsRendus = submitRepository.findAll().stream()
                .filter(s -> s.getDateSoumission() != null)
                .count();

        return (devoirsRendus * 100.0) / totalDevoirs;
    }

    private Double calculerTauxActivité() {
        long totalEtudiants = etudiantRepository.count();
        if (totalEtudiants == 0) return 0.0;

        long etudiantsActifs = userRepository.countByStatus(UserStatus.ACTIVE);

        return (etudiantsActifs * 100.0) / totalEtudiants;
    }

    private KPIDto buildKPIDto(KPIConfiguration config, Double valeur) {
        String statut = determinerStatut(valeur, config.getSeuilAlerte(), config.getSeuilCritique());

        return KPIDto.builder()
                .id(config.getId())
                .code(config.getCode())
                .libelle(config.getLibelle())
                .description(config.getDescription())
                .valeur(valeur)
                .unite(config.getUnite())
                .statut(statut)
                .seuilAlerte(config.getSeuilAlerte())
                .seuilCritique(config.getSeuilCritique())
                .tendance("STABLE")
                .variationPourcentage(0.0)
                .dateCalcul(LocalDateTime.now())
                .build();
    }

    private String determinerStatut(Double valeur, Double seuilAlerte, Double seuilCritique) {
        if (seuilCritique != null && valeur < seuilCritique) {
            return "CRITIQUE";
        } else if (seuilAlerte != null && valeur < seuilAlerte) {
            return "ALERTE";
        }
        return "NORMAL";
    }
}
