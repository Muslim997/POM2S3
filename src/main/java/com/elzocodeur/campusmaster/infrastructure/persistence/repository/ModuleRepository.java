package com.elzocodeur.campusmaster.infrastructure.persistence.repository;

import com.elzocodeur.campusmaster.domain.entity.Module;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ModuleRepository extends JpaRepository<Module, Long> {

    Optional<Module> findByCode(String code);

    List<Module> findBySemestreId(Long semestreId);

    List<Module> findByDepartementId(Long departementId);

    List<Module> findByActif(Boolean actif);

    @Query("SELECT m FROM Module m WHERE m.actif = true ORDER BY m.code")
    List<Module> findAllActifs();

    boolean existsByCode(String code);
}
