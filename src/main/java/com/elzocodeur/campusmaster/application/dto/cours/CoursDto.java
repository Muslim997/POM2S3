package com.elzocodeur.campusmaster.application.dto.cours;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CoursDto {
    private Long id;
    private String titre;
    private String description;
    private String semestre;
    private Long tuteurId;        // ID dans la table tuteurs
    private Long tuteurUserId;    // ID de l'utilisateur (table users)
    private String tuteurNom;
    private Long departementId;
    private String departementNom;
    private Integer nombreSupports;
    private Integer nombreDevoirs;
    private LocalDateTime createdAt;
}
