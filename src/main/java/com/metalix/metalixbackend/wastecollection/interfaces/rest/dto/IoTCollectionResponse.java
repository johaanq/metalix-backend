package com.metalix.metalixbackend.wastecollection.interfaces.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IoTCollectionResponse {
    
    private Long collectionId;
    private Long userId;
    private String userEmail;
    private String userName;
    private Double weight;
    private Integer pointsEarned;
    private Integer totalUserPoints;
    private String recyclableType;
    private LocalDateTime timestamp;
    private String message;
    private Boolean success;
}

