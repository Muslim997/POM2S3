package com.elzocodeur.campusmaster.application.dto.inscription;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InscriptionDto {
    private Long id;
    private LocalDate dateInscription;
    private Long etudiantId;        // ID dans la table etudiants
    private Long etudiantUserId;    // ID de l'utilisateur (table users)
    private String etudiantNom;
    private Long coursId;
    private String coursNom;
}
