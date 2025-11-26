package com.metalix.metalixbackend.wastecollection.domain.model.aggregates;

import com.metalix.metalixbackend.shared.domain.model.BaseEntity;
import com.metalix.metalixbackend.wastecollection.domain.model.valueobjects.CollectorStatus;
import com.metalix.metalixbackend.wastecollection.domain.model.valueobjects.CollectorType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
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
@Table(name = "waste_collectors")
public class WasteCollector extends BaseEntity {
    
    @NotBlank
    @Column(nullable = false)
    private String name;
    
    @Enumerated(EnumType.STRING)
    @NotNull
    @Column(nullable = false)
    private CollectorType type;
    
    private String location;
    
    @NotNull
    @Column(nullable = false)
    private Long municipalityId;
    
    private Long zoneId;
    
    private Double capacity;
    
    @Column(nullable = false)
    private Double currentFill = 0.0;
    
    private LocalDateTime lastCollection;
    
    private LocalDateTime nextScheduledCollection;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CollectorStatus status = CollectorStatus.ACTIVE;
    
    private String sensorId;
    
    public Double getFillPercentage() {
        if (capacity == null || capacity == 0) return 0.0;
        return (currentFill / capacity) * 100;
    }
    
    public void updateFillLevel(Double fillLevel) {
        this.currentFill = fillLevel;
    }
    
    public void empty() {
        this.currentFill = 0.0;
        this.lastCollection = LocalDateTime.now();
    }
}

