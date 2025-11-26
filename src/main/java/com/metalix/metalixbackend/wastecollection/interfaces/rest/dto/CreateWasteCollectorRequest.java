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
    
    private CollectorType type; // Optional, defaults to GENERAL if not provided
    
    // Support both String location and object location
    private String location;
    private LocationData locationData; // For object-based location
    
    @NotNull(message = "Municipality ID is required")
    private Long municipalityId;
    
    private Long zoneId;
    
    @NotNull(message = "Capacity is required")
    @Positive(message = "Capacity must be positive")
    private Double capacity;
    
    private String sensorId;
    
    // Inner class for location object
    @Data
    public static class LocationData {
        private Double latitude;
        private Double longitude;
        private String address;
    }
    
    // Helper method to get location string
    public String getLocationString() {
        if (location != null && !location.isEmpty()) {
            return location;
        }
        if (locationData != null && locationData.getAddress() != null) {
            return locationData.getAddress();
        }
        return null;
    }
    
    // Helper method to get type with default
    public CollectorType getTypeOrDefault() {
        return type != null ? type : CollectorType.GENERAL;
    }
}
