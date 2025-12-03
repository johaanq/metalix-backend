package com.metalix.metalixbackend.wastecollection.interfaces.rest.dto;

import com.metalix.metalixbackend.wastecollection.domain.model.valueobjects.RecyclableType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WeighMaterialRequest {
    
    @NotNull(message = "Weight is required")
    @Positive(message = "Weight must be positive")
    private Double weight;
    
    @NotNull(message = "Collector ID is required")
    private Long collectorId;
    
    @NotNull(message = "Recyclable type is required")
    private RecyclableType recyclableType;
    
    // Opcional: ID del dispositivo IoT
    private String deviceId;
}

