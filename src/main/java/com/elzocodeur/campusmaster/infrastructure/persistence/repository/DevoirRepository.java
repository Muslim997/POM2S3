package com.elzocodeur.campusmaster.infrastructure.persistence.repository;

import com.elzocodeur.campusmaster.domain.entity.Devoir;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface DevoirRepository extends JpaRepository<Devoir, Long> {
    List<Devoir> findByCoursId(Long coursId);
    List<Devoir> findByCoursIdAndDateLimiteAfter(Long coursId, LocalDateTime date);
    List<Devoir> findByDateLimiteBetween(LocalDateTime start, LocalDateTime end);
}
