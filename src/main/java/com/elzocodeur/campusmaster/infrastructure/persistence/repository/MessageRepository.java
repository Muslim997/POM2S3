package com.elzocodeur.campusmaster.infrastructure.persistence.repository;

import com.elzocodeur.campusmaster.domain.entity.Message;
import com.elzocodeur.campusmaster.domain.entity.MessageTag;
import com.elzocodeur.campusmaster.domain.enums.MessageType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    List<Message> findByConversationIdOrderByDateEnvoiDesc(Long conversationId);

    List<Message> findByGroupeIdOrderByDateEnvoiDesc(Long groupeId);

    List<Message> findByDestinataireIdAndEstLuFalse(Long destinataireId);

    List<Message> findByExpediteurIdOrderByDateEnvoiDesc(Long expediteurId);

    List<Message> findByTypeMessage(MessageType typeMessage);

    @Query("SELECT m FROM Message m JOIN m.tags t WHERE t.nom = :tagNom ORDER BY m.dateEnvoi DESC")
    List<Message> findByTagNom(@Param("tagNom") String tagNom);

    @Query("SELECT m FROM Message m JOIN m.tags t WHERE t IN :tags ORDER BY m.dateEnvoi DESC")
    List<Message> findByTags(@Param("tags") List<MessageTag> tags);

    @Query("SELECT COUNT(m) FROM Message m WHERE m.destinataire.id = :userId AND m.estLu = false")
    Integer countMessagesNonLus(@Param("userId") Long userId);

    @Query("SELECT COUNT(m) FROM Message m WHERE m.conversation.id = :conversationId AND m.destinataire.id = :userId AND m.estLu = false")
    Integer countMessagesNonLusByConversation(@Param("conversationId") Long conversationId, @Param("userId") Long userId);

    @Query("SELECT COUNT(m) FROM Message m WHERE m.groupe.id = :groupeId AND m.estLu = false")
    Integer countMessagesNonLusByGroupe(@Param("groupeId") Long groupeId);

    List<Message> findByDateEnvoiBetween(LocalDateTime start, LocalDateTime end);

    @Query("SELECT m FROM Message m WHERE (m.expediteur.id = :userId OR m.destinataire.id = :userId) AND m.typeMessage = 'PRIVE' ORDER BY m.dateEnvoi DESC")
    List<Message> findMessagesPrivesByUser(@Param("userId") Long userId);

    @Query("SELECT m FROM Message m JOIN m.groupe g JOIN g.membres mem WHERE mem.id = :userId AND m.typeMessage = 'GROUPE' ORDER BY m.dateEnvoi DESC")
    List<Message> findMessagesGroupeByUser(@Param("userId") Long userId);
}
