package com.elzocodeur.campusmaster.application.dto.inscription;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InscriptionDto {
    private Long id;
    private LocalDate dateInscription;
    private Long etudiantId;
    private String etudiantNom;
    private Long coursId;
    private String coursNom;
}
