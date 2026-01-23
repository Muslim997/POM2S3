package com.elzocodeur.campusmaster.infrastructure.persistence.repository;

import com.elzocodeur.campusmaster.domain.entity.NotificationPreference;
import com.elzocodeur.campusmaster.domain.enums.NotificationChannel;
import com.elzocodeur.campusmaster.domain.enums.NotificationEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NotificationPreferenceRepository extends JpaRepository<NotificationPreference, Long> {

    List<NotificationPreference> findByUserId(Long userId);

    Optional<NotificationPreference> findByUserIdAndEventTypeAndChannel(
            Long userId,
            NotificationEvent eventType,
            NotificationChannel channel);

    @Query("SELECT np FROM NotificationPreference np WHERE np.user.id = :userId " +
            "AND np.eventType = :eventType AND np.enabled = true")
    List<NotificationPreference> findEnabledPreferences(
            @Param("userId") Long userId,
            @Param("eventType") NotificationEvent eventType);

    @Query("SELECT np.channel FROM NotificationPreference np WHERE np.user.id = :userId " +
            "AND np.eventType = :eventType AND np.enabled = true")
    List<NotificationChannel> findEnabledChannels(
            @Param("userId") Long userId,
            @Param("eventType") NotificationEvent eventType);

    void deleteByUserId(Long userId);
}
