package com.elzocodeur.campusmaster.domain.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tags")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageTag extends BaseEntity {

    @Column(name = "nom", nullable = false, unique = true, length = 50)
    private String nom;

    @Column(name = "couleur", length = 7)
    private String couleur;

    @Column(name = "description", length = 255)
    private String description;
}
