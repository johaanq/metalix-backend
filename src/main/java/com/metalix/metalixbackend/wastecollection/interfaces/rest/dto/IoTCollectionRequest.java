package com.metalix.metalixbackend.wastecollection.interfaces.rest.dto;

import com.metalix.metalixbackend.wastecollection.domain.model.valueobjects.RecyclableType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IoTCollectionRequest {
    
    @NotBlank(message = "RFID card number is required")
    private String rfidCardNumber;
    
    @NotNull(message = "Weight is required")
    @Positive(message = "Weight must be positive")
    private Double weight;
    
    @NotNull(message = "Collector ID is required")
    private Long collectorId;
    
    @NotNull(message = "Recyclable type is required")
    private RecyclableType recyclableType;
    
    // Opcional: coordenadas del IoT
    private String coordinates;
}

