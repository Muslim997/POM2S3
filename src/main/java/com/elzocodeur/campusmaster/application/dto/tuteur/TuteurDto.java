package com.elzocodeur.campusmaster.application.dto.tuteur;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TuteurDto {
    private Long id;
    private Long userId;
    private String nom;
    private String prenom;
    private String email;
    private String specialisation;
    private Long departementId;
    private String departementNom;
    private LocalDateTime createdAt;
}
