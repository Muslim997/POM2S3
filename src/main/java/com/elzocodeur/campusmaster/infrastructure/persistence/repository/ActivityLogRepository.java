package com.elzocodeur.campusmaster.infrastructure.persistence.repository;

import com.elzocodeur.campusmaster.domain.entity.ActivityLog;
import com.elzocodeur.campusmaster.domain.enums.ActivityType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ActivityLogRepository extends JpaRepository<ActivityLog, Long> {

    List<ActivityLog> findByUserId(Long userId);

    List<ActivityLog> findByActivityType(ActivityType activityType);

    List<ActivityLog> findByActivityDateBetween(LocalDateTime start, LocalDateTime end);

    List<ActivityLog> findByUserIdAndActivityDateBetween(Long userId, LocalDateTime start, LocalDateTime end);

    @Query("SELECT COUNT(a) FROM ActivityLog a WHERE a.activityType = :type AND a.activityDate BETWEEN :start AND :end")
    long countByTypeAndDateBetween(@Param("type") ActivityType type, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT a.activityType, COUNT(a) FROM ActivityLog a WHERE a.activityDate BETWEEN :start AND :end GROUP BY a.activityType")
    List<Object[]> countByActivityTypeGrouped(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT DATE(a.activityDate) as date, COUNT(a) FROM ActivityLog a WHERE a.activityDate BETWEEN :start AND :end GROUP BY DATE(a.activityDate) ORDER BY date")
    List<Object[]> countByDateGrouped(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT a.entityType, a.entityId, COUNT(a) as count FROM ActivityLog a WHERE a.activityType = :type AND a.activityDate BETWEEN :start AND :end GROUP BY a.entityType, a.entityId ORDER BY count DESC")
    List<Object[]> findTopEntitiesByActivity(@Param("type") ActivityType type, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
}
