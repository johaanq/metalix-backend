package com.metalix.metalixbackend.wastecollection.interfaces.rest.dto;

import com.metalix.metalixbackend.wastecollection.domain.model.valueobjects.CollectorType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class CreateWasteCollectorRequest {
    @NotBlank(message = "Name is required")
    private String name;
    
    @NotNull(message = "Type is required")
    private CollectorType type;
    
    @NotBlank(message = "Location is required")
    private String location;
    
    @NotNull(message = "Municipality ID is required")
    private Long municipalityId;
    
    private Long zoneId;
    
    @NotNull(message = "Capacity is required")
    @Positive(message = "Capacity must be positive")
    private Double capacity;
    
    private String sensorId;
}
