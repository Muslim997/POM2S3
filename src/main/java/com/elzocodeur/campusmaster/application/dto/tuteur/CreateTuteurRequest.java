package com.elzocodeur.campusmaster.application.dto.tuteur;

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
public class CreateTuteurRequest {

    @NotNull(message = "L'ID utilisateur est obligatoire")
    private Long userId;

    @Size(max = 100, message = "La spécialisation ne peut pas dépasser 100 caractères")
    private String specialisation;

    private Long departementId;
}
