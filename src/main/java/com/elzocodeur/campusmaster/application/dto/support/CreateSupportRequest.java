package com.elzocodeur.campusmaster.application.dto.support;

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
public class CreateSupportRequest {

    @NotBlank(message = "Le titre est obligatoire")
    @Size(max = 200, message = "Le titre ne peut pas dépasser 200 caractères")
    private String titre;

    private String description;

    @NotBlank(message = "L'URL du fichier est obligatoire")
    @Size(max = 500, message = "L'URL ne peut pas dépasser 500 caractères")
    private String urlFichier;

    @Size(max = 50, message = "Le type de fichier ne peut pas dépasser 50 caractères")
    private String typeFichier;

    @NotNull(message = "Le cours est obligatoire")
    private Long coursId;
}
