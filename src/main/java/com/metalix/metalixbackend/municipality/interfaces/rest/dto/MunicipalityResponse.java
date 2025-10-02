package com.metalix.metalixbackend.municipality.interfaces.rest.dto;

import com.metalix.metalixbackend.municipality.domain.model.aggregates.Municipality;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MunicipalityResponse {
    private Long id;
    private String name;
    private String code;
    private String region;
    private Integer population;
    private Double area;
    private String contactEmail;
    private String contactPhone;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    public static MunicipalityResponse fromEntity(Municipality municipality) {
        MunicipalityResponse response = new MunicipalityResponse();
        response.setId(municipality.getId());
        response.setName(municipality.getName());
        response.setCode(municipality.getCode());
        response.setRegion(municipality.getRegion());
        response.setPopulation(municipality.getPopulation());
        response.setArea(municipality.getArea());
        response.setContactEmail(municipality.getContactEmail());
        response.setContactPhone(municipality.getContactPhone());
        response.setIsActive(municipality.getIsActive());
        response.setCreatedAt(municipality.getCreatedAt());
        response.setUpdatedAt(municipality.getUpdatedAt());
        return response;
    }
}

