package com.elzocodeur.campusmaster.application.dto.annonce;

import com.elzocodeur.campusmaster.domain.entity.Annonce;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnnonceDto {
    private Long id;
    private String titre;
    private String contenu;
    private LocalDateTime datePublication;
    private Annonce.Priorite priorite;
    private Long coursId;
    private String coursNom;
    private Long tuteurId;
    private String tuteurNom;
    private LocalDateTime createdAt;
}
