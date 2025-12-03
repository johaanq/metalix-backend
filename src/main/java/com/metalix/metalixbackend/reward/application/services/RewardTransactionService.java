package com.metalix.metalixbackend.reward.application.services;

import com.metalix.metalixbackend.iam.application.services.UserService;
import com.metalix.metalixbackend.iam.domain.model.aggregates.User;
import com.metalix.metalixbackend.reward.domain.model.aggregates.Reward;
import com.metalix.metalixbackend.reward.domain.model.entities.RewardTransaction;
import com.metalix.metalixbackend.reward.domain.model.valueobjects.TransactionStatus;
import com.metalix.metalixbackend.reward.domain.model.valueobjects.TransactionType;
import com.metalix.metalixbackend.reward.domain.repository.RewardTransactionRepository;
import com.metalix.metalixbackend.shared.exception.ResourceNotFoundException;
import com.metalix.metalixbackend.shared.exception.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RewardTransactionService {
    
    private final RewardTransactionRepository transactionRepository;
    private final RewardService rewardService;
    private final UserService userService;
    
    public RewardTransaction getTransactionById(Long id) {
        return transactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("RewardTransaction", id));
    }
    
    public List<RewardTransaction> getAllTransactions() {
        return transactionRepository.findAll();
    }
    
    public List<RewardTransaction> getTransactionsByUser(Long userId) {
        return transactionRepository.findByUserId(userId);
    }
    
    public Page<RewardTransaction> getTransactionsByUser(Long userId, Pageable pageable) {
        return transactionRepository.findByUserId(userId, pageable);
    }
    
    public List<RewardTransaction> getTransactionsByReward(Long rewardId) {
        return transactionRepository.findByRewardId(rewardId);
    }
    
    @Transactional
    public RewardTransaction redeemReward(Long userId, Long rewardId) {
        User user = userService.getUserById(userId);
        Reward reward = rewardService.getRewardById(rewardId);
        
        // Simplified validation: Only check if reward is active
        if (!reward.getIsActive()) {
            throw new ValidationException("Reward is not active");
        }
        
        // Check if user has enough points
        if (user.getTotalPoints() < reward.getPointsCost()) {
            throw new ValidationException("Insufficient points");
        }
        
        // Deduct points from user
        user.deductPoints(reward.getPointsCost());
        
        // Optionally decrease availability for tracking (but doesn't block redemption)
        if (reward.getAvailability() != null && reward.getAvailability() > 0) {
            reward.decreaseAvailability();
        }
        
        // Create transaction
        RewardTransaction transaction = new RewardTransaction();
        transaction.setUserId(userId);
        transaction.setRewardId(rewardId);
        transaction.setTransactionType(TransactionType.REDEEM);
        transaction.setPoints(reward.getPointsCost());
        transaction.setDescription("Redeemed: " + reward.getName());
        transaction.setStatus(TransactionStatus.COMPLETED);
        
        return transactionRepository.save(transaction);
    }
    
    @Transactional
    public RewardTransaction createTransaction(RewardTransaction transaction) {
        return transactionRepository.save(transaction);
    }
    
    @Transactional
    public void deleteTransaction(Long id) {
        RewardTransaction transaction = getTransactionById(id);
        transactionRepository.delete(transaction);
    }
}

