package com.metalix.metalixbackend.wastecollection.interfaces.rest;

import com.metalix.metalixbackend.wastecollection.application.services.WasteCollectorService;
import com.metalix.metalixbackend.wastecollection.domain.model.aggregates.WasteCollector;
import com.metalix.metalixbackend.wastecollection.interfaces.rest.dto.CreateWasteCollectorRequest;
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
@RequestMapping("/api/v1/waste-collectors")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Waste Collectors", description = "Waste collector management endpoints")
public class WasteCollectorController {
    
    private final WasteCollectorService wasteCollectorService;
    
    @GetMapping
    @Operation(summary = "Get all waste collectors")
    public ResponseEntity<List<WasteCollector>> getAllCollectors() {
        return ResponseEntity.ok(wasteCollectorService.getAllCollectors());
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get waste collector by ID")
    public ResponseEntity<WasteCollector> getCollectorById(@PathVariable Long id) {
        return ResponseEntity.ok(wasteCollectorService.getCollectorById(id));
    }
    
    @GetMapping("/municipality/{municipalityId}")
    @Operation(summary = "Get waste collectors by municipality")
    public ResponseEntity<List<WasteCollector>> getCollectorsByMunicipality(@PathVariable Long municipalityId) {
        return ResponseEntity.ok(wasteCollectorService.getCollectorsByMunicipality(municipalityId));
    }
    
    @GetMapping("/zone/{zoneId}")
    @Operation(summary = "Get waste collectors by zone")
    public ResponseEntity<List<WasteCollector>> getCollectorsByZone(@PathVariable Long zoneId) {
        return ResponseEntity.ok(wasteCollectorService.getCollectorsByZone(zoneId));
    }
    
    @GetMapping("/full")
    @Operation(summary = "Get full collectors (>80% capacity)")
    public ResponseEntity<List<WasteCollector>> getFullCollectors() {
        return ResponseEntity.ok(wasteCollectorService.getFullCollectors());
    }
    
    @PostMapping
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN', 'MUNICIPALITY_ADMIN')")
    @Operation(summary = "Create waste collector")
    public ResponseEntity<WasteCollector> createCollector(@Valid @RequestBody CreateWasteCollectorRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(wasteCollectorService.createCollector(request));
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN', 'MUNICIPALITY_ADMIN')")
    @Operation(summary = "Update waste collector")
    public ResponseEntity<WasteCollector> updateCollector(
            @PathVariable Long id,
            @RequestBody WasteCollector collector
    ) {
        return ResponseEntity.ok(wasteCollectorService.updateCollector(id, collector));
    }
    
    @PatchMapping("/{id}/empty")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN', 'MUNICIPALITY_ADMIN')")
    @Operation(summary = "Empty waste collector")
    public ResponseEntity<WasteCollector> emptyCollector(@PathVariable Long id) {
        return ResponseEntity.ok(wasteCollectorService.emptyCollector(id));
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN', 'MUNICIPALITY_ADMIN')")
    @Operation(summary = "Delete waste collector")
    public ResponseEntity<Void> deleteCollector(@PathVariable Long id) {
        wasteCollectorService.deleteCollector(id);
        return ResponseEntity.noContent().build();
    }
}

