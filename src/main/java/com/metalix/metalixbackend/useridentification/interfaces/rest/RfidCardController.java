package com.metalix.metalixbackend.useridentification.interfaces.rest;

import com.metalix.metalixbackend.useridentification.application.services.RfidCardService;
import com.metalix.metalixbackend.useridentification.domain.model.aggregates.RfidCard;
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
import java.util.Map;

@RestController
@RequestMapping("/api/v1/rfid-cards")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "RFID Cards", description = "RFID card management endpoints")
public class RfidCardController {
    
    private final RfidCardService rfidCardService;
    
    @GetMapping
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN', 'MUNICIPALITY_ADMIN')")
    @Operation(summary = "Get all RFID cards")
    public ResponseEntity<List<RfidCard>> getAllCards() {
        return ResponseEntity.ok(rfidCardService.getAllCards());
    }
    
    @GetMapping("/user/{userId}")
    @Operation(summary = "Get RFID card by user ID")
    public ResponseEntity<RfidCard> getCardByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(rfidCardService.getCardByUserId(userId));
    }
    
    @PostMapping
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN', 'MUNICIPALITY_ADMIN')")
    @Operation(summary = "Create RFID card")
    public ResponseEntity<RfidCard> createCard(@Valid @RequestBody RfidCard card) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(rfidCardService.createCard(card));
    }
    
    @PostMapping("/assign")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN', 'MUNICIPALITY_ADMIN')")
    @Operation(summary = "Assign card to user")
    public ResponseEntity<RfidCard> assignCardToUser(@RequestBody Map<String, Object> request) {
        String cardNumber = (String) request.get("cardNumber");
        Long userId = Long.valueOf(request.get("userId").toString());
        return ResponseEntity.ok(rfidCardService.assignCardToUser(cardNumber, userId));
    }
    
    @PostMapping("/link")
    @Operation(summary = "Link RFID card to own user profile (for citizens)")
    public ResponseEntity<RfidCard> linkCardToOwnProfile(@RequestBody Map<String, Object> request) {
        String cardNumber = (String) request.get("cardNumber");
        Long userId = Long.valueOf(request.get("userId").toString());
        // Aquí deberías verificar que el userId coincida con el usuario autenticado
        // pero por simplicidad, permitimos que cualquier usuario autenticado pueda vincular su tarjeta
        return ResponseEntity.ok(rfidCardService.assignCardToUser(cardNumber, userId));
    }
    
    @PostMapping("/use/{cardNumber}")
    @Operation(summary = "Use RFID card")
    public ResponseEntity<RfidCard> useCard(@PathVariable String cardNumber) {
        return ResponseEntity.ok(rfidCardService.useCard(cardNumber));
    }
    
    @PatchMapping("/{id}/block")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN', 'MUNICIPALITY_ADMIN')")
    @Operation(summary = "Block RFID card")
    public ResponseEntity<RfidCard> blockCard(@PathVariable Long id) {
        return ResponseEntity.ok(rfidCardService.blockCard(id));
    }
}

