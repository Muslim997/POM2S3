package com.elzocodeur.campusmaster.application.service;

import com.elzocodeur.campusmaster.application.dto.tuteur.CreateTuteurRequest;
import com.elzocodeur.campusmaster.application.dto.tuteur.TuteurDto;
import com.elzocodeur.campusmaster.domain.entity.Departement;
import com.elzocodeur.campusmaster.domain.entity.Tuteur;
import com.elzocodeur.campusmaster.domain.entity.User;
import com.elzocodeur.campusmaster.domain.enums.UserRole;
import com.elzocodeur.campusmaster.infrastructure.persistence.repository.DepartementRepository;
import com.elzocodeur.campusmaster.infrastructure.persistence.repository.TuteurRepository;
import com.elzocodeur.campusmaster.infrastructure.persistence.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TuteurService {

    private final TuteurRepository tuteurRepository;
    private final UserRepository userRepository;
    private final DepartementRepository departementRepository;

    public List<TuteurDto> getAllTuteurs() {
        return tuteurRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public TuteurDto getTuteurById(Long id) {
        Tuteur tuteur = tuteurRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tuteur non trouvé"));
        return toDto(tuteur);
    }

    public TuteurDto getTuteurByUserId(Long userId) {
        Tuteur tuteur = tuteurRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Tuteur non trouvé pour cet utilisateur"));
        return toDto(tuteur);
    }

    public Optional<TuteurDto> findTuteurByUserId(Long userId) {
        return tuteurRepository.findByUserId(userId).map(this::toDto);
    }

    @Transactional
    public TuteurDto createTuteur(CreateTuteurRequest request) {
        // Vérifier si l'utilisateur existe
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        // Vérifier que l'utilisateur est bien un PROFESSOR
        if (user.getRole() != UserRole.PROFESSOR) {
            throw new RuntimeException("L'utilisateur doit avoir le rôle PROFESSOR pour devenir tuteur");
        }

        // Vérifier qu'un tuteur n'existe pas déjà pour cet utilisateur
        if (tuteurRepository.findByUserId(request.getUserId()).isPresent()) {
            throw new RuntimeException("Un tuteur existe déjà pour cet utilisateur");
        }

        Tuteur tuteur = Tuteur.builder()
                .user(user)
                .specialisation(request.getSpecialisation())
                .build();

        if (request.getDepartementId() != null) {
            Departement departement = departementRepository.findById(request.getDepartementId())
                    .orElseThrow(() -> new RuntimeException("Département non trouvé"));
            tuteur.setDepartement(departement);
        }

        tuteur = tuteurRepository.save(tuteur);
        return toDto(tuteur);
    }

    @Transactional
    public TuteurDto updateTuteur(Long id, CreateTuteurRequest request) {
        Tuteur tuteur = tuteurRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tuteur non trouvé"));

        if (request.getSpecialisation() != null) {
            tuteur.setSpecialisation(request.getSpecialisation());
        }

        if (request.getDepartementId() != null) {
            Departement departement = departementRepository.findById(request.getDepartementId())
                    .orElseThrow(() -> new RuntimeException("Département non trouvé"));
            tuteur.setDepartement(departement);
        }

        tuteur = tuteurRepository.save(tuteur);
        return toDto(tuteur);
    }

    @Transactional
    public void deleteTuteur(Long id) {
        Tuteur tuteur = tuteurRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tuteur non trouvé"));
        tuteurRepository.delete(tuteur);
    }

    private TuteurDto toDto(Tuteur tuteur) {
        return TuteurDto.builder()
                .id(tuteur.getId())
                .userId(tuteur.getUser() != null ? tuteur.getUser().getId() : null)
                .nom(tuteur.getUser() != null ? tuteur.getUser().getLastName() : null)
                .prenom(tuteur.getUser() != null ? tuteur.getUser().getFirstName() : null)
                .email(tuteur.getUser() != null ? tuteur.getUser().getEmail() : null)
                .specialisation(tuteur.getSpecialisation())
                .departementId(tuteur.getDepartement() != null ? tuteur.getDepartement().getId() : null)
                .departementNom(tuteur.getDepartement() != null ? tuteur.getDepartement().getLibelle() : null)
                .createdAt(tuteur.getCreatedAt())
                .build();
    }
}
