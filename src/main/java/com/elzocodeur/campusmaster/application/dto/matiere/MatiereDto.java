package com.elzocodeur.campusmaster.application.dto.matiere;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MatiereDto {
    private Long id;
    private String code;
    private String libelle;
    private String description;
    private Double coefficient;
    private Integer volumeHoraire;
    private Boolean actif;
    private Long moduleId;
    private String moduleLibelle;
    private Integer nombreCours;
    private LocalDateTime createdAt;
}
