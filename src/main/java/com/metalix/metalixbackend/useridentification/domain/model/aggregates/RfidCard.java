package com.metalix.metalixbackend.useridentification.domain.model.aggregates;

import com.metalix.metalixbackend.shared.domain.model.BaseEntity;
import com.metalix.metalixbackend.useridentification.domain.model.valueobjects.CardStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "rfid_cards")
public class RfidCard extends BaseEntity {
    
    @NotBlank
    @Column(unique = true, nullable = false)
    private String cardNumber;
    
    private Long userId;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CardStatus status = CardStatus.ACTIVE;
    
    @Column(nullable = false)
    private LocalDate issuedDate = LocalDate.now();
    
    private LocalDate expirationDate;
    
    private LocalDateTime lastUsed;
    
    public boolean isValid() {
        return status == CardStatus.ACTIVE && 
               (expirationDate == null || expirationDate.isAfter(LocalDate.now()));
    }
    
    public void use() {
        this.lastUsed = LocalDateTime.now();
    }
}

