package com.metalix.metalixbackend.reward.domain.repository;

import com.metalix.metalixbackend.reward.domain.model.aggregates.Reward;
import com.metalix.metalixbackend.reward.domain.model.valueobjects.RewardCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RewardRepository extends JpaRepository<Reward, Long> {
    List<Reward> findByIsActive(Boolean isActive);
    List<Reward> findByCategory(RewardCategory category);
    List<Reward> findByMunicipalityId(Long municipalityId);
    List<Reward> findByMunicipalityIdAndIsActive(Long municipalityId, Boolean isActive);
    List<Reward> findByPointsCostLessThanEqual(Integer pointsCost);
}

