package com.metalix.metalixbackend.useridentification.domain.repository;

import com.metalix.metalixbackend.useridentification.domain.model.aggregates.RfidCard;
import com.metalix.metalixbackend.useridentification.domain.model.valueobjects.CardStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RfidCardRepository extends JpaRepository<RfidCard, Long> {
    Optional<RfidCard> findByCardNumber(String cardNumber);
    Optional<RfidCard> findByUserId(Long userId);
    List<RfidCard> findByStatus(CardStatus status);
    boolean existsByCardNumber(String cardNumber);
}

