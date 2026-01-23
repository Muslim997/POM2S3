package com.elzocodeur.campusmaster.application.service;

import com.elzocodeur.campusmaster.application.dto.dashboard.ActivityStatsDto;
import com.elzocodeur.campusmaster.domain.entity.ActivityLog;
import com.elzocodeur.campusmaster.domain.entity.User;
import com.elzocodeur.campusmaster.domain.enums.ActivityType;
import com.elzocodeur.campusmaster.infrastructure.persistence.repository.ActivityLogRepository;
import com.elzocodeur.campusmaster.infrastructure.persistence.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ActivityTrackingService {

    private final ActivityLogRepository activityLogRepository;
    private final UserRepository userRepository;

    @Transactional
    public void logActivity(Long userId, ActivityType activityType, String entityType, Long entityId,
                           String description, String ipAddress, String userAgent) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        ActivityLog log = ActivityLog.builder()
                .user(user)
                .activityType(activityType)
                .entityType(entityType)
                .entityId(entityId)
                .description(description)
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .activityDate(LocalDateTime.now())
                .build();

        activityLogRepository.save(log);
    }

    public ActivityStatsDto getActivityStats(LocalDate startDate, LocalDate endDate) {
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atTime(23, 59, 59);

        List<ActivityLog> activities = activityLogRepository.findByActivityDateBetween(start, end);

        long totalActivities = activities.size();
        long totalPageViews = activities.stream()
                .filter(a -> a.getActivityType() == ActivityType.PAGE_VIEW)
                .count();
        long totalDownloads = activities.stream()
                .filter(a -> a.getActivityType() == ActivityType.FILE_DOWNLOAD)
                .count();
        long totalUploads = activities.stream()
                .filter(a -> a.getActivityType() == ActivityType.FILE_UPLOAD)
                .count();

        // Grouper par type d'activité
        Map<String, Long> activitiesByType = activities.stream()
                .collect(Collectors.groupingBy(
                        a -> a.getActivityType().name(),
                        Collectors.counting()
                ));

        // Activités quotidiennes
        List<ActivityStatsDto.DailyActivityDto> dailyActivities = calculateDailyActivities(activities, startDate, endDate);

        // Top pages vues
        List<ActivityStatsDto.TopActivityDto> topPages = getTopActivities(ActivityType.PAGE_VIEW, start, end);

        // Top téléchargements
        List<ActivityStatsDto.TopActivityDto> topDownloads = getTopActivities(ActivityType.FILE_DOWNLOAD, start, end);

        return ActivityStatsDto.builder()
                .startDate(startDate)
                .endDate(endDate)
                .totalActivities(totalActivities)
                .totalPageViews(totalPageViews)
                .totalDownloads(totalDownloads)
                .totalUploads(totalUploads)
                .dailyActivities(dailyActivities)
                .activitiesByType(activitiesByType)
                .topPages(topPages)
                .topDownloads(topDownloads)
                .build();
    }

    private List<ActivityStatsDto.DailyActivityDto> calculateDailyActivities(List<ActivityLog> activities,
                                                                              LocalDate startDate, LocalDate endDate) {
        Map<LocalDate, List<ActivityLog>> activitiesByDate = activities.stream()
                .collect(Collectors.groupingBy(a -> a.getActivityDate().toLocalDate()));

        List<ActivityStatsDto.DailyActivityDto> dailyStats = new ArrayList<>();

        LocalDate current = startDate;
        while (!current.isAfter(endDate)) {
            List<ActivityLog> dayActivities = activitiesByDate.getOrDefault(current, new ArrayList<>());

            long pageViews = dayActivities.stream()
                    .filter(a -> a.getActivityType() == ActivityType.PAGE_VIEW)
                    .count();
            long downloads = dayActivities.stream()
                    .filter(a -> a.getActivityType() == ActivityType.FILE_DOWNLOAD)
                    .count();
            long uploads = dayActivities.stream()
                    .filter(a -> a.getActivityType() == ActivityType.FILE_UPLOAD)
                    .count();

            dailyStats.add(ActivityStatsDto.DailyActivityDto.builder()
                    .date(current)
                    .pageViews(pageViews)
                    .downloads(downloads)
                    .uploads(uploads)
                    .totalActivities((long) dayActivities.size())
                    .build());

            current = current.plusDays(1);
        }

        return dailyStats;
    }

    private List<ActivityStatsDto.TopActivityDto> getTopActivities(ActivityType type, LocalDateTime start, LocalDateTime end) {
        List<Object[]> topEntities = activityLogRepository.findTopEntitiesByActivity(type, start, end);

        return topEntities.stream()
                .limit(10)
                .map(result -> ActivityStatsDto.TopActivityDto.builder()
                        .type((String) result[0])
                        .name(result[0] + " #" + result[1])
                        .count((Long) result[2])
                        .build())
                .collect(Collectors.toList());
    }

    public ActivityStatsDto getWeeklyActivityStats() {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(6);
        return getActivityStats(startDate, endDate);
    }

    public Long getActivityCountForUser(Long userId, LocalDate startDate, LocalDate endDate) {
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atTime(23, 59, 59);

        return (long) activityLogRepository
                .findByUserIdAndActivityDateBetween(userId, start, end)
                .size();
    }
}
