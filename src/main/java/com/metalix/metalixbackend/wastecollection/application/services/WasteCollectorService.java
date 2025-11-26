package com.metalix.metalixbackend.wastecollection.application.services;

import com.metalix.metalixbackend.shared.exception.ResourceNotFoundException;
import com.metalix.metalixbackend.shared.exception.ValidationException;
import com.metalix.metalixbackend.wastecollection.domain.model.aggregates.WasteCollector;
import com.metalix.metalixbackend.wastecollection.domain.model.valueobjects.CollectorStatus;
import com.metalix.metalixbackend.wastecollection.domain.repository.WasteCollectorRepository;
import com.metalix.metalixbackend.wastecollection.interfaces.rest.dto.CreateWasteCollectorRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WasteCollectorService {
    
    private final WasteCollectorRepository wasteCollectorRepository;
    
    public WasteCollector getCollectorById(Long id) {
        return wasteCollectorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("WasteCollector", id));
    }
    
    public List<WasteCollector> getAllCollectors() {
        return wasteCollectorRepository.findAll();
    }
    
    public List<WasteCollector> getCollectorsByMunicipality(Long municipalityId) {
        return wasteCollectorRepository.findByMunicipalityId(municipalityId);
    }
    
    public List<WasteCollector> getCollectorsByZone(Long zoneId) {
        return wasteCollectorRepository.findByZoneId(zoneId);
    }
    
    public List<WasteCollector> getCollectorsByStatus(CollectorStatus status) {
        return wasteCollectorRepository.findByStatus(status);
    }
    
    public List<WasteCollector> getFullCollectors() {
        return wasteCollectorRepository.findCollectorsAboveThreshold(80.0);
    }
    
    @Transactional
    public WasteCollector createCollector(CreateWasteCollectorRequest request) {
        WasteCollector collector = new WasteCollector();
        collector.setName(request.getName());
        collector.setType(request.getTypeOrDefault());
        
        // Handle location - can be String or object
        String locationStr = request.getLocationString();
        if (locationStr == null || locationStr.isEmpty()) {
            throw new ValidationException("Location is required");
        }
        collector.setLocation(locationStr);
        
        collector.setMunicipalityId(request.getMunicipalityId());
        collector.setZoneId(request.getZoneId());
        collector.setCapacity(request.getCapacity());
        collector.setSensorId(request.getSensorId());
        collector.setCurrentFill(0.0);
        collector.setStatus(CollectorStatus.ACTIVE);
        
        return wasteCollectorRepository.save(collector);
    }
    
    @Transactional
    public WasteCollector updateCollector(Long id, WasteCollector updatedCollector) {
        WasteCollector collector = getCollectorById(id);
        
        if (updatedCollector.getName() != null) collector.setName(updatedCollector.getName());
        if (updatedCollector.getType() != null) collector.setType(updatedCollector.getType());
        if (updatedCollector.getLocation() != null) collector.setLocation(updatedCollector.getLocation());
        if (updatedCollector.getCapacity() != null) collector.setCapacity(updatedCollector.getCapacity());
        if (updatedCollector.getCurrentFill() != null) collector.setCurrentFill(updatedCollector.getCurrentFill());
        if (updatedCollector.getStatus() != null) collector.setStatus(updatedCollector.getStatus());
        if (updatedCollector.getNextScheduledCollection() != null) 
            collector.setNextScheduledCollection(updatedCollector.getNextScheduledCollection());
        
        return wasteCollectorRepository.save(collector);
    }
    
    @Transactional
    public WasteCollector updateFillLevel(Long id, Double fillLevel) {
        WasteCollector collector = getCollectorById(id);
        collector.updateFillLevel(fillLevel);
        
        if (collector.getFillPercentage() >= 90) {
            collector.setStatus(CollectorStatus.FULL);
        }
        
        return wasteCollectorRepository.save(collector);
    }
    
    @Transactional
    public WasteCollector emptyCollector(Long id) {
        WasteCollector collector = getCollectorById(id);
        collector.empty();
        collector.setStatus(CollectorStatus.ACTIVE);
        return wasteCollectorRepository.save(collector);
    }
    
    @Transactional
    public void deleteCollector(Long id) {
        WasteCollector collector = getCollectorById(id);
        wasteCollectorRepository.delete(collector);
    }
}

