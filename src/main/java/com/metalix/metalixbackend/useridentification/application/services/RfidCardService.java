package com.metalix.metalixbackend.useridentification.application.services;

import com.metalix.metalixbackend.shared.exception.ResourceNotFoundException;
import com.metalix.metalixbackend.shared.exception.ValidationException;
import com.metalix.metalixbackend.useridentification.domain.model.aggregates.RfidCard;
import com.metalix.metalixbackend.useridentification.domain.model.valueobjects.CardStatus;
import com.metalix.metalixbackend.useridentification.domain.repository.RfidCardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RfidCardService {
    
    private final RfidCardRepository rfidCardRepository;
    
    public RfidCard getCardById(Long id) {
        return rfidCardRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("RfidCard", id));
    }
    
    public RfidCard getCardByNumber(String cardNumber) {
        return rfidCardRepository.findByCardNumber(cardNumber)
                .orElseThrow(() -> new ResourceNotFoundException("RFID Card with number " + cardNumber + " not found"));
    }
    
    public RfidCard getCardByUserId(Long userId) {
        return rfidCardRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("RFID Card for user " + userId + " not found"));
    }
    
    public List<RfidCard> getAllCards() {
        return rfidCardRepository.findAll();
    }
    
    public List<RfidCard> getCardsByStatus(CardStatus status) {
        return rfidCardRepository.findByStatus(status);
    }
    
    @Transactional
    public RfidCard createCard(RfidCard card) {
        if (rfidCardRepository.existsByCardNumber(card.getCardNumber())) {
            throw new ValidationException("Card number already exists");
        }
        return rfidCardRepository.save(card);
    }
    
    @Transactional
    public RfidCard assignCardToUser(String cardNumber, Long userId) {
        RfidCard card = getCardByNumber(cardNumber);
        if (card.getUserId() != null) {
            throw new ValidationException("Card is already assigned to a user");
        }
        card.setUserId(userId);
        return rfidCardRepository.save(card);
    }
    
    @Transactional
    public RfidCard updateCard(Long id, RfidCard updatedCard) {
        RfidCard card = getCardById(id);
        
        if (updatedCard.getStatus() != null) card.setStatus(updatedCard.getStatus());
        if (updatedCard.getExpirationDate() != null) card.setExpirationDate(updatedCard.getExpirationDate());
        if (updatedCard.getUserId() != null) card.setUserId(updatedCard.getUserId());
        
        return rfidCardRepository.save(card);
    }
    
    @Transactional
    public RfidCard useCard(String cardNumber) {
        RfidCard card = getCardByNumber(cardNumber);
        if (!card.isValid()) {
            throw new ValidationException("Card is not valid");
        }
        card.use();
        return rfidCardRepository.save(card);
    }
    
    @Transactional
    public RfidCard blockCard(Long id) {
        RfidCard card = getCardById(id);
        card.setStatus(CardStatus.BLOCKED);
        return rfidCardRepository.save(card);
    }
    
    @Transactional
    public void deleteCard(Long id) {
        RfidCard card = getCardById(id);
        rfidCardRepository.delete(card);
    }
}

