package com.elzocodeur.campusmaster.application.dto.semestre;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateSemestreRequest {

    @NotBlank(message = "Le code est obligatoire")
    @Size(max = 20)
    private String code;

    @NotBlank(message = "Le libellé est obligatoire")
    @Size(max = 100)
    private String libelle;

    @NotBlank(message = "L'année académique est obligatoire")
    @Size(max = 20)
    private String anneeAcademique;

    private LocalDate dateDebut;

    private LocalDate dateFin;
}
