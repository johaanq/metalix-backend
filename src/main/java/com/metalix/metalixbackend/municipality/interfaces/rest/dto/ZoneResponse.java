package com.metalix.metalixbackend.municipality.interfaces.rest.dto;

import com.metalix.metalixbackend.municipality.domain.model.entities.Zone;
import com.metalix.metalixbackend.municipality.domain.model.valueobjects.ZoneType;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ZoneResponse {
    private Long id;
    private String name;
    private Long municipalityId;
    private ZoneType type;
    private String coordinates;
    private String collectionSchedule;
    private String description;
    private Boolean isActive;
    private LocalDateTime createdAt;
    
    public static ZoneResponse fromEntity(Zone zone) {
        ZoneResponse response = new ZoneResponse();
        response.setId(zone.getId());
        response.setName(zone.getName());
        response.setMunicipalityId(zone.getMunicipalityId());
        response.setType(zone.getType());
        response.setCoordinates(zone.getCoordinates());
        response.setCollectionSchedule(zone.getCollectionSchedule());
        response.setDescription(zone.getDescription());
        response.setIsActive(zone.getIsActive());
        response.setCreatedAt(zone.getCreatedAt());
        return response;
    }
}

