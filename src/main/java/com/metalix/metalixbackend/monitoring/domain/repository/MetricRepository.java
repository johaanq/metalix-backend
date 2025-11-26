package com.metalix.metalixbackend.monitoring.domain.repository;

import com.metalix.metalixbackend.monitoring.domain.model.entities.Metric;
import com.metalix.metalixbackend.monitoring.domain.model.valueobjects.MetricCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MetricRepository extends JpaRepository<Metric, Long> {
    List<Metric> findByMunicipalityId(Long municipalityId);
    List<Metric> findByCategory(MetricCategory category);
    List<Metric> findByTimestampBetween(LocalDateTime start, LocalDateTime end);
    List<Metric> findByMunicipalityIdAndCategory(Long municipalityId, MetricCategory category);
}

