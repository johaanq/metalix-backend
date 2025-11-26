package com.metalix.metalixbackend.reward.domain.repository;

import com.metalix.metalixbackend.reward.domain.model.entities.RewardTransaction;
import com.metalix.metalixbackend.reward.domain.model.valueobjects.TransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RewardTransactionRepository extends JpaRepository<RewardTransaction, Long> {
    List<RewardTransaction> findByUserId(Long userId);
    Page<RewardTransaction> findByUserId(Long userId, Pageable pageable);
    List<RewardTransaction> findByRewardId(Long rewardId);
    List<RewardTransaction> findByTransactionType(TransactionType transactionType);
    List<RewardTransaction> findByTimestampBetween(LocalDateTime start, LocalDateTime end);
    
    @Query("SELECT SUM(t.points) FROM RewardTransaction t WHERE t.userId = :userId AND t.transactionType = :type")
    Integer getTotalPointsByUserAndType(Long userId, TransactionType type);
}

