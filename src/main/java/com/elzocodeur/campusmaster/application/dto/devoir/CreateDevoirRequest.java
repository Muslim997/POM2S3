package com.elzocodeur.campusmaster.application.dto.devoir;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateDevoirRequest {

    @NotBlank(message = "Le titre est obligatoire")
    @Size(max = 200, message = "Le titre ne peut pas dépasser 200 caractères")
    private String titre;

    private String description;

    @NotNull(message = "La date limite est obligatoire")
    private LocalDateTime dateLimite;

    @NotNull(message = "Le cours est obligatoire")
    private Long coursId;
}
