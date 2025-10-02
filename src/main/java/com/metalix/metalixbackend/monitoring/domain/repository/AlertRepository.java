package com.metalix.metalixbackend.monitoring.domain.repository;

import com.metalix.metalixbackend.monitoring.domain.model.entities.Alert;
import com.metalix.metalixbackend.monitoring.domain.model.valueobjects.AlertSeverity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlertRepository extends JpaRepository<Alert, Long> {
    List<Alert> findByMunicipalityId(Long municipalityId);
    List<Alert> findByIsRead(Boolean isRead);
    List<Alert> findByMunicipalityIdAndIsRead(Long municipalityId, Boolean isRead);
    List<Alert> findBySeverity(AlertSeverity severity);
    List<Alert> findByActionRequired(Boolean actionRequired);
}

