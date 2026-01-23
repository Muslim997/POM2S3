package com.elzocodeur.campusmaster.infrastructure.persistence.repository;

import com.elzocodeur.campusmaster.domain.entity.Annonce;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AnnonceRepository extends JpaRepository<Annonce, Long> {
    List<Annonce> findByCoursIdOrderByDatePublicationDesc(Long coursId);
    List<Annonce> findByTuteurIdOrderByDatePublicationDesc(Long tuteurId);
    List<Annonce> findByCoursIsNullOrderByDatePublicationDesc();
    List<Annonce> findByDatePublicationBetweenOrderByDatePublicationDesc(LocalDateTime start, LocalDateTime end);

    @Query("SELECT a FROM Annonce a WHERE a.priorite = 'URGENTE' OR a.priorite = 'HAUTE' ORDER BY a.datePublication DESC")
    List<Annonce> findAnnoncesImportantes();
}
