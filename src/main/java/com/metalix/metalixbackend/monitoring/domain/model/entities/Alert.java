package com.metalix.metalixbackend.monitoring.domain.model.entities;

import com.metalix.metalixbackend.monitoring.domain.model.valueobjects.AlertSeverity;
import com.metalix.metalixbackend.monitoring.domain.model.valueobjects.AlertType;
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
@Table(name = "alerts")
public class Alert extends BaseEntity {
    
    @NotBlank
    @Column(nullable = false)
    private String title;
    
    @Column(columnDefinition = "TEXT")
    private String message;
    
    @Enumerated(EnumType.STRING)
    private AlertType type;
    
    @Enumerated(EnumType.STRING)
    private AlertSeverity severity;
    
    private String source;
    
    private Long municipalityId;
    
    @Column(nullable = false)
    private LocalDateTime timestamp = LocalDateTime.now();
    
    @Column(nullable = false)
    private Boolean isRead = false;
    
    private LocalDateTime readAt;
    
    @Column(nullable = false)
    private Boolean isResolved = false;
    
    private LocalDateTime resolvedAt;
    
    @Column(nullable = false)
    private Boolean actionRequired = false;
}

