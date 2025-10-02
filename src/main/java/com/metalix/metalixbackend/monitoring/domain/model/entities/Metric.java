package com.metalix.metalixbackend.monitoring.domain.model.entities;

import com.metalix.metalixbackend.monitoring.domain.model.valueobjects.MetricCategory;
import com.metalix.metalixbackend.shared.domain.model.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
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
@Table(name = "metrics")
public class Metric extends BaseEntity {
    
    @NotBlank
    @Column(nullable = false)
    private String name;
    
    @Column(nullable = false)
    private Double value;
    
    private String unit;
    
    @Enumerated(EnumType.STRING)
    private MetricCategory category;
    
    private Long municipalityId;
    
    @Column(nullable = false)
    private LocalDateTime timestamp = LocalDateTime.now();
    
    private String trend;
}

