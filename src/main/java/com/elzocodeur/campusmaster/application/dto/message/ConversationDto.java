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
public class ConversationDto {
    private Long id;
    private Long participant1Id;
    private String participant1Nom;
    private Long participant2Id;
    private String participant2Nom;
    private LocalDateTime dernierMessageDate;
    private String dernierMessageContenu;
    private Integer nombreMessagesNonLus;
    private Boolean archivee;
}
