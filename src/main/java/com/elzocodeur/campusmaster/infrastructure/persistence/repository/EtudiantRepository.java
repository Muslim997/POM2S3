package com.elzocodeur.campusmaster.infrastructure.persistence.repository;

import com.elzocodeur.campusmaster.domain.entity.Etudiant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EtudiantRepository extends JpaRepository<Etudiant, Long> {
    Optional<Etudiant> findByNumeroEtudiant(String numeroEtudiant);
    Optional<Etudiant> findByUserId(Long userId);
    boolean existsByNumeroEtudiant(String numeroEtudiant);
}
