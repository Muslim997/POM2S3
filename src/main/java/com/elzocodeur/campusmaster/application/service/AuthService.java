package com.elzocodeur.campusmaster.application.service;

import com.elzocodeur.campusmaster.application.dto.auth.AuthResponse;
import com.elzocodeur.campusmaster.application.dto.auth.LoginRequest;
import com.elzocodeur.campusmaster.application.dto.auth.RefreshTokenRequest;
import com.elzocodeur.campusmaster.application.dto.auth.RegisterRequest;
import com.elzocodeur.campusmaster.domain.entity.*;
import com.elzocodeur.campusmaster.domain.enums.UserRole;
import com.elzocodeur.campusmaster.domain.enums.UserStatus;
import com.elzocodeur.campusmaster.infrastructure.persistence.repository.*;
import com.elzocodeur.campusmaster.infrastructure.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final EtudiantRepository etudiantRepository;
    private final TuteurRepository tuteurRepository;
    private final DepartementRepository departementRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email déjà utilisé");
        }

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Nom d'utilisateur déjà utilisé");
        }

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .phoneNumber(request.getPhoneNumber())
                .role(UserRole.valueOf(request.getRole().toUpperCase()))
                .status(UserStatus.ACTIVE)
                .emailVerified(false)
                .build();

        user = userRepository.save(user);

        if ("ETUDIANT".equalsIgnoreCase(request.getRole()) && request.getNumeroEtudiant() != null) {
            Etudiant etudiant = Etudiant.builder()
                    .numeroEtudiant(request.getNumeroEtudiant())
                    .user(user)
                    .build();

            if (request.getDepartementId() != null) {
                Departement departement = departementRepository.findById(request.getDepartementId())
                        .orElseThrow(() -> new RuntimeException("Département non trouvé"));
                etudiant.setDepartement(departement);
            }

            etudiantRepository.save(etudiant);
        }

        if ("ENSEIGNANT".equalsIgnoreCase(request.getRole())) {
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

        String token = jwtService.generateToken(user.getEmail());
        String refreshToken = jwtService.generateRefreshToken(user.getEmail());

        return new AuthResponse(
                token,
                refreshToken,
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole().name(),
                user.getFirstName(),
                user.getLastName()
        );
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Email ou mot de passe incorrect"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Email ou mot de passe incorrect");
        }

        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new RuntimeException("Compte non actif");
        }

        String token = jwtService.generateToken(user.getEmail());
        String refreshToken = jwtService.generateRefreshToken(user.getEmail());

        return new AuthResponse(
                token,
                refreshToken,
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole().name(),
                user.getFirstName(),
                user.getLastName()
        );
    }

    public AuthResponse refreshToken(RefreshTokenRequest request) {
        String refreshToken = request.getRefreshToken();
        String userEmail = jwtService.extractUsername(refreshToken);

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        if (!jwtService.isTokenValid(refreshToken, user.getEmail())) {
            throw new RuntimeException("Token invalide");
        }

        String newToken = jwtService.generateToken(user.getEmail());
        String newRefreshToken = jwtService.generateRefreshToken(user.getEmail());

        return new AuthResponse(
                newToken,
                newRefreshToken,
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole().name(),
                user.getFirstName(),
                user.getLastName()
        );
    }
}
