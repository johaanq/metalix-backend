package com.metalix.metalixbackend.wastecollection.application.services;

import com.metalix.metalixbackend.iam.domain.model.aggregates.User;
import com.metalix.metalixbackend.iam.domain.repository.UserRepository;
import com.metalix.metalixbackend.shared.exception.ResourceNotFoundException;
import com.metalix.metalixbackend.shared.exception.ValidationException;
import com.metalix.metalixbackend.useridentification.domain.repository.RfidCardRepository;
import com.metalix.metalixbackend.wastecollection.domain.model.aggregates.WasteCollector;
import com.metalix.metalixbackend.wastecollection.domain.model.entities.WasteCollection;
import com.metalix.metalixbackend.wastecollection.domain.model.valueobjects.VerificationMethod;
import com.metalix.metalixbackend.wastecollection.domain.repository.WasteCollectionRepository;
import com.metalix.metalixbackend.wastecollection.domain.repository.WasteCollectorRepository;
import com.metalix.metalixbackend.wastecollection.interfaces.rest.dto.IoTCollectionRequest;
import com.metalix.metalixbackend.wastecollection.interfaces.rest.dto.IoTCollectionResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class IoTCollectionService {
    
    private final RfidCardRepository rfidCardRepository;
    private final UserRepository userRepository;
    private final WasteCollectorRepository wasteCollectorRepository;
    private final WasteCollectionRepository wasteCollectionRepository;
    
    /**
     * Registrar colección desde IoT usando ID de bañista
     * - Valida usuario
     * - Suma puntos enviados por el IoT
     * - Suma peso a estadísticas del usuario
     */
    @Transactional
    public IoTCollectionResponse registerCollection(IoTCollectionRequest request) {
        log.info("Processing IoT collection for User ID: {}, Weight: {} kg, Points: {}", 
                request.getUserId(), request.getWeight(), request.getPoints());
        
        // 1. Obtener y validar usuario (bañista)
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User (Bañista)", request.getUserId()));
        
        if (!user.getIsActive()) {
            throw new ValidationException("User account is not active");
        }
        
        // 2. Validar contenedor
        WasteCollector collector = wasteCollectorRepository.findById(request.getCollectorId())
                .orElseThrow(() -> new ResourceNotFoundException("Waste Collector", request.getCollectorId()));
        
        // 3. Crear recolección
        WasteCollection collection = new WasteCollection();
        collection.setUserId(user.getId());
        collection.setCollectorId(collector.getId());
        collection.setWeight(request.getWeight());
        collection.setRecyclableType(request.getRecyclableType());
        collection.setPoints(request.getPoints()); // Usar puntos enviados por IoT
        collection.setTimestamp(LocalDateTime.now());
        collection.setVerified(true);
        collection.setVerificationMethod(VerificationMethod.SENSOR); // Verificado por sensor IoT
        collection.setMunicipalityId(collector.getMunicipalityId());
        collection.setZoneId(collector.getZoneId());
        
        // 4. Guardar recolección
        WasteCollection savedCollection = wasteCollectionRepository.save(collection);
        log.info("Collection saved - ID: {}, Points: {}", savedCollection.getId(), savedCollection.getPoints());
        
        // 5. Sumar puntos al usuario (bañista)
        int previousPoints = user.getTotalPoints();
        user.addPoints(savedCollection.getPoints());
        userRepository.save(user);
        log.info("User {} - Points: {} -> {} (+{})", 
                user.getId(), previousPoints, user.getTotalPoints(), savedCollection.getPoints());
        
        // 6. Buscar si el usuario tiene RFID card para actualizar último uso
        try {
            rfidCardRepository.findByUserId(user.getId()).ifPresent(rfidCard -> {
                rfidCard.use();
                rfidCardRepository.save(rfidCard);
                log.info("RFID card {} marked as used", rfidCard.getCardNumber());
            });
        } catch (Exception e) {
            // Ignorar si hay múltiples tarjetas o error al buscar
            log.warn("Could not update RFID card for user {}: {}", user.getId(), e.getMessage());
        }
        
        // 7. Actualizar nivel de llenado del contenedor
        collector.setCurrentFill(collector.getCurrentFill() + request.getWeight());
        collector.setLastCollection(LocalDateTime.now());
        wasteCollectorRepository.save(collector);
        log.info("Collector {} updated - Fill: {} kg", collector.getId(), collector.getCurrentFill());
        
        // 8. Construir respuesta
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
                .message("Collection completed! +" + savedCollection.getPoints() + 
                        " points awarded to " + user.getFirstName() + ". Total: " + user.getTotalPoints())
                .build();
    }
}
