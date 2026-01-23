package com.elzocodeur.campusmaster.application.dto.etudiant;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EtudiantDto {
    private Long id;
    private String numeroEtudiant;
    private Long userId;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String status;
    private Long departementId;
    private String departementNom;
    private Boolean profilValide;
}
