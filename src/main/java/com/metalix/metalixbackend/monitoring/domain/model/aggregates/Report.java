package com.metalix.metalixbackend.monitoring.domain.model.aggregates;

import com.metalix.metalixbackend.monitoring.domain.model.valueobjects.ReportStatus;
import com.metalix.metalixbackend.monitoring.domain.model.valueobjects.ReportType;
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
@Table(name = "reports")
public class Report extends BaseEntity {
    
    @NotBlank
    @Column(nullable = false)
    private String title;
    
    @Enumerated(EnumType.STRING)
    private ReportType type;
    
    private Long generatedBy;
    
    private LocalDateTime generatedAt = LocalDateTime.now();
    
    private Long municipalityId;
    
    private String dateRange;
    
    @Column(columnDefinition = "TEXT")
    private String summary;
    
    @Column(columnDefinition = "JSON")
    private String metrics;
    
    @Enumerated(EnumType.STRING)
    private ReportStatus status = ReportStatus.GENERATED;
}

