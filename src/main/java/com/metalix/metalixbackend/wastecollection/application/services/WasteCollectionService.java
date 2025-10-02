package com.metalix.metalixbackend.wastecollection.application.services;

import com.metalix.metalixbackend.iam.application.services.UserService;
import com.metalix.metalixbackend.iam.domain.model.aggregates.User;
import com.metalix.metalixbackend.shared.exception.ResourceNotFoundException;
import com.metalix.metalixbackend.wastecollection.domain.model.entities.WasteCollection;
import com.metalix.metalixbackend.wastecollection.domain.repository.WasteCollectionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WasteCollectionService {
    
    private final WasteCollectionRepository wasteCollectionRepository;
    private final UserService userService;
    
    public WasteCollection getCollectionById(Long id) {
        return wasteCollectionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("WasteCollection", id));
    }
    
    public List<WasteCollection> getAllCollections() {
        return wasteCollectionRepository.findAll();
    }
    
    public List<WasteCollection> getCollectionsByUser(Long userId) {
        return wasteCollectionRepository.findByUserId(userId);
    }
    
    public Page<WasteCollection> getCollectionsByUser(Long userId, Pageable pageable) {
        return wasteCollectionRepository.findByUserId(userId, pageable);
    }
    
    public List<WasteCollection> getCollectionsByCollector(Long collectorId) {
        return wasteCollectionRepository.findByCollectorId(collectorId);
    }
    
    public List<WasteCollection> getCollectionsByMunicipality(Long municipalityId) {
        return wasteCollectionRepository.findByMunicipalityId(municipalityId);
    }
    
    public List<WasteCollection> getCollectionsByDateRange(LocalDateTime start, LocalDateTime end) {
        return wasteCollectionRepository.findByTimestampBetween(start, end);
    }
    
    @Transactional
    public WasteCollection createCollection(WasteCollection collection) {
        collection.calculatePoints();
        WasteCollection saved = wasteCollectionRepository.save(collection);
        
        // Add points to user
        User user = userService.getUserById(collection.getUserId());
        user.addPoints(saved.getPoints());
        
        return saved;
    }
    
    @Transactional
    public WasteCollection updateCollection(Long id, WasteCollection updatedCollection) {
        WasteCollection collection = getCollectionById(id);
        
        if (updatedCollection.getWeight() != null) collection.setWeight(updatedCollection.getWeight());
        if (updatedCollection.getRecyclableType() != null) collection.setRecyclableType(updatedCollection.getRecyclableType());
        if (updatedCollection.getVerified() != null) collection.setVerified(updatedCollection.getVerified());
        if (updatedCollection.getVerificationMethod() != null) 
            collection.setVerificationMethod(updatedCollection.getVerificationMethod());
        
        collection.calculatePoints();
        return wasteCollectionRepository.save(collection);
    }
    
    @Transactional
    public void deleteCollection(Long id) {
        WasteCollection collection = getCollectionById(id);
        wasteCollectionRepository.delete(collection);
    }
    
    public Double getTotalWeightByUser(Long userId) {
        Double total = wasteCollectionRepository.getTotalWeightByUser(userId);
        return total != null ? total : 0.0;
    }
    
    public Integer getTotalPointsByUser(Long userId) {
        Integer total = wasteCollectionRepository.getTotalPointsByUser(userId);
        return total != null ? total : 0;
    }
}

