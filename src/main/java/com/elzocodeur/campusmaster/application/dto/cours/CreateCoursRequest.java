package com.elzocodeur.campusmaster.application.dto.cours;

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
public class CreateCoursRequest {

    @NotBlank(message = "Le titre est obligatoire")
    @Size(max = 200, message = "Le titre ne peut pas dépasser 200 caractères")
    private String titre;

    private String description;

    @Size(max = 20, message = "Le semestre ne peut pas dépasser 20 caractères")
    private String semestre;

    @NotNull(message = "Le tuteur est obligatoire")
    private Long tuteurId;

    private Long departementId;
}
