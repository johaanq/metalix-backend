package com.metalix.metalixbackend.reward.interfaces.rest;

import com.metalix.metalixbackend.reward.application.services.RewardService;
import com.metalix.metalixbackend.reward.domain.model.aggregates.Reward;
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
@RequestMapping("/api/v1/rewards")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Rewards", description = "Reward management endpoints")
public class RewardController {
    
    private final RewardService rewardService;
    
    @GetMapping
    @Operation(summary = "Get all rewards")
    public ResponseEntity<List<Reward>> getAllRewards() {
        return ResponseEntity.ok(rewardService.getAllRewards());
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get reward by ID")
    public ResponseEntity<Reward> getRewardById(@PathVariable Long id) {
        return ResponseEntity.ok(rewardService.getRewardById(id));
    }
    
    @GetMapping("/active")
    @Operation(summary = "Get active rewards")
    public ResponseEntity<List<Reward>> getActiveRewards() {
        return ResponseEntity.ok(rewardService.getActiveRewards());
    }
    
    @GetMapping("/municipality/{municipalityId}")
    @Operation(summary = "Get rewards by municipality")
    public ResponseEntity<List<Reward>> getRewardsByMunicipality(@PathVariable Long municipalityId) {
        return ResponseEntity.ok(rewardService.getRewardsByMunicipality(municipalityId));
    }
    
    @GetMapping("/affordable/{maxPoints}")
    @Operation(summary = "Get affordable rewards for user")
    public ResponseEntity<List<Reward>> getAffordableRewards(@PathVariable Integer maxPoints) {
        return ResponseEntity.ok(rewardService.getAffordableRewards(maxPoints));
    }
    
    @PostMapping
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN', 'MUNICIPALITY_ADMIN')")
    @Operation(summary = "Create reward")
    public ResponseEntity<Reward> createReward(@Valid @RequestBody Reward reward) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(rewardService.createReward(reward));
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN', 'MUNICIPALITY_ADMIN')")
    @Operation(summary = "Update reward")
    public ResponseEntity<Reward> updateReward(
            @PathVariable Long id,
            @RequestBody Reward reward
    ) {
        return ResponseEntity.ok(rewardService.updateReward(id, reward));
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN', 'MUNICIPALITY_ADMIN')")
    @Operation(summary = "Delete reward")
    public ResponseEntity<Void> deleteReward(@PathVariable Long id) {
        rewardService.deleteReward(id);
        return ResponseEntity.noContent().build();
    }
}

