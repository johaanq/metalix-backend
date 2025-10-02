package com.metalix.metalixbackend.municipality.domain.repository;

import com.metalix.metalixbackend.municipality.domain.model.entities.Zone;
import com.metalix.metalixbackend.municipality.domain.model.valueobjects.ZoneType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ZoneRepository extends JpaRepository<Zone, Long> {
    List<Zone> findByMunicipalityId(Long municipalityId);
    List<Zone> findByMunicipalityIdAndIsActive(Long municipalityId, Boolean isActive);
    List<Zone> findByType(ZoneType type);
}

