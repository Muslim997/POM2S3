package com.elzocodeur.campusmaster.application.dto.message;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateGroupeRequest {

    @NotBlank(message = "Le nom est obligatoire")
    private String nom;

    private String description;
    private Long matiereId;
    private Long coursId;

    @NotNull(message = "Le créateur est obligatoire")
    private Long createurId;

    private Set<Long> membresIds;
}
