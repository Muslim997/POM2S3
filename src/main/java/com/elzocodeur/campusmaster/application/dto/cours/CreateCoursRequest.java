package com.elzocodeur.campusmaster.application.dto.cours;

import jakarta.validation.constraints.NotBlank;
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
public class CreateCoursRequest {

    @NotBlank(message = "Le titre est obligatoire")
    @Size(max = 200, message = "Le titre ne peut pas dépasser 200 caractères")
    private String titre;

    private String description;

    @Size(max = 20, message = "Le semestre ne peut pas dépasser 20 caractères")
    private String semestre;

    // On peut utiliser soit tuteurId (ID table tuteurs) soit tuteurUserId (ID table users)
    // Le service vérifiera lequel est fourni
    private Long tuteurId;      // ID dans la table tuteurs (optionnel si tuteurUserId est fourni)
    private Long tuteurUserId;  // ID de l'utilisateur (table users) - recommandé

    private Long departementId;
}
