package com.metalix.metalixbackend.municipality.interfaces.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardDataResponse {
    private Long municipalityId;
    private Integer totalCollections;
    private Double totalWeight;
    private Integer activeUsers;
    private Integer totalPoints;
    private EnvironmentalImpact environmentalImpact;
    private List<AlertSummary> recentAlerts;
    private List<TopCollector> topCollectors;
    private LocalDateTime lastUpdated;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EnvironmentalImpact {
        private Double co2Saved;
        private Double energySaved;
        private Double treesEquivalent;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AlertSummary {
        private Long id;
        private String alertType;
        private String severity;
        private String message;
        private Boolean isResolved;
        private LocalDateTime createdAt;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TopCollector {
        private Long id;
        private String name;
        private Integer collections;
        private Double weight;
    }
}

