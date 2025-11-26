package com.metalix.metalixbackend.iam.interfaces.rest.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterMunicipalityRequest {
    // Municipality data
    @NotBlank(message = "Municipality name is required")
    private String municipalityName;
    
    @NotBlank(message = "Region is required")
    private String region;
    
    @NotBlank(message = "District is required")
    private String district;
    
    @NotBlank(message = "Municipality address is required")
    private String municipalityAddress;
    
    // Admin account (same email for municipality contact and admin login)
    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;
    
    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;
}

