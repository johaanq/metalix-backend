package com.metalix.metalixbackend.reward.application.services;

import com.metalix.metalixbackend.reward.domain.model.aggregates.Reward;
import com.metalix.metalixbackend.reward.domain.model.valueobjects.RewardCategory;
import com.metalix.metalixbackend.reward.domain.repository.RewardRepository;
import com.metalix.metalixbackend.reward.interfaces.rest.dto.CreateRewardRequest;
import com.metalix.metalixbackend.shared.exception.ResourceNotFoundException;
import com.metalix.metalixbackend.shared.exception.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RewardService {
    
    private final RewardRepository rewardRepository;
    
    public Reward getRewardById(Long id) {
        return rewardRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reward", id));
    }
    
    public List<Reward> getAllRewards() {
        return rewardRepository.findAll();
    }
    
    public List<Reward> getActiveRewards() {
        return rewardRepository.findByIsActive(true);
    }
    
    public List<Reward> getRewardsByCategory(RewardCategory category) {
        return rewardRepository.findByCategory(category);
    }
    
    public List<Reward> getRewardsByMunicipality(Long municipalityId) {
        return rewardRepository.findByMunicipalityId(municipalityId);
    }
    
    public List<Reward> getAffordableRewards(Integer maxPoints) {
        return rewardRepository.findByPointsCostLessThanEqual(maxPoints);
    }
    
    @Transactional
    public Reward createReward(CreateRewardRequest request) {
        Reward reward = new Reward();
        reward.setName(request.getName());
        reward.setDescription(request.getDescription());
        reward.setPointsCost(request.getPointsCost());
        reward.setCategory(request.getCategory());
        reward.setAvailability(request.getAvailability());
        reward.setMunicipalityId(request.getMunicipalityId());
        reward.setImageUrl(request.getImageUrl());
        reward.setExpirationDate(request.getExpirationDate());
        reward.setTermsAndConditions(request.getTermsAndConditions());
        reward.setIsActive(true);
        
        return rewardRepository.save(reward);
    }
    
    @Transactional
    public Reward updateReward(Long id, Reward updatedReward) {
        Reward reward = getRewardById(id);
        
        if (updatedReward.getName() != null) reward.setName(updatedReward.getName());
        if (updatedReward.getDescription() != null) reward.setDescription(updatedReward.getDescription());
        if (updatedReward.getPointsCost() != null) reward.setPointsCost(updatedReward.getPointsCost());
        if (updatedReward.getCategory() != null) reward.setCategory(updatedReward.getCategory());
        if (updatedReward.getAvailability() != null) reward.setAvailability(updatedReward.getAvailability());
        if (updatedReward.getImageUrl() != null) reward.setImageUrl(updatedReward.getImageUrl());
        if (updatedReward.getExpirationDate() != null) reward.setExpirationDate(updatedReward.getExpirationDate());
        if (updatedReward.getTermsAndConditions() != null) reward.setTermsAndConditions(updatedReward.getTermsAndConditions());
        
        return rewardRepository.save(reward);
    }
    
    @Transactional
    public void deleteReward(Long id) {
        Reward reward = getRewardById(id);
        rewardRepository.delete(reward);
    }
    
    @Transactional
    public void decreaseAvailability(Long rewardId) {
        Reward reward = getRewardById(rewardId);
        // Simplified: No validation, just decrease if possible
        reward.decreaseAvailability();
        rewardRepository.save(reward);
    }
}

