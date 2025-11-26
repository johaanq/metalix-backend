package com.metalix.metalixbackend.wastecollection.application.services;

import com.metalix.metalixbackend.shared.exception.ResourceNotFoundException;
import com.metalix.metalixbackend.wastecollection.domain.model.entities.SensorData;
import com.metalix.metalixbackend.wastecollection.domain.repository.SensorDataRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SensorDataService {
    
    private final SensorDataRepository sensorDataRepository;
    private final WasteCollectorService wasteCollectorService;
    
    public SensorData getSensorDataById(Long id) {
        return sensorDataRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("SensorData", id));
    }
    
    public List<SensorData> getAllSensorData() {
        return sensorDataRepository.findAll();
    }
    
    public List<SensorData> getSensorDataBySensorId(String sensorId) {
        return sensorDataRepository.findBySensorId(sensorId);
    }
    
    public List<SensorData> getSensorDataByCollector(Long collectorId) {
        return sensorDataRepository.findByCollectorId(collectorId);
    }
    
    public SensorData getLatestSensorDataByCollector(Long collectorId) {
        return sensorDataRepository.findFirstByCollectorIdOrderByTimestampDesc(collectorId)
                .orElse(null);
    }
    
    public List<SensorData> getSensorDataByDateRange(LocalDateTime start, LocalDateTime end) {
        return sensorDataRepository.findByTimestampBetween(start, end);
    }
    
    @Transactional
    public SensorData createSensorData(SensorData sensorData) {
        SensorData saved = sensorDataRepository.save(sensorData);
        
        // Update collector fill level
        wasteCollectorService.updateFillLevel(sensorData.getCollectorId(), sensorData.getFillLevel());
        
        return saved;
    }
    
    @Transactional
    public void deleteSensorData(Long id) {
        SensorData sensorData = getSensorDataById(id);
        sensorDataRepository.delete(sensorData);
    }
}

