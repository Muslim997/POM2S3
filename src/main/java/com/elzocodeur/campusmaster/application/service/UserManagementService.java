package com.elzocodeur.campusmaster.application.service;

import com.elzocodeur.campusmaster.application.dto.user.CreateUserRequest;
import com.elzocodeur.campusmaster.application.dto.user.UpdateUserRequest;
import com.elzocodeur.campusmaster.application.dto.user.UserDto;
import com.elzocodeur.campusmaster.domain.entity.Departement;
import com.elzocodeur.campusmaster.domain.entity.Etudiant;
import com.elzocodeur.campusmaster.domain.entity.Tuteur;
import com.elzocodeur.campusmaster.domain.entity.User;
import com.elzocodeur.campusmaster.domain.enums.UserRole;
import com.elzocodeur.campusmaster.domain.enums.UserStatus;
import com.elzocodeur.campusmaster.infrastructure.persistence.repository.DepartementRepository;
import com.elzocodeur.campusmaster.infrastructure.persistence.repository.EtudiantRepository;
import com.elzocodeur.campusmaster.infrastructure.persistence.repository.TuteurRepository;
import com.elzocodeur.campusmaster.infrastructure.persistence.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserManagementService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EtudiantRepository etudiantRepository;
    private final TuteurRepository tuteurRepository;
    private final DepartementRepository departementRepository;

    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public UserDto getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        return toDto(user);
    }

    public List<UserDto> searchUsers(String keyword) {
        return userRepository.findAll().stream()
                .filter(user -> user.getUsername().toLowerCase().contains(keyword.toLowerCase()) ||
                        user.getEmail().toLowerCase().contains(keyword.toLowerCase()) ||
                        (user.getFirstName() != null && user.getFirstName().toLowerCase().contains(keyword.toLowerCase())) ||
                        (user.getLastName() != null && user.getLastName().toLowerCase().contains(keyword.toLowerCase())))
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public List<UserDto> getUsersByRole(UserRole role) {
        return userRepository.findAll().stream()
                .filter(user -> user.getRole() == role)
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public List<UserDto> getUsersByStatus(UserStatus status) {
        return userRepository.findAll().stream()
                .filter(user -> user.getStatus() == status)
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public UserDto createUser(CreateUserRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Cet email est déjà utilisé");
        }

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Ce nom d'utilisateur est déjà utilisé");
        }

        // Convertir le rôle
        UserRole userRole = convertToUserRole(request.getRole());

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .phoneNumber(request.getPhoneNumber())
                .status(UserStatus.ACTIVE)
                .role(userRole)
                .build();

        user = userRepository.save(user);

        // Créer automatiquement l'entité Etudiant si le rôle est STUDENT
        if (userRole == UserRole.STUDENT) {
            String numeroEtudiant = request.getNumeroEtudiant();
            if (numeroEtudiant == null || numeroEtudiant.isBlank()) {
                numeroEtudiant = "ETU" + System.currentTimeMillis();
            }

            Etudiant etudiant = Etudiant.builder()
                    .numeroEtudiant(numeroEtudiant)
                    .user(user)
                    .build();

            if (request.getDepartementId() != null) {
                Departement departement = departementRepository.findById(request.getDepartementId())
                        .orElseThrow(() -> new RuntimeException("Département non trouvé"));
                etudiant.setDepartement(departement);
            }

            etudiantRepository.save(etudiant);
        }

        // Créer automatiquement l'entité Tuteur si le rôle est PROFESSOR
        if (userRole == UserRole.PROFESSOR) {
            Tuteur tuteur = Tuteur.builder()
                    .specialisation(request.getSpecialisation())
                    .user(user)
                    .build();

            if (request.getDepartementId() != null) {
                Departement departement = departementRepository.findById(request.getDepartementId())
                        .orElseThrow(() -> new RuntimeException("Département non trouvé"));
                tuteur.setDepartement(departement);
            }

            tuteurRepository.save(tuteur);
        }

        return toDto(user);
    }

    @Transactional
    public UserDto updateUser(Long id, UpdateUserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new RuntimeException("Cet email est déjà utilisé");
            }
            user.setEmail(request.getEmail());
        }

        if (request.getFirstName() != null) {
            user.setFirstName(request.getFirstName());
        }

        if (request.getLastName() != null) {
            user.setLastName(request.getLastName());
        }

        if (request.getPhoneNumber() != null) {
            user.setPhoneNumber(request.getPhoneNumber());
        }

        if (request.getStatus() != null) {
            user.setStatus(UserStatus.valueOf(request.getStatus()));
        }

        user = userRepository.save(user);
        return toDto(user);
    }

    @Transactional
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        userRepository.delete(user);
    }

    @Transactional
    public UserDto activateUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        user.setStatus(UserStatus.ACTIVE);
        user = userRepository.save(user);
        return toDto(user);
    }

    @Transactional
    public UserDto suspendUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        user.setStatus(UserStatus.SUSPENDED);
        user = userRepository.save(user);
        return toDto(user);
    }

    @Transactional
    public UserDto changeUserRole(Long id, UserRole newRole) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        UserRole oldRole = user.getRole();
        user.setRole(newRole);
        user = userRepository.save(user);

        // Si le nouveau rôle est STUDENT et qu'il n'a pas d'entrée Etudiant, on la crée
        if (newRole == UserRole.STUDENT && etudiantRepository.findByUserId(id).isEmpty()) {
            Etudiant etudiant = Etudiant.builder()
                    .numeroEtudiant("ETU" + System.currentTimeMillis())
                    .user(user)
                    .build();
            etudiantRepository.save(etudiant);
        }

        // Si le nouveau rôle est PROFESSOR et qu'il n'a pas d'entrée Tuteur, on la crée
        if (newRole == UserRole.PROFESSOR && tuteurRepository.findByUserId(id).isEmpty()) {
            Tuteur tuteur = Tuteur.builder()
                    .user(user)
                    .build();
            tuteurRepository.save(tuteur);
        }

        return toDto(user);
    }

    /**
     * Convertit le rôle envoyé par le client vers les valeurs valides de l'enum UserRole.
     */
    private UserRole convertToUserRole(String role) {
        if (role == null || role.isBlank()) {
            throw new RuntimeException("Le rôle est obligatoire");
        }

        String normalizedRole = role.toUpperCase().trim();

        return switch (normalizedRole) {
            case "STUDENT", "ETUDIANT" -> UserRole.STUDENT;
            case "PROFESSOR", "ENSEIGNANT", "TEACHER" -> UserRole.PROFESSOR;
            case "ADMIN", "ADMINISTRATOR" -> UserRole.ADMIN;
            case "STAFF" -> UserRole.STAFF;
            default -> throw new RuntimeException("Rôle invalide: " + role + ". Valeurs acceptées: STUDENT, PROFESSOR, ADMIN, STAFF");
        };
    }

    private UserDto toDto(User user) {
        Set<String> roles = new HashSet<>();
        if (user.getRole() != null) {
            roles.add(user.getRole().name());
        }

        return UserDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .phoneNumber(user.getPhoneNumber())
                .status(user.getStatus() != null ? user.getStatus().name() : null)
                .roles(roles)
                .createdAt(user.getCreatedAt())
                .build();
    }
}
