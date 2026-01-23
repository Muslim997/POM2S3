package com.elzocodeur.campusmaster.application.dto.devoir;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DevoirDto {
    private Long id;
    private String titre;
    private String description;
    private LocalDateTime dateLimite;
    private Long coursId;
    private String coursNom;
    private Integer nombreSubmissions;
    private Boolean isSubmitted;
    private LocalDateTime createdAt;
}
