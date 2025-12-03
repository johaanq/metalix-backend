package com.metalix.metalixbackend.wastecollection.application.services;

import com.metalix.metalixbackend.iam.domain.model.aggregates.User;
import com.metalix.metalixbackend.iam.domain.repository.UserRepository;
import com.metalix.metalixbackend.shared.exception.ResourceNotFoundException;
import com.metalix.metalixbackend.shared.exception.ValidationException;
import com.metalix.metalixbackend.useridentification.domain.model.aggregates.RfidCard;
import com.metalix.metalixbackend.useridentification.domain.repository.RfidCardRepository;
import com.metalix.metalixbackend.wastecollection.domain.model.aggregates.WasteCollector;
import com.metalix.metalixbackend.wastecollection.domain.model.entities.PendingCollection;
import com.metalix.metalixbackend.wastecollection.domain.model.entities.WasteCollection;
import com.metalix.metalixbackend.wastecollection.domain.model.valueobjects.VerificationMethod;
import com.metalix.metalixbackend.wastecollection.domain.repository.PendingCollectionRepository;
import com.metalix.metalixbackend.wastecollection.domain.repository.WasteCollectionRepository;
import com.metalix.metalixbackend.wastecollection.domain.repository.WasteCollectorRepository;
import com.metalix.metalixbackend.wastecollection.interfaces.rest.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class IoTCollectionService {
    
    private final RfidCardRepository rfidCardRepository;
    private final UserRepository userRepository;
    private final WasteCollectorRepository wasteCollectorRepository;
    private final WasteCollectionRepository wasteCollectionRepository;
    private final PendingCollectionRepository pendingCollectionRepository;
    
    private static final int SESSION_EXPIRATION_MINUTES = 5;
    
    /**
     * PASO 1: Pesar el material y calcular puntos (sin usuario aún)
     */
    @Transactional
    public WeighMaterialResponse weighMaterial(WeighMaterialRequest request) {
        log.info("Step 1: Weighing material - Weight: {} kg, Type: {}", request.getWeight(), request.getRecyclableType());
        
        // 1. Validar que el contenedor existe
        WasteCollector collector = wasteCollectorRepository.findById(request.getCollectorId())
                .orElseThrow(() -> new ResourceNotFoundException("Waste Collector", request.getCollectorId()));
        
        // 2. Crear colección pendiente
        PendingCollection pending = new PendingCollection();
        pending.setCollectorId(collector.getId());
        pending.setWeight(request.getWeight());
        pending.setRecyclableType(request.getRecyclableType());
        pending.setSessionToken(UUID.randomUUID().toString());
        pending.setExpiresAt(LocalDateTime.now().plusMinutes(SESSION_EXPIRATION_MINUTES));
        pending.setCompleted(false);
        
        // 3. Calcular puntos
        pending.calculatePoints();
        
        // 4. Guardar colección pendiente
        PendingCollection saved = pendingCollectionRepository.save(pending);
        log.info("Pending collection created with token: {} and {} points calculated", 
                saved.getSessionToken(), saved.getCalculatedPoints());
        
        // 5. Construir respuesta
        return WeighMaterialResponse.builder()
                .sessionToken(saved.getSessionToken())
                .weight(saved.getWeight())
                .recyclableType(saved.getRecyclableType().name())
                .calculatedPoints(saved.getCalculatedPoints())
                .message("Material weighed successfully. " + saved.getCalculatedPoints() + 
                        " points ready to be assigned. Please scan your RFID card.")
                .expiresInSeconds(SESSION_EXPIRATION_MINUTES * 60)
                .build();
    }
    
    /**
     * PASO 2: Confirmar con RFID y asignar puntos al usuario
     */
    @Transactional
    public IoTCollectionResponse confirmWithRfid(ConfirmWithRfidRequest request) {
        log.info("Step 2: Confirming with RFID: {}", request.getRfidCardNumber());
        
        // 1. Buscar colección pendiente
        PendingCollection pending = pendingCollectionRepository.findBySessionToken(request.getSessionToken())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Pending collection not found or expired. Session: " + request.getSessionToken()));
        
        if (pending.getCompleted()) {
            throw new ValidationException("This collection has already been completed");
        }
        
        if (pending.isExpired()) {
            throw new ValidationException("Session expired. Please weigh the material again");
        }
        
        // 2. Buscar y validar tarjeta RFID
        RfidCard rfidCard = rfidCardRepository.findByCardNumber(request.getRfidCardNumber())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "RFID Card not found: " + request.getRfidCardNumber()));
        
        if (!rfidCard.isValid()) {
            throw new ValidationException("RFID Card is not valid or expired");
        }
        
        // 3. Verificar que la tarjeta esté vinculada a un usuario
        if (rfidCard.getUserId() == null) {
            throw new ValidationException("RFID Card is not linked to any user. Please contact an administrator");
        }
        
        // 4. Obtener usuario
        User user = userRepository.findById(rfidCard.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User", rfidCard.getUserId()));
        
        if (!user.getIsActive()) {
            throw new ValidationException("User account is not active");
        }
        
        // 5. Obtener contenedor
        WasteCollector collector = wasteCollectorRepository.findById(pending.getCollectorId())
                .orElseThrow(() -> new ResourceNotFoundException("Waste Collector", pending.getCollectorId()));
        
        // 6. Crear recolección definitiva
        WasteCollection collection = new WasteCollection();
        collection.setUserId(user.getId());
        collection.setCollectorId(collector.getId());
        collection.setWeight(pending.getWeight());
        collection.setRecyclableType(pending.getRecyclableType());
        collection.setPoints(pending.getCalculatedPoints());
        collection.setTimestamp(pending.getTimestamp());
        collection.setVerified(true);
        collection.setVerificationMethod(VerificationMethod.RFID);
        collection.setMunicipalityId(collector.getMunicipalityId());
        collection.setZoneId(collector.getZoneId());
        
        // 7. Guardar recolección
        WasteCollection savedCollection = wasteCollectionRepository.save(collection);
        log.info("Collection saved with ID: {} and {} points", savedCollection.getId(), savedCollection.getPoints());
        
        // 8. Actualizar puntos del usuario
        user.addPoints(savedCollection.getPoints());
        userRepository.save(user);
        log.info("User {} awarded {} points. Total: {}", user.getId(), savedCollection.getPoints(), user.getTotalPoints());
        
        // 9. Actualizar uso de tarjeta RFID
        rfidCard.use();
        rfidCardRepository.save(rfidCard);
        
        // 10. Actualizar contenedor
        collector.setCurrentFill(collector.getCurrentFill() + pending.getWeight());
        collector.setLastCollection(LocalDateTime.now());
        wasteCollectorRepository.save(collector);
        
        // 11. Marcar colección pendiente como completada
        pending.setCompleted(true);
        pendingCollectionRepository.save(pending);
        
        // 12. Construir respuesta
        return IoTCollectionResponse.builder()
                .collectionId(savedCollection.getId())
                .userId(user.getId())
                .userEmail(user.getEmail())
                .userName(user.getFirstName() + " " + user.getLastName())
                .weight(savedCollection.getWeight())
                .pointsEarned(savedCollection.getPoints())
                .totalUserPoints(user.getTotalPoints())
                .recyclableType(savedCollection.getRecyclableType().name())
                .timestamp(savedCollection.getTimestamp())
                .success(true)
                .message("Collection completed successfully! " + savedCollection.getPoints() + 
                        " points awarded to " + user.getFirstName() + " " + user.getLastName())
                .build();
    }
    
    /**
     * Limpiar colecciones pendientes expiradas
     */
    @Transactional
    public void cleanupExpiredPendingCollections() {
        LocalDateTime now = LocalDateTime.now();
        pendingCollectionRepository.deleteByExpiresAtBeforeAndCompletedFalse(now);
        log.info("Cleaned up expired pending collections");
    }
}

