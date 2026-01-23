package com.elzocodeur.campusmaster.application.service;

import com.elzocodeur.campusmaster.application.dto.stats.CoursStatsDto;
import com.elzocodeur.campusmaster.domain.entity.Cours;
import com.elzocodeur.campusmaster.domain.entity.Submit;
import com.elzocodeur.campusmaster.infrastructure.persistence.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StatsService {

    private final CoursRepository coursRepository;
    private final InscriptionRepository inscriptionRepository;
    private final SupportRepository supportRepository;
    private final DevoirRepository devoirRepository;
    private final SubmitRepository submitRepository;

    public CoursStatsDto getCoursStats(Long coursId) {
        Cours cours = coursRepository.findById(coursId)
                .orElseThrow(() -> new RuntimeException("Cours non trouvé"));

        int nombreEtudiants = inscriptionRepository.findByCoursId(coursId).size();
        int nombreSupports = supportRepository.findByCoursId(coursId).size();
        int nombreDevoirs = devoirRepository.findByCoursId(coursId).size();

        List<Submit> submits = devoirRepository.findByCoursId(coursId).stream()
                .flatMap(devoir -> submitRepository.findByDevoirId(devoir.getId()).stream())
                .collect(Collectors.toList());

        long devoirsEvalues = submits.stream()
                .filter(s -> s.getNote() != null)
                .count();

        int devoirsEnAttente = submits.size() - (int) devoirsEvalues;

        Double moyenneGenerale = submits.stream()
                .filter(s -> s.getNote() != null)
                .mapToDouble(Submit::getNote)
                .average()
                .orElse(0.0);

        int tauxRendu = nombreDevoirs > 0 ?
                (int) ((submits.size() * 100.0) / (nombreDevoirs * nombreEtudiants)) : 0;

        return CoursStatsDto.builder()
                .coursId(cours.getId())
                .coursNom(cours.getTitre())
                .nombreEtudiants(nombreEtudiants)
                .nombreSupports(nombreSupports)
                .nombreDevoirs(nombreDevoirs)
                .devoirsEnAttente(devoirsEnAttente)
                .devoirsEvalues((int) devoirsEvalues)
                .moyenneGenerale(moyenneGenerale)
                .tauxRendu(tauxRendu)
                .build();
    }

    public List<CoursStatsDto> getCoursStatsByTuteur(Long tuteurId) {
        return coursRepository.findByTuteurId(tuteurId).stream()
                .map(cours -> getCoursStats(cours.getId()))
                .collect(Collectors.toList());
    }
}
