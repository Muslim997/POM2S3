package com.elzocodeur.campusmaster.application.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {

    @NotBlank(message = "Le nom d'utilisateur est obligatoire")
    @Size(min = 3, max = 50, message = "Le nom d'utilisateur doit contenir entre 3 et 50 caract\u00e8res")
    private String username;

    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "L'email doit \u00eatre valide")
    private String email;

    @NotBlank(message = "Le mot de passe est obligatoire")
    @Size(min = 6, message = "Le mot de passe doit contenir au moins 6 caract\u00e8res")
    private String password;

    @NotBlank(message = "Le pr\u00e9nom est obligatoire")
    private String firstName;

    @NotBlank(message = "Le nom est obligatoire")
    private String lastName;

    private String phoneNumber;

    @NotBlank(message = "Le r\u00f4le est obligatoire")
    private String role;

    private Long departementId;

    private String numeroEtudiant;

    private String specialisation;
}
