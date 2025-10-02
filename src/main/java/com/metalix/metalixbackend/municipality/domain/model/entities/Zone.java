package com.metalix.metalixbackend.municipality.domain.model.entities;

import com.metalix.metalixbackend.municipality.domain.model.valueobjects.ZoneType;
import com.metalix.metalixbackend.shared.domain.model.AuditableModel;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "zones")
public class Zone extends AuditableModel {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank
    @Column(nullable = false)
    private String name;
    
    @NotNull
    @Column(nullable = false)
    private Long municipalityId;
    
    @Enumerated(EnumType.STRING)
    private ZoneType type;
    
    @Column(columnDefinition = "TEXT")
    private String coordinates;
    
    private String collectionSchedule;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(nullable = false)
    private Boolean isActive = true;
}

