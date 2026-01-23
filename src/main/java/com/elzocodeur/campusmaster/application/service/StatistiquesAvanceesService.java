package com.elzocodeur.campusmaster.application.service;

import com.elzocodeur.campusmaster.application.dto.stats.StatistiquesAvanceesDto;
import com.elzocodeur.campusmaster.domain.entity.Devoir;
import com.elzocodeur.campusmaster.domain.entity.Matiere;
import com.elzocodeur.campusmaster.domain.entity.Submit;
import com.elzocodeur.campusmaster.domain.enums.UserRole;
import com.elzocodeur.campusmaster.domain.enums.UserStatus;
import com.elzocodeur.campusmaster.infrastructure.persistence.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StatistiquesAvanceesService {

    private final UserRepository userRepository;
    private final EtudiantRepository etudiantRepository;
    private final TuteurRepository tuteurRepository;
    private final CoursRepository coursRepository;
    private final ModuleRepository moduleRepository;
    private final DevoirRepository devoirRepository;
    private final SubmitRepository submitRepository;
    private final MatiereRepository matiereRepository;

    public StatistiquesAvanceesDto getStatistiquesGlobales() {
        // Statistiques sur les utilisateurs
        long nombreEtudiants = etudiantRepository.count();
        long nombreEtudiantsActifs = userRepository.countByStatus(UserStatus.ACTIVE);
        long nombreEtudiantsInactifs = nombreEtudiants - nombreEtudiantsActifs;
        long nombreEnseignants = tuteurRepository.count();
        long nombreCours = coursRepository.count();
        long nombreModules = moduleRepository.count();

        // Statistiques sur les devoirs
        List<Devoir> tousLesDevoirs = devoirRepository.findAll();
        int nombreDevoirsTotal = tousLesDevoirs.size();

        List<Submit> tousLesSubmits = submitRepository.findAll();
        long nombreDevoirsRendus = tousLesSubmits.stream()
                .filter(s -> s.getDateSoumission() != null)
                .count();

        long nombreDevoirsEnAttente = tousLesSubmits.stream()
                .filter(s -> s.getNote() == null)
                .count();

        // Calcul du taux de remise des devoirs
        Double tauxRemiseDevoirs = nombreDevoirsTotal > 0 ?
                (nombreDevoirsRendus * 100.0) / nombreDevoirsTotal : 0.0;

        // Calcul de la moyenne générale
        Double moyenneGenerale = tousLesSubmits.stream()
                .filter(s -> s.getNote() != null)
                .mapToDouble(Submit::getNote)
                .average()
                .orElse(0.0);

        // Performance par matière
        Map<String, Double> performanceParMatiere = calculerPerformanceParMatiere();
        Map<String, Integer> nombreEtudiantsParMatiere = calculerNombreEtudiantsParMatiere();

        // Taux de réussite (notes >= 10)
        long nombreEtudiantsReussis = tousLesSubmits.stream()
                .filter(s -> s.getNote() != null && s.getNote() >= 10)
                .map(s -> s.getEtudiant().getId())
                .distinct()
                .count();

        long nombreEtudiantsEchoues = nombreEtudiants - nombreEtudiantsReussis;

        Double tauxReussite = nombreEtudiants > 0 ?
                (nombreEtudiantsReussis * 100.0) / nombreEtudiants : 0.0;

        return StatistiquesAvanceesDto.builder()
                .nombreEtudiants((int) nombreEtudiants)
                .nombreEtudiantsActifs((int) nombreEtudiantsActifs)
                .nombreEtudiantsInactifs((int) nombreEtudiantsInactifs)
                .nombreEnseignants((int) nombreEnseignants)
                .nombreCours((int) nombreCours)
                .nombreModules((int) nombreModules)
                .nombreDevoirsTotal(nombreDevoirsTotal)
                .nombreDevoirsRendus((int) nombreDevoirsRendus)
                .nombreDevoirsEnAttente((int) nombreDevoirsEnAttente)
                .tauxRemiseDevoirs(tauxRemiseDevoirs)
                .moyenneGenerale(moyenneGenerale)
                .performanceParMatiere(performanceParMatiere)
                .nombreEtudiantsParMatiere(nombreEtudiantsParMatiere)
                .tauxReussite(tauxReussite)
                .nombreEtudiantsReussis((int) nombreEtudiantsReussis)
                .nombreEtudiantsEchoues((int) nombreEtudiantsEchoues)
                .build();
    }

    private Map<String, Double> calculerPerformanceParMatiere() {
        Map<String, Double> performanceParMatiere = new HashMap<>();
        List<Matiere> matieres = matiereRepository.findAll();

        for (Matiere matiere : matieres) {
            List<Submit> submitsMatiere = matiere.getCours().stream()
                    .flatMap(cours -> cours.getDevoirs().stream())
                    .flatMap(devoir -> submitRepository.findByDevoirId(devoir.getId()).stream())
                    .collect(Collectors.toList());

            Double moyenne = submitsMatiere.stream()
                    .filter(s -> s.getNote() != null)
                    .mapToDouble(Submit::getNote)
                    .average()
                    .orElse(0.0);

            performanceParMatiere.put(matiere.getLibelle(), moyenne);
        }

        return performanceParMatiere;
    }

    private Map<String, Integer> calculerNombreEtudiantsParMatiere() {
        Map<String, Integer> nombreEtudiantsParMatiere = new HashMap<>();
        List<Matiere> matieres = matiereRepository.findAll();

        for (Matiere matiere : matieres) {
            long nombreEtudiants = matiere.getCours().stream()
                    .flatMap(cours -> cours.getInscriptions().stream())
                    .map(inscription -> inscription.getEtudiant().getId())
                    .distinct()
                    .count();

            nombreEtudiantsParMatiere.put(matiere.getLibelle(), (int) nombreEtudiants);
        }

        return nombreEtudiantsParMatiere;
    }

    public Map<String, Object> getStatistiquesByPeriode(String anneeAcademique) {
        Map<String, Object> stats = new HashMap<>();

        // Statistiques filtrées par année académique
        // Implémentation simplifiée - peut être étendue selon les besoins

        stats.put("anneeAcademique", anneeAcademique);
        stats.put("statistiquesGlobales", getStatistiquesGlobales());

        return stats;
    }

    public Map<String, Object> getStatistiquesByDepartement(Long departementId) {
        Map<String, Object> stats = new HashMap<>();

        List<Submit> submitsDepartement = coursRepository.findByDepartementId(departementId).stream()
                .flatMap(cours -> cours.getDevoirs().stream())
                .flatMap(devoir -> submitRepository.findByDevoirId(devoir.getId()).stream())
                .collect(Collectors.toList());

        Double moyenneDepartement = submitsDepartement.stream()
                .filter(s -> s.getNote() != null)
                .mapToDouble(Submit::getNote)
                .average()
                .orElse(0.0);

        long nombreEtudiantsDepartement = coursRepository.findByDepartementId(departementId).stream()
                .flatMap(cours -> cours.getInscriptions().stream())
                .map(inscription -> inscription.getEtudiant().getId())
                .distinct()
                .count();

        stats.put("departementId", departementId);
        stats.put("moyenneDepartement", moyenneDepartement);
        stats.put("nombreEtudiants", nombreEtudiantsDepartement);
        stats.put("nombreCours", coursRepository.findByDepartementId(departementId).size());

        return stats;
    }
}
