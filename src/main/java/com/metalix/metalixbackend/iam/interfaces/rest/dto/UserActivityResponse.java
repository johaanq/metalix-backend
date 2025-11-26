package com.metalix.metalixbackend.iam.interfaces.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserActivityResponse {
    private Long id;
    private String type; // COLLECTION, REWARD_REDEMPTION, POINTS_EARNED
    private String description;
    private Integer points;
    private Double weight;
    private LocalDateTime timestamp;
}

