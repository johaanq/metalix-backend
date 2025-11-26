package com.metalix.metalixbackend.municipality.domain.model.aggregates;

import com.metalix.metalixbackend.shared.domain.model.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "municipalities")
public class Municipality extends BaseEntity {
    
    @NotBlank
    @Column(nullable = false)
    private String name;
    
    @NotBlank
    @Column(unique = true, nullable = false)
    private String code;
    
    private String region;
    
    private Integer population;
    
    private Double area;
    
    @Email
    private String contactEmail;
    
    private String contactPhone;
    
    @Column(nullable = false)
    private Boolean isActive = true;
}

