package com.elzocodeur.campusmaster.application.dto.module;

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
public class CreateModuleRequest {

    @NotBlank(message = "Le code est obligatoire")
    @Size(max = 20)
    private String code;

    @NotBlank(message = "Le libellé est obligatoire")
    @Size(max = 200)
    private String libelle;

    private String description;

    private Integer credits;

    @NotNull(message = "Le semestre est obligatoire")
    private Long semestreId;

    private Long departementId;
}
