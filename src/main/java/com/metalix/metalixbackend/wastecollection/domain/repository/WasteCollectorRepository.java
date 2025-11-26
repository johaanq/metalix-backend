package com.metalix.metalixbackend.wastecollection.domain.repository;

import com.metalix.metalixbackend.wastecollection.domain.model.aggregates.WasteCollector;
import com.metalix.metalixbackend.wastecollection.domain.model.valueobjects.CollectorStatus;
import com.metalix.metalixbackend.wastecollection.domain.model.valueobjects.CollectorType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WasteCollectorRepository extends JpaRepository<WasteCollector, Long> {
    List<WasteCollector> findByMunicipalityId(Long municipalityId);
    List<WasteCollector> findByZoneId(Long zoneId);
    List<WasteCollector> findByStatus(CollectorStatus status);
    List<WasteCollector> findByType(CollectorType type);
    List<WasteCollector> findByMunicipalityIdAndStatus(Long municipalityId, CollectorStatus status);
    
    @Query("SELECT w FROM WasteCollector w WHERE (w.currentFill / w.capacity) * 100 >= :threshold")
    List<WasteCollector> findCollectorsAboveThreshold(Double threshold);
}

