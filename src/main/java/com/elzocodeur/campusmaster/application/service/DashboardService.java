package com.elzocodeur.campusmaster.application.service;

import com.elzocodeur.campusmaster.application.dto.dashboard.DashboardSummaryDto;
import com.elzocodeur.campusmaster.application.dto.dashboard.KPIDto;
import com.elzocodeur.campusmaster.application.dto.dashboard.StatsDepartementDto;
import com.elzocodeur.campusmaster.domain.entity.Departement;
import com.elzocodeur.campusmaster.domain.entity.Matiere;
import com.elzocodeur.campusmaster.domain.entity.Submit;
import com.elzocodeur.campusmaster.domain.enums.UserStatus;
import com.elzocodeur.campusmaster.infrastructure.persistence.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardService {

    private final EtudiantRepository etudiantRepository;
    private final TuteurRepository tuteurRepository;
    private final CoursRepository coursRepository;
    private final SubmitRepository submitRepository;
    private final DevoirRepository devoirRepository;
    private final UserRepository userRepository;
    private final DepartementRepository departementRepository;
    private final MatiereRepository matiereRepository;
    private final ActivityTrackingService activityTrackingService;
    private final KPIService kpiService;

    public DashboardSummaryDto getDashboardSummary() {
        int nombreTotalEtudiants = (int) etudiantRepository.count();
        int nombreEtudiantsActifs = (int) userRepository.countByStatus(UserStatus.ACTIVE);
        int nombreEnseignants = (int) tuteurRepository.count();
        int nombreCours = (int) coursRepository.count();

        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(6);
        Long activitesRecentes = activityTrackingService.getWeeklyActivityStats().getTotalActivities();
        Long pagesVuesRecentes = activityTrackingService.getWeeklyActivityStats().getTotalPageViews();
        Long telechargements = activityTrackingService.getWeeklyActivityStats().getTotalDownloads();

        List<Submit> submits = submitRepository.findAll();
        Double moyenneGenerale = submits.stream()
                .filter(s -> s.getNote() != null)
                .mapToDouble(Submit::getNote)
                .average()
                .orElse(0.0);

        long totalNotes = submits.stream().filter(s -> s.getNote() != null).count();
        long notesReussies = submits.stream()
                .filter(s -> s.getNote() != null && s.getNote() >= 10)
                .count();
        Double tauxReussite = totalNotes > 0 ? (notesReussies * 100.0) / totalNotes : 0.0;

        long totalDevoirs = devoirRepository.count();
        long devoirsRendus = submits.stream()
                .filter(s -> s.getDateSoumission() != null)
                .count();
        Double tauxAssiduité = totalDevoirs > 0 ? (devoirsRendus * 100.0) / totalDevoirs : 0.0;

        List<KPIDto> kpisPrincipaux = kpiService.calculerTousLesKPIs().stream()
                .limit(6)
                .collect(Collectors.toList());

        List<DashboardSummaryDto.AlertDto> alertes = genererAlertes(kpisPrincipaux);

        return DashboardSummaryDto.builder()
                .nombreTotalEtudiants(nombreTotalEtudiants)
                .nombreEtudiantsActifs(nombreEtudiantsActifs)
                .nombreEnseignants(nombreEnseignants)
                .nombreCours(nombreCours)
                .activitesRecentes(activitesRecentes)
                .pagesVuesRecentes(pagesVuesRecentes)
                .telechargements(telechargements)
                .moyenneGenerale(moyenneGenerale)
                .tauxReussite(tauxReussite)
                .tauxAssiduité(tauxAssiduité)
                .kpisPrincipaux(kpisPrincipaux)
                .nombreAlertes(alertes.size())
                .alertes(alertes)
                .build();
    }

    private List<DashboardSummaryDto.AlertDto> genererAlertes(List<KPIDto> kpis) {
        List<DashboardSummaryDto.AlertDto> alertes = new ArrayList<>();

        for (KPIDto kpi : kpis) {
            if ("CRITIQUE".equals(kpi.getStatut())) {
                alertes.add(DashboardSummaryDto.AlertDto.builder()
                        .type(kpi.getCode())
                        .message(kpi.getLibelle() + " est en état critique: " + kpi.getValeur() + kpi.getUnite())
                        .severite("CRITICAL")
                        .action("Intervention immédiate requise")
                        .build());
            } else if ("ALERTE".equals(kpi.getStatut())) {
                alertes.add(DashboardSummaryDto.AlertDto.builder()
                        .type(kpi.getCode())
                        .message(kpi.getLibelle() + " nécessite attention: " + kpi.getValeur() + kpi.getUnite())
                        .severite("WARNING")
                        .action("Surveillance recommandée")
                        .build());
            }
        }

        return alertes;
    }

    public StatsDepartementDto getStatsDepartement(Long departementId) {
        Departement departement = departementRepository.findById(departementId)
                .orElseThrow(() -> new RuntimeException("Département non trouvé"));

        int nombreEtudiants = (int) coursRepository.findByDepartementId(departementId).stream()
                .flatMap(cours -> cours.getInscriptions().stream())
                .map(inscription -> inscription.getEtudiant().getId())
                .distinct()
                .count();

        int nombreEnseignants = (int) coursRepository.findByDepartementId(departementId).stream()
                .map(cours -> cours.getTuteur().getId())
                .distinct()
                .count();

        int nombreCours = coursRepository.findByDepartementId(departementId).size();

        List<Submit> submits = coursRepository.findByDepartementId(departementId).stream()
                .flatMap(cours -> cours.getDevoirs().stream())
                .flatMap(devoir -> submitRepository.findByDevoirId(devoir.getId()).stream())
                .collect(Collectors.toList());

        Double moyenneGenerale = submits.stream()
                .filter(s -> s.getNote() != null)
                .mapToDouble(Submit::getNote)
                .average()
                .orElse(0.0);

        long totalNotes = submits.stream().filter(s -> s.getNote() != null).count();
        long notesReussies = submits.stream()
                .filter(s -> s.getNote() != null && s.getNote() >= 10)
                .count();
        Double tauxReussite = totalNotes > 0 ? (notesReussies * 100.0) / totalNotes : 0.0;

        long devoirsRendus = submits.stream()
                .filter(s -> s.getDateSoumission() != null)
                .count();
        long totalDevoirs = coursRepository.findByDepartementId(departementId).stream()
                .flatMap(cours -> cours.getDevoirs().stream())
                .count();
        Double tauxAssiduité = totalDevoirs > 0 ? (devoirsRendus * 100.0) / totalDevoirs : 0.0;

        Map<String, StatsDepartementDto.MatiereStatsDto> statsParMatiere = calculerStatsParMatiere(departementId);

        int nombreModules = (int) matiereRepository.findAll().stream()
                .filter(m -> m.getCours().stream()
                        .anyMatch(c -> c.getDepartement() != null && c.getDepartement().getId().equals(departementId)))
                .map(Matiere::getModule)
                .filter(mod -> mod != null)
                .map(mod -> mod.getId())
                .distinct()
                .count();

        return StatsDepartementDto.builder()
                .departementId(departementId)
                .departementNom(departement.getLibelle())
                .nombreEtudiants(nombreEtudiants)
                .nombreEnseignants(nombreEnseignants)
                .nombreCours(nombreCours)
                .nombreModules(nombreModules)
                .moyenneGenerale(moyenneGenerale)
                .tauxReussite(tauxReussite)
                .tauxAssiduité(tauxAssiduité)
                .statsParMatiere(statsParMatiere)
                .build();
    }

    private Map<String, StatsDepartementDto.MatiereStatsDto> calculerStatsParMatiere(Long departementId) {
        Map<String, StatsDepartementDto.MatiereStatsDto> stats = new HashMap<>();

        List<Matiere> matieres = matiereRepository.findAll().stream()
                .filter(m -> m.getCours().stream()
                        .anyMatch(c -> c.getDepartement() != null && c.getDepartement().getId().equals(departementId)))
                .collect(Collectors.toList());

        for (Matiere matiere : matieres) {
            List<Submit> submitsMatiere = matiere.getCours().stream()
                    .flatMap(cours -> cours.getDevoirs().stream())
                    .flatMap(devoir -> submitRepository.findByDevoirId(devoir.getId()).stream())
                    .collect(Collectors.toList());

            int nombreEtudiants = (int) matiere.getCours().stream()
                    .flatMap(cours -> cours.getInscriptions().stream())
                    .map(inscription -> inscription.getEtudiant().getId())
                    .distinct()
                    .count();

            Double moyenneMatiere = submitsMatiere.stream()
                    .filter(s -> s.getNote() != null)
                    .mapToDouble(Submit::getNote)
                    .average()
                    .orElse(0.0);

            long totalNotes = submitsMatiere.stream().filter(s -> s.getNote() != null).count();
            long notesReussies = submitsMatiere.stream()
                    .filter(s -> s.getNote() != null && s.getNote() >= 10)
                    .count();
            Double tauxReussite = totalNotes > 0 ? (notesReussies * 100.0) / totalNotes : 0.0;

            int nombreDevoirs = (int) matiere.getCours().stream()
                    .flatMap(cours -> cours.getDevoirs().stream())
                    .count();

            int devoirsRendus = (int) submitsMatiere.stream()
                    .filter(s -> s.getDateSoumission() != null)
                    .count();

            StatsDepartementDto.MatiereStatsDto matiereStats = StatsDepartementDto.MatiereStatsDto.builder()
                    .matiereNom(matiere.getLibelle())
                    .nombreEtudiants(nombreEtudiants)
                    .moyenneMatiere(moyenneMatiere)
                    .tauxReussite(tauxReussite)
                    .nombreDevoirs(nombreDevoirs)
                    .devoirsRendus(devoirsRendus)
                    .build();

            stats.put(matiere.getLibelle(), matiereStats);
        }

        return stats;
    }
}
