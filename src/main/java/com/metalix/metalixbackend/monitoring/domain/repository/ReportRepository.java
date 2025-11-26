package com.metalix.metalixbackend.monitoring.domain.repository;

import com.metalix.metalixbackend.monitoring.domain.model.aggregates.Report;
import com.metalix.metalixbackend.monitoring.domain.model.valueobjects.ReportType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {
    List<Report> findByMunicipalityId(Long municipalityId);
    List<Report> findByType(ReportType type);
    List<Report> findByGeneratedBy(Long userId);
}

