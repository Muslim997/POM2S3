package com.elzocodeur.campusmaster.domain.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "departements")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Departement extends BaseEntity {

    @Column(name = "libelle", nullable = false, length = 100)
    private String libelle;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
}
