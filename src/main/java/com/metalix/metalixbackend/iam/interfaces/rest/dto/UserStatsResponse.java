package com.metalix.metalixbackend.iam.interfaces.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserStatsResponse {
    private Long userId;
    private Integer totalCollections;
    private Double totalWeight;
    private Integer totalPoints;
    private Double averageWeight;
    private Integer collectionsThisMonth;
    private Integer pointsThisMonth;
    private Integer rank;
    private LocalDateTime lastUpdated;
}

