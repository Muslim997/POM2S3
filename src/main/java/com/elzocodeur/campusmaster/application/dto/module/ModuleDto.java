package com.elzocodeur.campusmaster.application.dto.module;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModuleDto {
    private Long id;
    private String code;
    private String libelle;
    private String description;
    private Integer credits;
    private Boolean actif;
    private Long semestreId;
    private String semestreLibelle;
    private Long departementId;
    private String departementNom;
    private Integer nombreMatieres;
    private LocalDateTime createdAt;
}
