package com.metalix.metalixbackend.wastecollection.interfaces.rest;

import com.metalix.metalixbackend.wastecollection.application.services.IoTCollectionService;
import com.metalix.metalixbackend.wastecollection.interfaces.rest.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/iot/collections")
@RequiredArgsConstructor
@Tag(name = "IoT Collections", description = "Endpoints for IoT devices to register waste collections")
public class IoTCollectionController {
    
    private final IoTCollectionService ioTCollectionService;
    
    @PostMapping("/weigh")
    @Operation(
        summary = "STEP 1: Weigh material and calculate points (Public endpoint)",
        description = "First step: IoT device weighs the material and calculates points. " +
                     "Returns a session token to be used in the next step."
    )
    public ResponseEntity<?> weighMaterial(@Valid @RequestBody WeighMaterialRequest request) {
        
        log.info("Step 1: Weighing material - {} kg of {}", request.getWeight(), request.getRecyclableType());
        
        try {
            WeighMaterialResponse response = ioTCollectionService.weighMaterial(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            log.error("Error weighing material: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }
    
    @PostMapping("/confirm")
    @Operation(
        summary = "STEP 2: Confirm with RFID and assign points (Public endpoint)",
        description = "Second step: User scans their RFID card to complete the collection. " +
                     "Points and weight are assigned to the user's statistics."
    )
    public ResponseEntity<?> confirmWithRfid(@Valid @RequestBody ConfirmWithRfidRequest request) {
        
        log.info("Step 2: Confirming with RFID: {}", request.getRfidCardNumber());
        
        try {
            IoTCollectionResponse response = ioTCollectionService.confirmWithRfid(request);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            log.error("Error confirming collection: {}", e.getMessage(), e);
            
            IoTCollectionResponse errorResponse = IoTCollectionResponse.builder()
                    .success(false)
                    .message("Error: " + e.getMessage())
                    .build();
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }
    
    @GetMapping("/health")
    @Operation(summary = "Health check for IoT endpoint")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("IoT Collection Service is running");
    }
    
    @PostMapping("/cleanup")
    @Operation(summary = "Cleanup expired pending collections")
    public ResponseEntity<String> cleanupExpired() {
        try {
            ioTCollectionService.cleanupExpiredPendingCollections();
            return ResponseEntity.ok("Expired pending collections cleaned up");
        } catch (Exception e) {
            log.error("Error cleaning up: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error: " + e.getMessage());
        }
    }
}

