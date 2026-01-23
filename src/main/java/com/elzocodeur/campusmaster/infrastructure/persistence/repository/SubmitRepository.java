package com.elzocodeur.campusmaster.infrastructure.persistence.repository;

import com.elzocodeur.campusmaster.domain.entity.Submit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubmitRepository extends JpaRepository<Submit, Long> {
    List<Submit> findByDevoirId(Long devoirId);
    List<Submit> findByEtudiantId(Long etudiantId);
    Optional<Submit> findByDevoirIdAndEtudiantId(Long devoirId, Long etudiantId);
    List<Submit> findByDevoirIdAndEtudiantIdOrderByCreatedAtDesc(Long devoirId, Long etudiantId);
}
