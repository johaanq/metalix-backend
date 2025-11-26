package com.metalix.metalixbackend.iam.interfaces.rest.dto;

import com.metalix.metalixbackend.iam.domain.model.aggregates.User;
import com.metalix.metalixbackend.iam.domain.model.valueobjects.Role;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserResponse {
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private Role role;
    private Long municipalityId;
    private String phone;
    private String address;
    private String city;
    private String zipCode;
    private Boolean isActive;
    private String rfidCard;
    private Integer totalPoints;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    public static UserResponse fromEntity(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setEmail(user.getEmail());
        response.setFirstName(user.getFirstName());
        response.setLastName(user.getLastName());
        response.setRole(user.getRole());
        response.setMunicipalityId(user.getMunicipalityId());
        response.setPhone(user.getPhone());
        response.setAddress(user.getAddress());
        response.setCity(user.getCity());
        response.setZipCode(user.getZipCode());
        response.setIsActive(user.getIsActive());
        response.setRfidCard(user.getRfidCard());
        response.setTotalPoints(user.getTotalPoints());
        response.setCreatedAt(user.getCreatedAt());
        response.setUpdatedAt(user.getUpdatedAt());
        return response;
    }
}

