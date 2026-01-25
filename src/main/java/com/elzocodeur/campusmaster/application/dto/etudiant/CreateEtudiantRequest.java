package com.elzocodeur.campusmaster.application.dto.etudiant;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateEtudiantRequest {

    @NotNull(message = "L'ID utilisateur est obligatoire")
    private Long userId;

    @Size(max = 50, message = "Le numéro étudiant ne peut pas dépasser 50 caractères")
    private String numeroEtudiant;

    private Long departementId;
}
