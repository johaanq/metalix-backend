package com.metalix.metalixbackend.municipality.interfaces.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MunicipalityStatsResponse {
    private Long municipalityId;
    private Integer totalUsers;
    private Integer activeUsers;
    private Integer totalCollections;
    private Double totalWeight;
    private Integer totalPoints;
    private Double averageParticipation;
    private String lastUpdated;
}

