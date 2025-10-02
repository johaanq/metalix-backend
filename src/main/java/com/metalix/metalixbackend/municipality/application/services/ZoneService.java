package com.metalix.metalixbackend.municipality.application.services;

import com.metalix.metalixbackend.municipality.domain.model.entities.Zone;
import com.metalix.metalixbackend.municipality.domain.model.valueobjects.ZoneType;
import com.metalix.metalixbackend.municipality.domain.repository.ZoneRepository;
import com.metalix.metalixbackend.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ZoneService {
    
    private final ZoneRepository zoneRepository;
    
    public Zone getZoneById(Long id) {
        return zoneRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Zone", id));
    }
    
    public List<Zone> getAllZones() {
        return zoneRepository.findAll();
    }
    
    public List<Zone> getZonesByMunicipality(Long municipalityId) {
        return zoneRepository.findByMunicipalityId(municipalityId);
    }
    
    public List<Zone> getActiveZonesByMunicipality(Long municipalityId) {
        return zoneRepository.findByMunicipalityIdAndIsActive(municipalityId, true);
    }
    
    public List<Zone> getZonesByType(ZoneType type) {
        return zoneRepository.findByType(type);
    }
    
    @Transactional
    public Zone createZone(Zone zone) {
        return zoneRepository.save(zone);
    }
    
    @Transactional
    public Zone updateZone(Long id, Zone updatedZone) {
        Zone zone = getZoneById(id);
        
        if (updatedZone.getName() != null) zone.setName(updatedZone.getName());
        if (updatedZone.getType() != null) zone.setType(updatedZone.getType());
        if (updatedZone.getCoordinates() != null) zone.setCoordinates(updatedZone.getCoordinates());
        if (updatedZone.getCollectionSchedule() != null) zone.setCollectionSchedule(updatedZone.getCollectionSchedule());
        if (updatedZone.getDescription() != null) zone.setDescription(updatedZone.getDescription());
        
        return zoneRepository.save(zone);
    }
    
    @Transactional
    public void deleteZone(Long id) {
        Zone zone = getZoneById(id);
        zoneRepository.delete(zone);
    }
}

