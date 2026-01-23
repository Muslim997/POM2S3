package com.elzocodeur.campusmaster.infrastructure.persistence.repository;

import com.elzocodeur.campusmaster.domain.entity.MessageTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface MessageTagRepository extends JpaRepository<MessageTag, Long> {

    Optional<MessageTag> findByNom(String nom);

    List<MessageTag> findByNomIn(Set<String> noms);

    @Query("SELECT t FROM MessageTag t WHERE LOWER(t.nom) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<MessageTag> searchByNom(@Param("keyword") String keyword);

    @Query("SELECT COUNT(m) FROM Message m JOIN m.tags t WHERE t.id = :tagId")
    Integer countMessagesByTagId(@Param("tagId") Long tagId);

    Boolean existsByNom(String nom);
}
