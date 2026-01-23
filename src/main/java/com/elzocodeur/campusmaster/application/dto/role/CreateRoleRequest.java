package com.elzocodeur.campusmaster.application.dto.role;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateRoleRequest {

    @NotBlank(message = "Le libellé est obligatoire")
    @Size(max = 50)
    private String libelle;

    @Size(max = 255)
    private String description;
}
