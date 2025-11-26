package com.metalix.metalixbackend.reward.domain.model.aggregates;

import com.metalix.metalixbackend.reward.domain.model.valueobjects.RewardCategory;
import com.metalix.metalixbackend.shared.domain.model.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "rewards")
public class Reward extends BaseEntity {
    
    @NotBlank
    @Column(nullable = false)
    private String name;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @NotNull
    @Column(nullable = false)
    private Integer pointsCost;
    
    @Enumerated(EnumType.STRING)
    private RewardCategory category;
    
    @Column(nullable = false)
    private Integer availability = 0;
    
    private Long municipalityId;
    
    private String imageUrl;
    
    private LocalDate expirationDate;
    
    @Column(columnDefinition = "TEXT")
    private String termsAndConditions;
    
    @Column(nullable = false)
    private Boolean isActive = true;
    
    public boolean isAvailable() {
        return isActive && availability > 0 && 
               (expirationDate == null || expirationDate.isAfter(LocalDate.now()));
    }
    
    public void decreaseAvailability() {
        if (availability > 0) {
            availability--;
        }
    }
    
    public void increaseAvailability() {
        availability++;
    }
}

