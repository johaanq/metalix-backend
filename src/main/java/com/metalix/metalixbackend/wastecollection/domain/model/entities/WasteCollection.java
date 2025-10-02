package com.metalix.metalixbackend.wastecollection.domain.model.entities;

import com.metalix.metalixbackend.shared.domain.model.BaseEntity;
import com.metalix.metalixbackend.wastecollection.domain.model.valueobjects.RecyclableType;
import com.metalix.metalixbackend.wastecollection.domain.model.valueobjects.VerificationMethod;
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
@Table(name = "waste_collections")
public class WasteCollection extends BaseEntity {
    
    @NotNull
    @Column(nullable = false)
    private Long userId;
    
    @NotNull
    @Column(nullable = false)
    private Long collectorId;
    
    @NotNull
    @Column(nullable = false)
    private Double weight;
    
    @Column(nullable = false)
    private LocalDateTime timestamp = LocalDateTime.now();
    
    @Column(nullable = false)
    private Integer points = 0;
    
    @Enumerated(EnumType.STRING)
    private RecyclableType recyclableType;
    
    @Column(nullable = false)
    private Boolean verified = false;
    
    @Enumerated(EnumType.STRING)
    private VerificationMethod verificationMethod;
    
    @NotNull
    @Column(nullable = false)
    private Long municipalityId;
    
    private Long zoneId;
    
    public void calculatePoints() {
        if (weight == null || recyclableType == null) {
            this.points = 0;
            return;
        }
        
        double basePoints = weight * 10;
        
        double multiplier = switch (recyclableType) {
            case PLASTIC -> 1.2;
            case GLASS -> 1.1;
            case METAL -> 1.5;
            case PAPER -> 1.0;
            case ORGANIC -> 0.8;
            case ELECTRONIC -> 2.0;
            case HAZARDOUS -> 2.5;
            default -> 1.0;
        };
        
        this.points = (int) (basePoints * multiplier);
    }
}

