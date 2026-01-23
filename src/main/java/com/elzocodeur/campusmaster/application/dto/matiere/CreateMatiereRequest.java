package com.elzocodeur.campusmaster.application.dto.matiere;

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
public class CreateMatiereRequest {

    @NotBlank(message = "Le code est obligatoire")
    @Size(max = 20)
    private String code;

    @NotBlank(message = "Le libellé est obligatoire")
    @Size(max = 200)
    private String libelle;

    private String description;

    private Double coefficient;

    private Integer volumeHoraire;

    @NotNull(message = "Le module est obligatoire")
    private Long moduleId;
}
