package com.elzocodeur.campusmaster.infrastructure.persistence.repository;

import com.elzocodeur.campusmaster.domain.entity.Matiere;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MatiereRepository extends JpaRepository<Matiere, Long> {

    Optional<Matiere> findByCode(String code);

    List<Matiere> findByModuleId(Long moduleId);

    List<Matiere> findByActif(Boolean actif);

    @Query("SELECT m FROM Matiere m WHERE m.actif = true ORDER BY m.code")
    List<Matiere> findAllActives();

    boolean existsByCode(String code);
}
