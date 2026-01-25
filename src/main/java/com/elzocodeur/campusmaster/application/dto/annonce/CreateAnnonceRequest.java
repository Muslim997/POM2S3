package com.elzocodeur.campusmaster.application.dto.annonce;

import com.elzocodeur.campusmaster.domain.entity.Annonce;
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
public class CreateAnnonceRequest {

    @NotBlank(message = "Le titre est obligatoire")
    @Size(max = 200, message = "Le titre ne peut pas dépasser 200 caractères")
    private String titre;

    @NotBlank(message = "Le contenu est obligatoire")
    private String contenu;

    @NotNull(message = "La priorité est obligatoire")
    private Annonce.Priorite priorite;

    private Long coursId;

    // On peut utiliser soit tuteurId (ID table tuteurs) soit tuteurUserId (ID table users)
    // Le service vérifiera lequel est fourni
    private Long tuteurId;      // ID dans la table tuteurs (optionnel si tuteurUserId est fourni)
    private Long tuteurUserId;  // ID de l'utilisateur (table users) - recommandé
}
