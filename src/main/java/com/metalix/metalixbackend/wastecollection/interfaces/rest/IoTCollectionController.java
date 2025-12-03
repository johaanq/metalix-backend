package com.metalix.metalixbackend.wastecollection.interfaces.rest;

import com.metalix.metalixbackend.shared.exception.ResourceNotFoundException;
import com.metalix.metalixbackend.wastecollection.application.services.IoTCollectionService;
import com.metalix.metalixbackend.wastecollection.interfaces.rest.dto.IoTCollectionRequest;
import com.metalix.metalixbackend.wastecollection.interfaces.rest.dto.IoTCollectionResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/iot/collections")
@RequiredArgsConstructor
@Tag(name = "IoT Collections", description = "Endpoints for IoT devices to register waste collections")
public class IoTCollectionController {
    
    private final IoTCollectionService ioTCollectionService;
    
    @PostMapping("/register")
    @Operation(
        summary = "Register collection from IoT device (Public endpoint)",
        description = "Registers a waste collection using RFID card. " +
                     "Automatically identifies user, calculates points based on weight and material type, " +
                     "and updates user statistics."
    )
    public ResponseEntity<IoTCollectionResponse> registerCollection(
            @Valid @RequestBody IoTCollectionRequest request) {
        
        log.info("IoT collection request - RFID: {}, Weight: {} kg, Type: {}", 
                request.getRfidCardNumber(), request.getWeight(), request.getRecyclableType());
        
        try {
            IoTCollectionResponse response = ioTCollectionService.registerCollection(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (ResourceNotFoundException e) {
            log.error("Resource not found: {}", e.getMessage());
            
            IoTCollectionResponse errorResponse = IoTCollectionResponse.builder()
                    .success(false)
                    .message("Error: " + e.getMessage())
                    .build();
            
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        } catch (Exception e) {
            log.error("Error processing collection: {}", e.getMessage(), e);
            
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
}
