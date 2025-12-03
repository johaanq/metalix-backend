package com.metalix.metalixbackend.municipality.interfaces.rest;

import com.metalix.metalixbackend.municipality.application.services.MunicipalityService;
import com.metalix.metalixbackend.municipality.domain.model.aggregates.Municipality;
import com.metalix.metalixbackend.municipality.interfaces.rest.dto.MunicipalityResponse;
import com.metalix.metalixbackend.municipality.interfaces.rest.dto.CreateMunicipalityRequest;
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
@RequestMapping("/api/v1/municipalities")
@RequiredArgsConstructor
@Tag(name = "Municipalities", description = "Municipality management endpoints")
public class MunicipalityController {
    
    private final MunicipalityService municipalityService;
    
    @GetMapping
    @Operation(summary = "Get all municipalities")
    public ResponseEntity<List<MunicipalityResponse>> getAllMunicipalities() {
        List<Municipality> municipalities = municipalityService.getAllMunicipalities();
        return ResponseEntity.ok(municipalities.stream()
                .map(MunicipalityResponse::fromEntity)
                .collect(Collectors.toList()));
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get municipality by ID")
    public ResponseEntity<MunicipalityResponse> getMunicipalityById(@PathVariable Long id) {
        Municipality municipality = municipalityService.getMunicipalityById(id);
        return ResponseEntity.ok(MunicipalityResponse.fromEntity(municipality));
    }
    
    @PostMapping
    @PreAuthorize("hasRole('SYSTEM_ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Create municipality")
    public ResponseEntity<MunicipalityResponse> createMunicipality(@Valid @RequestBody CreateMunicipalityRequest request) {
        Municipality created = municipalityService.createMunicipality(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(MunicipalityResponse.fromEntity(created));
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN', 'MUNICIPALITY_ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Update municipality")
    public ResponseEntity<MunicipalityResponse> updateMunicipality(
            @PathVariable Long id,
            @RequestBody Municipality municipality
    ) {
        Municipality updated = municipalityService.updateMunicipality(id, municipality);
        return ResponseEntity.ok(MunicipalityResponse.fromEntity(updated));
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SYSTEM_ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Delete municipality")
    public ResponseEntity<Void> deleteMunicipality(@PathVariable Long id) {
        municipalityService.deleteMunicipality(id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/{id}/stats")
    @Operation(summary = "Get municipality statistics")
    public ResponseEntity<com.metalix.metalixbackend.municipality.interfaces.rest.dto.MunicipalityStatsResponse> getMunicipalityStats(@PathVariable Long id) {
        return ResponseEntity.ok(municipalityService.getMunicipalityStats(id));
    }
    
    @GetMapping("/{id}/dashboard")
    @Operation(summary = "Get dashboard data for municipality")
    public ResponseEntity<com.metalix.metalixbackend.municipality.interfaces.rest.dto.DashboardDataResponse> getDashboardData(@PathVariable Long id) {
        return ResponseEntity.ok(municipalityService.getDashboardData(id));
    }
}

