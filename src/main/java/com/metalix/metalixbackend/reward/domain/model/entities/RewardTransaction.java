package com.metalix.metalixbackend.reward.domain.model.entities;

import com.metalix.metalixbackend.reward.domain.model.valueobjects.TransactionStatus;
import com.metalix.metalixbackend.reward.domain.model.valueobjects.TransactionType;
import com.metalix.metalixbackend.shared.domain.model.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "reward_transactions")
public class RewardTransaction extends BaseEntity {
    
    @NotNull
    @Column(nullable = false)
    private Long userId;
    
    private Long rewardId;
    
    @Enumerated(EnumType.STRING)
    @NotNull
    @Column(nullable = false)
    private TransactionType transactionType;
    
    @NotNull
    @Column(nullable = false)
    private Integer points;
    
    private String description;
    
    @Column(nullable = false)
    private LocalDateTime timestamp = LocalDateTime.now();
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionStatus status = TransactionStatus.COMPLETED;
}

