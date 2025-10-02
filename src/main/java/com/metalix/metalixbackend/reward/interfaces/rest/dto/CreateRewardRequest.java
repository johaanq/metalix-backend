package com.metalix.metalixbackend.reward.interfaces.rest.dto;

import com.metalix.metalixbackend.reward.domain.model.valueobjects.RewardCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CreateRewardRequest {
    @NotBlank(message = "Name is required")
    private String name;
    
    private String description;
    
    @NotNull(message = "Points cost is required")
    @Positive(message = "Points cost must be positive")
    private Integer pointsCost;
    
    private RewardCategory category;
    
    @NotNull(message = "Availability is required")
    @Positive(message = "Availability must be positive")
    private Integer availability;
    
    @NotNull(message = "Municipality ID is required")
    private Long municipalityId;
    
    private String imageUrl;
    
    private LocalDate expirationDate;
    
    private String termsAndConditions;
}
