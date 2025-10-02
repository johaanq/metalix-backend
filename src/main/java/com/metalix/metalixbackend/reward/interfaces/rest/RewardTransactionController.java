package com.metalix.metalixbackend.reward.interfaces.rest;

import com.metalix.metalixbackend.reward.application.services.RewardTransactionService;
import com.metalix.metalixbackend.reward.domain.model.entities.RewardTransaction;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/reward-transactions")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Reward Transactions", description = "Reward transaction endpoints")
public class RewardTransactionController {
    
    private final RewardTransactionService transactionService;
    
    @GetMapping
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN', 'MUNICIPALITY_ADMIN')")
    @Operation(summary = "Get all transactions")
    public ResponseEntity<List<RewardTransaction>> getAllTransactions() {
        return ResponseEntity.ok(transactionService.getAllTransactions());
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get transaction by ID")
    public ResponseEntity<RewardTransaction> getTransactionById(@PathVariable Long id) {
        return ResponseEntity.ok(transactionService.getTransactionById(id));
    }
    
    @GetMapping("/user/{userId}")
    @Operation(summary = "Get transactions by user")
    public ResponseEntity<Page<RewardTransaction>> getTransactionsByUser(
            @PathVariable Long userId,
            Pageable pageable
    ) {
        return ResponseEntity.ok(transactionService.getTransactionsByUser(userId, pageable));
    }
    
    @PostMapping("/redeem")
    @Operation(summary = "Redeem a reward")
    public ResponseEntity<RewardTransaction> redeemReward(@RequestBody Map<String, Long> request) {
        Long userId = request.get("userId");
        Long rewardId = request.get("rewardId");
        return ResponseEntity.ok(transactionService.redeemReward(userId, rewardId));
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SYSTEM_ADMIN')")
    @Operation(summary = "Delete transaction")
    public ResponseEntity<Void> deleteTransaction(@PathVariable Long id) {
        transactionService.deleteTransaction(id);
        return ResponseEntity.noContent().build();
    }
}

