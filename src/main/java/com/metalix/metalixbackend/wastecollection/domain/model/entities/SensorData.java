package com.metalix.metalixbackend.wastecollection.domain.model.entities;

import com.metalix.metalixbackend.shared.domain.model.AuditableModel;
import com.metalix.metalixbackend.wastecollection.domain.model.valueobjects.SensorStatus;
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
@Table(name = "sensor_data")
public class SensorData extends AuditableModel {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank
    @Column(nullable = false)
    private String sensorId;
    
    @NotNull
    @Column(nullable = false)
    private Long collectorId;
    
    @NotNull
    @Column(nullable = false)
    private Double fillLevel;
    
    private Double temperature;
    
    private Integer batteryLevel;
    
    @Column(nullable = false)
    private LocalDateTime timestamp = LocalDateTime.now();
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SensorStatus status = SensorStatus.ACTIVE;
}

