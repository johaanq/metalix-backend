package com.metalix.metalixbackend.wastecollection.interfaces.rest;

import com.metalix.metalixbackend.wastecollection.application.services.WasteCollectionService;
import com.metalix.metalixbackend.wastecollection.domain.model.entities.WasteCollection;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/waste-collections")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Waste Collections", description = "Waste collection management endpoints")
public class WasteCollectionController {
    
    private final WasteCollectionService wasteCollectionService;
    
    @GetMapping
    @Operation(summary = "Get all waste collections")
    public ResponseEntity<List<WasteCollection>> getAllCollections() {
        return ResponseEntity.ok(wasteCollectionService.getAllCollections());
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get waste collection by ID")
    public ResponseEntity<WasteCollection> getCollectionById(@PathVariable Long id) {
        return ResponseEntity.ok(wasteCollectionService.getCollectionById(id));
    }
    
    @GetMapping("/user/{userId}")
    @Operation(summary = "Get waste collections by user")
    public ResponseEntity<Page<WasteCollection>> getCollectionsByUser(
            @PathVariable Long userId,
            Pageable pageable
    ) {
        return ResponseEntity.ok(wasteCollectionService.getCollectionsByUser(userId, pageable));
    }
    
    @GetMapping("/collector/{collectorId}")
    @Operation(summary = "Get waste collections by collector")
    public ResponseEntity<List<WasteCollection>> getCollectionsByCollector(@PathVariable Long collectorId) {
        return ResponseEntity.ok(wasteCollectionService.getCollectionsByCollector(collectorId));
    }
    
    @GetMapping("/municipality/{municipalityId}")
    @Operation(summary = "Get waste collections by municipality")
    public ResponseEntity<List<WasteCollection>> getCollectionsByMunicipality(@PathVariable Long municipalityId) {
        return ResponseEntity.ok(wasteCollectionService.getCollectionsByMunicipality(municipalityId));
    }
    
    @PostMapping
    @Operation(summary = "Create waste collection")
    public ResponseEntity<WasteCollection> createCollection(@Valid @RequestBody WasteCollection collection) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(wasteCollectionService.createCollection(collection));
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Update waste collection")
    public ResponseEntity<WasteCollection> updateCollection(
            @PathVariable Long id,
            @RequestBody WasteCollection collection
    ) {
        return ResponseEntity.ok(wasteCollectionService.updateCollection(id, collection));
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete waste collection")
    public ResponseEntity<Void> deleteCollection(@PathVariable Long id) {
        wasteCollectionService.deleteCollection(id);
        return ResponseEntity.noContent().build();
    }
}

