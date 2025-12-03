package com.metalix.metalixbackend.wastecollection.domain.model.entities;

import com.metalix.metalixbackend.shared.domain.model.BaseEntity;
import com.metalix.metalixbackend.wastecollection.domain.model.valueobjects.RecyclableType;
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
@Table(name = "pending_collections")
public class PendingCollection extends BaseEntity {
    
    @NotNull
    @Column(nullable = false)
    private Long collectorId;
    
    @NotNull
    @Column(nullable = false)
    private Double weight;
    
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RecyclableType recyclableType;
    
    @Column(nullable = false)
    private Integer calculatedPoints = 0;
    
    @Column(nullable = false)
    private LocalDateTime timestamp = LocalDateTime.now();
    
    @Column(nullable = false)
    private Boolean completed = false;
    
    private LocalDateTime expiresAt;
    
    @Column(unique = true, nullable = false)
    private String sessionToken; // Token único para esta sesión
    
    public void calculatePoints() {
        if (weight == null || recyclableType == null) {
            this.calculatedPoints = 0;
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
        
        this.calculatedPoints = (int) (basePoints * multiplier);
    }
    
    public boolean isExpired() {
        return expiresAt != null && LocalDateTime.now().isAfter(expiresAt);
    }
}

