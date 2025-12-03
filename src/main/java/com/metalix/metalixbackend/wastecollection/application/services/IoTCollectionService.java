package com.metalix.metalixbackend.wastecollection.application.services;

import com.metalix.metalixbackend.iam.domain.model.aggregates.User;
import com.metalix.metalixbackend.iam.domain.repository.UserRepository;
import com.metalix.metalixbackend.shared.exception.ResourceNotFoundException;
import com.metalix.metalixbackend.shared.exception.ValidationException;
import com.metalix.metalixbackend.useridentification.domain.model.aggregates.RfidCard;
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
     * Registrar colección desde IoT usando RFID
     * - Valida RFID y usuario
     * - Calcula y asigna puntos
     * - Suma peso a estadísticas del usuario
     */
    @Transactional
    public IoTCollectionResponse registerCollection(IoTCollectionRequest request) {
        log.info("Processing IoT collection for RFID: {}, Weight: {} kg, Type: {}", 
                request.getRfidCardNumber(), request.getWeight(), request.getRecyclableType());
        
        // 1. Buscar y validar tarjeta RFID
        RfidCard rfidCard = rfidCardRepository.findByCardNumber(request.getRfidCardNumber())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "RFID Card not found: " + request.getRfidCardNumber()));
        
        if (!rfidCard.isValid()) {
            throw new ValidationException("RFID Card is not valid or expired");
        }
        
        // 2. Verificar que la tarjeta esté vinculada a un usuario
        if (rfidCard.getUserId() == null) {
            throw new ValidationException("RFID Card is not linked to any user. Please contact administrator");
        }
        
        // 3. Obtener usuario vinculado
        User user = userRepository.findById(rfidCard.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User", rfidCard.getUserId()));
        
        if (!user.getIsActive()) {
            throw new ValidationException("User account is not active");
        }
        
        // 4. Validar contenedor
        WasteCollector collector = wasteCollectorRepository.findById(request.getCollectorId())
                .orElseThrow(() -> new ResourceNotFoundException("Waste Collector", request.getCollectorId()));
        
        // 5. Crear recolección
        WasteCollection collection = new WasteCollection();
        collection.setUserId(user.getId());
        collection.setCollectorId(collector.getId());
        collection.setWeight(request.getWeight());
        collection.setRecyclableType(request.getRecyclableType());
        collection.setTimestamp(LocalDateTime.now());
        collection.setVerified(true);
        collection.setVerificationMethod(VerificationMethod.RFID);
        collection.setMunicipalityId(collector.getMunicipalityId());
        collection.setZoneId(collector.getZoneId());
        
        // 6. Calcular puntos basado en peso y tipo de material
        collection.calculatePoints();
        
        // 7. Guardar recolección
        WasteCollection savedCollection = wasteCollectionRepository.save(collection);
        log.info("Collection saved - ID: {}, Points: {}", savedCollection.getId(), savedCollection.getPoints());
        
        // 8. Sumar puntos al usuario
        int previousPoints = user.getTotalPoints();
        user.addPoints(savedCollection.getPoints());
        userRepository.save(user);
        log.info("User {} - Points: {} -> {} (+{})", 
                user.getId(), previousPoints, user.getTotalPoints(), savedCollection.getPoints());
        
        // 9. Actualizar último uso de tarjeta RFID
        rfidCard.use();
        rfidCardRepository.save(rfidCard);
        
        // 10. Actualizar nivel de llenado del contenedor
        collector.setCurrentFill(collector.getCurrentFill() + request.getWeight());
        collector.setLastCollection(LocalDateTime.now());
        wasteCollectorRepository.save(collector);
        log.info("Collector {} updated - Fill: {} kg", collector.getId(), collector.getCurrentFill());
        
        // 11. Construir respuesta
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
