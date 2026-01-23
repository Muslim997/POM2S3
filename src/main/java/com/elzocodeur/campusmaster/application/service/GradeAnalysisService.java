package com.elzocodeur.campusmaster.application.service;

import com.elzocodeur.campusmaster.application.dto.dashboard.GrapheEvolutionNotesDto;
import com.elzocodeur.campusmaster.domain.entity.Etudiant;
import com.elzocodeur.campusmaster.domain.entity.Submit;
import com.elzocodeur.campusmaster.infrastructure.persistence.repository.EtudiantRepository;
import com.elzocodeur.campusmaster.infrastructure.persistence.repository.SubmitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GradeAnalysisService {

    private final EtudiantRepository etudiantRepository;
    private final SubmitRepository submitRepository;

    public GrapheEvolutionNotesDto getEvolutionNotes(Long etudiantId) {
        Etudiant etudiant = etudiantRepository.findById(etudiantId)
                .orElseThrow(() -> new RuntimeException("Étudiant non trouvé"));

        List<Submit> submits = submitRepository.findByEtudiantId(etudiantId).stream()
                .filter(s -> s.getNote() != null)
                .sorted(Comparator.comparing(Submit::getDateSoumission))
                .collect(Collectors.toList());

        List<GrapheEvolutionNotesDto.DataPoint> evolution = submits.stream()
                .map(submit -> GrapheEvolutionNotesDto.DataPoint.builder()
                        .date(submit.getDateSoumission() != null ?
                              submit.getDateSoumission().toLocalDate() : LocalDate.now())
                        .note(submit.getNote())
                        .matiere(submit.getDevoir().getCours().getMatiere() != null ?
                                submit.getDevoir().getCours().getMatiere().getLibelle() :
                                submit.getDevoir().getCours().getTitre())
                        .typeEvaluation(submit.getDevoir().getTitre())
                        .build())
                .collect(Collectors.toList());

        Double moyenneGlobale = submits.stream()
                .mapToDouble(Submit::getNote)
                .average()
                .orElse(0.0);

        Double moyenneMinimum = submits.stream()
                .mapToDouble(Submit::getNote)
                .min()
                .orElse(0.0);

        Double moyenneMaximum = submits.stream()
                .mapToDouble(Submit::getNote)
                .max()
                .orElse(0.0);

        String tendance = calculerTendance(submits);

        return GrapheEvolutionNotesDto.builder()
                .etudiantId(etudiantId)
                .etudiantNom(etudiant.getUser().getFirstName() + " " + etudiant.getUser().getLastName())
                .evolution(evolution)
                .moyenneGlobale(moyenneGlobale)
                .moyenneMinimum(moyenneMinimum)
                .moyenneMaximum(moyenneMaximum)
                .tendance(tendance)
                .build();
    }

    private String calculerTendance(List<Submit> submits) {
        if (submits.size() < 2) {
            return "STABLE";
        }

        int recentCount = Math.min(3, submits.size());
        List<Submit> recentSubmits = submits.subList(submits.size() - recentCount, submits.size());
        List<Submit> olderSubmits = submits.subList(0, Math.min(3, submits.size() - recentCount + 1));

        Double moyenneRecente = recentSubmits.stream()
                .mapToDouble(Submit::getNote)
                .average()
                .orElse(0.0);

        Double moyenneAncienne = olderSubmits.stream()
                .mapToDouble(Submit::getNote)
                .average()
                .orElse(0.0);

        double difference = moyenneRecente - moyenneAncienne;

        if (Math.abs(difference) < 1.0) {
            return "STABLE";
        } else if (difference > 0) {
            return "HAUSSE";
        } else {
            return "BAISSE";
        }
    }

    public List<GrapheEvolutionNotesDto> getEvolutionNotesForCours(Long coursId) {
        List<Etudiant> etudiants = etudiantRepository.findAll();

        List<GrapheEvolutionNotesDto> evolutions = new ArrayList<>();

        for (Etudiant etudiant : etudiants) {
            List<Submit> submits = submitRepository.findByEtudiantId(etudiant.getId()).stream()
                    .filter(s -> s.getDevoir().getCours().getId().equals(coursId))
                    .filter(s -> s.getNote() != null)
                    .sorted(Comparator.comparing(Submit::getDateSoumission))
                    .collect(Collectors.toList());

            if (!submits.isEmpty()) {
                GrapheEvolutionNotesDto dto = buildEvolutionDto(etudiant, submits);
                evolutions.add(dto);
            }
        }

        return evolutions;
    }

    private GrapheEvolutionNotesDto buildEvolutionDto(Etudiant etudiant, List<Submit> submits) {
        List<GrapheEvolutionNotesDto.DataPoint> evolution = submits.stream()
                .map(submit -> GrapheEvolutionNotesDto.DataPoint.builder()
                        .date(submit.getDateSoumission() != null ?
                              submit.getDateSoumission().toLocalDate() : LocalDate.now())
                        .note(submit.getNote())
                        .matiere(submit.getDevoir().getCours().getMatiere() != null ?
                                submit.getDevoir().getCours().getMatiere().getLibelle() :
                                submit.getDevoir().getCours().getTitre())
                        .typeEvaluation(submit.getDevoir().getTitre())
                        .build())
                .collect(Collectors.toList());

        Double moyenneGlobale = submits.stream()
                .mapToDouble(Submit::getNote)
                .average()
                .orElse(0.0);

        Double moyenneMinimum = submits.stream()
                .mapToDouble(Submit::getNote)
                .min()
                .orElse(0.0);

        Double moyenneMaximum = submits.stream()
                .mapToDouble(Submit::getNote)
                .max()
                .orElse(0.0);

        String tendance = calculerTendance(submits);

        return GrapheEvolutionNotesDto.builder()
                .etudiantId(etudiant.getId())
                .etudiantNom(etudiant.getUser().getFirstName() + " " + etudiant.getUser().getLastName())
                .evolution(evolution)
                .moyenneGlobale(moyenneGlobale)
                .moyenneMinimum(moyenneMinimum)
                .moyenneMaximum(moyenneMaximum)
                .tendance(tendance)
                .build();
    }
}
