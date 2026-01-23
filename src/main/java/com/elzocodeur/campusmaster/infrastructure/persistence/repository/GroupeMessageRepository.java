package com.elzocodeur.campusmaster.infrastructure.persistence.repository;

import com.elzocodeur.campusmaster.domain.entity.GroupeMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GroupeMessageRepository extends JpaRepository<GroupeMessage, Long> {

    List<GroupeMessage> findByMatiereId(Long matiereId);

    List<GroupeMessage> findByCoursId(Long coursId);

    List<GroupeMessage> findByCreateurId(Long createurId);

    @Query("SELECT g FROM GroupeMessage g JOIN g.membres m WHERE m.id = :userId AND g.actif = true ORDER BY g.createdAt DESC")
    List<GroupeMessage> findActiveGroupesByMembreId(@Param("userId") Long userId);

    @Query("SELECT g FROM GroupeMessage g JOIN g.membres m WHERE m.id = :userId ORDER BY g.createdAt DESC")
    List<GroupeMessage> findAllGroupesByMembreId(@Param("userId") Long userId);

    @Query("SELECT COUNT(g.membres) FROM GroupeMessage g WHERE g.id = :groupeId")
    Integer countMembresByGroupeId(@Param("groupeId") Long groupeId);

    @Query("SELECT COUNT(g.messages) FROM GroupeMessage g WHERE g.id = :groupeId")
    Integer countMessagesByGroupeId(@Param("groupeId") Long groupeId);

    @Query("SELECT g FROM GroupeMessage g WHERE g.matiere.id = :matiereId AND g.actif = true")
    List<GroupeMessage> findActiveByMatiereId(@Param("matiereId") Long matiereId);

    @Query("SELECT g FROM GroupeMessage g WHERE g.cours.id = :coursId AND g.actif = true")
    List<GroupeMessage> findActiveByCoursId(@Param("coursId") Long coursId);

    Optional<GroupeMessage> findByNom(String nom);

    @Query("SELECT CASE WHEN COUNT(gm) > 0 THEN true ELSE false END " +
            "FROM GroupeMessage g JOIN g.membres gm WHERE g.id = :groupeId AND gm.id = :userId")
    Boolean isUserMembre(@Param("groupeId") Long groupeId, @Param("userId") Long userId);
}
