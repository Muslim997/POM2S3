package com.elzocodeur.campusmaster.application.dto.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageDto {
    private Long id;
    private String contenu;
    private Boolean estLu;
    private LocalDateTime dateEnvoi;
    private String typeMessage;
    private Long expediteurId;
    private String expediteurNom;
    private Long destinataireId;
    private String destinataireNom;
    private Long conversationId;
    private Long groupeId;
    private String groupeNom;
    private Set<String> tags;
}
