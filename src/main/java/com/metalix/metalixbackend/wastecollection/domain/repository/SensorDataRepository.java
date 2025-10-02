package com.metalix.metalixbackend.wastecollection.domain.repository;

import com.metalix.metalixbackend.wastecollection.domain.model.entities.SensorData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SensorDataRepository extends JpaRepository<SensorData, Long> {
    List<SensorData> findBySensorId(String sensorId);
    List<SensorData> findByCollectorId(Long collectorId);
    List<SensorData> findByTimestampBetween(LocalDateTime start, LocalDateTime end);
    
    @Query("SELECT s FROM SensorData s WHERE s.collectorId = :collectorId ORDER BY s.timestamp DESC")
    List<SensorData> findLatestByCollectorId(Long collectorId);
    
    Optional<SensorData> findFirstByCollectorIdOrderByTimestampDesc(Long collectorId);
}

