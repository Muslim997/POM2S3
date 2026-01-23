package com.elzocodeur.campusmaster.application.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivityStatsDto {

    private LocalDate startDate;
    private LocalDate endDate;
    private Long totalActivities;
    private Long totalPageViews;
    private Long totalDownloads;
    private Long totalUploads;
    private List<DailyActivityDto> dailyActivities;
    private Map<String, Long> activitiesByType;
    private List<TopActivityDto> topPages;
    private List<TopActivityDto> topDownloads;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DailyActivityDto {
        private LocalDate date;
        private Long pageViews;
        private Long downloads;
        private Long uploads;
        private Long totalActivities;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TopActivityDto {
        private String name;
        private Long count;
        private String type;
    }
}
