package com.elzocodeur.campusmaster.application.service;

import com.elzocodeur.campusmaster.application.dto.support.CreateSupportRequest;
import com.elzocodeur.campusmaster.application.dto.support.SupportDto;
import com.elzocodeur.campusmaster.domain.entity.Cours;
import com.elzocodeur.campusmaster.domain.entity.Support;
import com.elzocodeur.campusmaster.infrastructure.persistence.repository.CoursRepository;
import com.elzocodeur.campusmaster.infrastructure.persistence.repository.SupportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SupportService {

    private final SupportRepository supportRepository;
    private final CoursRepository coursRepository;
    private final NotificationService notificationService;

    public List<SupportDto> getAllSupports() {
        return supportRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public SupportDto getSupportById(Long id) {
        Support support = supportRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Support non trouvé"));
        return toDto(support);
    }

    public List<SupportDto> getSupportsByCours(Long coursId) {
        return supportRepository.findByCoursId(coursId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public List<SupportDto> getSupportsByCoursAndType(Long coursId, String typeFichier) {
        return supportRepository.findByCoursIdAndTypeFichier(coursId, typeFichier).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public SupportDto createSupport(CreateSupportRequest request) {
        Cours cours = coursRepository.findById(request.getCoursId())
                .orElseThrow(() -> new RuntimeException("Cours non trouvé"));

        Support support = Support.builder()
                .titre(request.getTitre())
                .description(request.getDescription())
                .urlFichier(request.getUrlFichier())
                .typeFichier(request.getTypeFichier())
                .cours(cours)
                .build();

        support = supportRepository.save(support);

        // Notifier les étudiants inscrits au cours
        notificationService.notifierNouveauSupport(cours, support);

        return toDto(support);
    }

    @Transactional
    public SupportDto updateSupport(Long id, CreateSupportRequest request) {
        Support support = supportRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Support non trouvé"));

        support.setTitre(request.getTitre());
        support.setDescription(request.getDescription());
        support.setUrlFichier(request.getUrlFichier());
        support.setTypeFichier(request.getTypeFichier());

        if (request.getCoursId() != null && !request.getCoursId().equals(support.getCours().getId())) {
            Cours cours = coursRepository.findById(request.getCoursId())
                    .orElseThrow(() -> new RuntimeException("Cours non trouvé"));
            support.setCours(cours);
        }

        support = supportRepository.save(support);
        return toDto(support);
    }

    @Transactional
    public void deleteSupport(Long id) {
        Support support = supportRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Support non trouvé"));
        supportRepository.delete(support);
    }

    private SupportDto toDto(Support support) {
        return SupportDto.builder()
                .id(support.getId())
                .titre(support.getTitre())
                .description(support.getDescription())
                .urlFichier(support.getUrlFichier())
                .typeFichier(support.getTypeFichier())
                .coursId(support.getCours() != null ? support.getCours().getId() : null)
                .coursNom(support.getCours() != null ? support.getCours().getTitre() : null)
                .createdAt(support.getCreatedAt())
                .build();
    }
}
