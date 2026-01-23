package com.elzocodeur.campusmaster.infrastructure.persistence.repository;

import com.elzocodeur.campusmaster.domain.entity.Semestre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SemestreRepository extends JpaRepository<Semestre, Long> {

    Optional<Semestre> findByCode(String code);

    List<Semestre> findByAnneeAcademique(String anneeAcademique);

    List<Semestre> findByActif(Boolean actif);

    @Query("SELECT s FROM Semestre s WHERE s.actif = true ORDER BY s.dateDebut DESC")
    List<Semestre> findAllActifs();

    boolean existsByCode(String code);
}
