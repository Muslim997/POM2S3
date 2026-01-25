package com.elzocodeur.campusmaster.application.dto.submit;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubmitDto {
    private Long id;
    private LocalDateTime dateSoumission;
    private Double note;
    private String feedback;
    private String fichierUrl;
    private Long devoirId;
    private String devoirTitre;
    private Long etudiantId;        // ID dans la table etudiants
    private Long etudiantUserId;    // ID de l'utilisateur (table users)
    private String etudiantNom;
    private Integer version;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
