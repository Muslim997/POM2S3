package com.elzocodeur.campusmaster.application.dto.support;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SupportDto {
    private Long id;
    private String titre;
    private String description;
    private String urlFichier;
    private String typeFichier;
    private Long coursId;
    private String coursNom;
    private Long tailleFichier;
    private LocalDateTime createdAt;
}
