package com.metalix.metalixbackend.wastecollection.interfaces.rest;

import com.metalix.metalixbackend.wastecollection.application.services.SensorDataService;
import com.metalix.metalixbackend.wastecollection.domain.model.entities.SensorData;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/sensor-data")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Sensor Data", description = "Sensor data management endpoints")
public class SensorDataController {
    
    private final SensorDataService sensorDataService;
    
    @GetMapping
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN', 'MUNICIPALITY_ADMIN')")
    @Operation(summary = "Get all sensor data")
    public ResponseEntity<List<SensorData>> getAllSensorData() {
        return ResponseEntity.ok(sensorDataService.getAllSensorData());
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get sensor data by ID")
    public ResponseEntity<SensorData> getSensorDataById(@PathVariable Long id) {
        return ResponseEntity.ok(sensorDataService.getSensorDataById(id));
    }
    
    @GetMapping("/collector/{collectorId}")
    @Operation(summary = "Get sensor data by collector")
    public ResponseEntity<List<SensorData>> getSensorDataByCollector(@PathVariable Long collectorId) {
        return ResponseEntity.ok(sensorDataService.getSensorDataByCollector(collectorId));
    }
    
    @GetMapping("/collector/{collectorId}/latest")
    @Operation(summary = "Get latest sensor data by collector")
    public ResponseEntity<SensorData> getLatestSensorDataByCollector(@PathVariable Long collectorId) {
        return ResponseEntity.ok(sensorDataService.getLatestSensorDataByCollector(collectorId));
    }
    
    @PostMapping
    @Operation(summary = "Create sensor data")
    public ResponseEntity<SensorData> createSensorData(@Valid @RequestBody SensorData sensorData) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(sensorDataService.createSensorData(sensorData));
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SYSTEM_ADMIN')")
    @Operation(summary = "Delete sensor data")
    public ResponseEntity<Void> deleteSensorData(@PathVariable Long id) {
        sensorDataService.deleteSensorData(id);
        return ResponseEntity.noContent().build();
    }
}

