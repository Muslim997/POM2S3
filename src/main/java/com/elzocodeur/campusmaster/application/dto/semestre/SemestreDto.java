package com.elzocodeur.campusmaster.application.dto.semestre;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SemestreDto {
    private Long id;
    private String code;
    private String libelle;
    private String anneeAcademique;
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private Boolean actif;
    private Integer nombreModules;
    private LocalDateTime createdAt;
}
