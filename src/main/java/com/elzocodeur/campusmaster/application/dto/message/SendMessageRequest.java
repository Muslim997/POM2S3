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
public class SendMessageRequest {

    @NotBlank(message = "Le contenu est obligatoire")
    private String contenu;

    @NotNull(message = "L'expéditeur est obligatoire")
    private Long expediteurId;

    private Long destinataireId;
    private Long groupeId;
    private Set<String> tags;
}
