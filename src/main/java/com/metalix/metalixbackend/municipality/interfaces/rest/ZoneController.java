package com.metalix.metalixbackend.municipality.interfaces.rest;

import com.metalix.metalixbackend.municipality.application.services.ZoneService;
import com.metalix.metalixbackend.municipality.domain.model.entities.Zone;
import com.metalix.metalixbackend.municipality.interfaces.rest.dto.ZoneResponse;
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
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/zones")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Zones", description = "Zone management endpoints")
public class ZoneController {
    
    private final ZoneService zoneService;
    
    @GetMapping
    @Operation(summary = "Get all zones")
    public ResponseEntity<List<ZoneResponse>> getAllZones() {
        List<Zone> zones = zoneService.getAllZones();
        return ResponseEntity.ok(zones.stream()
                .map(ZoneResponse::fromEntity)
                .collect(Collectors.toList()));
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get zone by ID")
    public ResponseEntity<ZoneResponse> getZoneById(@PathVariable Long id) {
        Zone zone = zoneService.getZoneById(id);
        return ResponseEntity.ok(ZoneResponse.fromEntity(zone));
    }
    
    @GetMapping("/municipality/{municipalityId}")
    @Operation(summary = "Get zones by municipality")
    public ResponseEntity<List<ZoneResponse>> getZonesByMunicipality(@PathVariable Long municipalityId) {
        List<Zone> zones = zoneService.getZonesByMunicipality(municipalityId);
        return ResponseEntity.ok(zones.stream()
                .map(ZoneResponse::fromEntity)
                .collect(Collectors.toList()));
    }
    
    @PostMapping
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN', 'MUNICIPALITY_ADMIN')")
    @Operation(summary = "Create zone")
    public ResponseEntity<ZoneResponse> createZone(@Valid @RequestBody Zone zone) {
        Zone created = zoneService.createZone(zone);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ZoneResponse.fromEntity(created));
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN', 'MUNICIPALITY_ADMIN')")
    @Operation(summary = "Update zone")
    public ResponseEntity<ZoneResponse> updateZone(
            @PathVariable Long id,
            @RequestBody Zone zone
    ) {
        Zone updated = zoneService.updateZone(id, zone);
        return ResponseEntity.ok(ZoneResponse.fromEntity(updated));
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN', 'MUNICIPALITY_ADMIN')")
    @Operation(summary = "Delete zone")
    public ResponseEntity<Void> deleteZone(@PathVariable Long id) {
        zoneService.deleteZone(id);
        return ResponseEntity.noContent().build();
    }
}

