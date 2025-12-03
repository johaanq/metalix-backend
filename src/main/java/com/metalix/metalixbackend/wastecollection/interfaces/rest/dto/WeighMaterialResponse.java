package com.metalix.metalixbackend.wastecollection.interfaces.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WeighMaterialResponse {
    
    private String sessionToken;
    private Double weight;
    private String recyclableType;
    private Integer calculatedPoints;
    private String message;
    private Integer expiresInSeconds;
}

