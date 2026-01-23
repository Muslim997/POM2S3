package com.elzocodeur.campusmaster.infrastructure.persistence.repository;

import com.elzocodeur.campusmaster.domain.entity.KPIConfiguration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface KPIConfigurationRepository extends JpaRepository<KPIConfiguration, Long> {

    Optional<KPIConfiguration> findByCode(String code);

    List<KPIConfiguration> findByActif(Boolean actif);

    @Query("SELECT k FROM KPIConfiguration k WHERE k.actif = true ORDER BY k.ordreAffichage")
    List<KPIConfiguration> findAllActifsOrdered();

    boolean existsByCode(String code);
}
