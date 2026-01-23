package com.elzocodeur.campusmaster.infrastructure.persistence.repository;

import com.elzocodeur.campusmaster.domain.entity.NotificationPush;
import com.elzocodeur.campusmaster.domain.enums.NotificationChannel;
import com.elzocodeur.campusmaster.domain.enums.NotificationEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NotificationPushRepository extends JpaRepository<NotificationPush, Long> {

    List<NotificationPush> findByUserIdOrderByCreatedAtDesc(Long userId);

    List<NotificationPush> findByUserIdAndSentFalseOrderByCreatedAtDesc(Long userId);

    @Query("SELECT n FROM NotificationPush n WHERE n.sent = false AND " +
            "(n.scheduledAt IS NULL OR n.scheduledAt <= :now) " +
            "ORDER BY n.priority DESC, n.createdAt ASC")
    List<NotificationPush> findPendingNotifications(@Param("now") LocalDateTime now);

    List<NotificationPush> findByEventTypeAndSentTrue(NotificationEvent eventType);

    List<NotificationPush> findByChannelAndSentFalse(NotificationChannel channel);

    @Query("SELECT n FROM NotificationPush n WHERE n.user.id = :userId AND n.sent = true " +
            "AND n.sentAt >= :since ORDER BY n.sentAt DESC")
    List<NotificationPush> findRecentNotifications(
            @Param("userId") Long userId,
            @Param("since") LocalDateTime since);

    @Query("SELECT COUNT(n) FROM NotificationPush n WHERE n.user.id = :userId AND n.sent = false")
    Integer countPendingByUserId(@Param("userId") Long userId);

    @Query("SELECT n FROM NotificationPush n WHERE n.sent = false AND n.retryCount < :maxRetries " +
            "AND n.errorMessage IS NOT NULL")
    List<NotificationPush> findFailedNotificationsForRetry(@Param("maxRetries") Integer maxRetries);

    void deleteByUserIdAndSentTrueAndSentAtBefore(Long userId, LocalDateTime before);

    @Query("SELECT n FROM NotificationPush n WHERE n.entityType = :entityType " +
            "AND n.entityId = :entityId ORDER BY n.createdAt DESC")
    List<NotificationPush> findByEntity(
            @Param("entityType") String entityType,
            @Param("entityId") Long entityId);
}
