package com.metalix.metalixbackend.wastecollection.domain.repository;

import com.metalix.metalixbackend.wastecollection.domain.model.entities.WasteCollection;
import com.metalix.metalixbackend.wastecollection.domain.model.valueobjects.RecyclableType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface WasteCollectionRepository extends JpaRepository<WasteCollection, Long> {
    List<WasteCollection> findByUserId(Long userId);
    Page<WasteCollection> findByUserId(Long userId, Pageable pageable);
    List<WasteCollection> findByCollectorId(Long collectorId);
    List<WasteCollection> findByMunicipalityId(Long municipalityId);
    Page<WasteCollection> findByMunicipalityId(Long municipalityId, Pageable pageable);
    List<WasteCollection> findByRecyclableType(RecyclableType recyclableType);
    List<WasteCollection> findByTimestampBetween(LocalDateTime start, LocalDateTime end);
    
    @Query("SELECT SUM(w.weight) FROM WasteCollection w WHERE w.userId = :userId")
    Double getTotalWeightByUser(Long userId);
    
    @Query("SELECT SUM(w.points) FROM WasteCollection w WHERE w.userId = :userId")
    Integer getTotalPointsByUser(Long userId);
    
    @Query("SELECT SUM(w.weight) FROM WasteCollection w WHERE w.municipalityId = :municipalityId")
    Double getTotalWeightByMunicipality(Long municipalityId);
}

