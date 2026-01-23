package com.elzocodeur.campusmaster.application.dto.submit;

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
public class CreateSubmitRequest {

    @NotNull(message = "Le devoir est obligatoire")
    private Long devoirId;

    @NotBlank(message = "Le fichier est obligatoire")
    @Size(max = 500, message = "L'URL du fichier ne peut pas dépasser 500 caractères")
    private String fichierUrl;
}
