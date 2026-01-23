package com.elzocodeur.campusmaster.application.dto.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GroupeMessageDto {
    private Long id;
    private String nom;
    private String description;
    private Long matiereId;
    private String matiereNom;
    private Long coursId;
    private String coursNom;
    private Long createurId;
    private String createurNom;
    private Integer nombreMembres;
    private Integer nombreMessages;
    private Boolean actif;
    private LocalDateTime createdAt;
}
