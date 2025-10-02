package com.metalix.metalixbackend.municipality.domain.repository;

import com.metalix.metalixbackend.municipality.domain.model.aggregates.Municipality;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MunicipalityRepository extends JpaRepository<Municipality, Long> {
    Optional<Municipality> findByCode(String code);
    boolean existsByCode(String code);
    List<Municipality> findByIsActive(Boolean isActive);
    List<Municipality> findByRegion(String region);
}

