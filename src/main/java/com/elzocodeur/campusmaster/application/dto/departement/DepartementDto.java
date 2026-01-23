package com.elzocodeur.campusmaster.application.dto.departement;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DepartementDto {
    private Long id;
    private String libelle;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
