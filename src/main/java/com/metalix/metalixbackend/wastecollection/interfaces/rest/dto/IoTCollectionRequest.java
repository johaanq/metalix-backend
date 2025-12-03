package com.metalix.metalixbackend.wastecollection.interfaces.rest.dto;

import com.metalix.metalixbackend.wastecollection.domain.model.valueobjects.RecyclableType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IoTCollectionRequest {
    
    @NotNull(message = "User ID (bañista) is required")
    private Long userId;
    
    @NotNull(message = "Weight is required")
    @Positive(message = "Weight must be positive")
    private Double weight;
    
    @NotNull(message = "Collector ID is required")
    private Long collectorId;
    
    @NotNull(message = "Recyclable type is required")
    private RecyclableType recyclableType;
    
    @NotNull(message = "Points are required")
    @PositiveOrZero(message = "Points must be zero or positive")
    private Integer points;
}

