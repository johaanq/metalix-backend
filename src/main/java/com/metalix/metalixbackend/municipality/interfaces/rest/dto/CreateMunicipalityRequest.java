package com.metalix.metalixbackend.municipality.interfaces.rest.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class CreateMunicipalityRequest {
    @NotBlank(message = "Name is required")
    private String name;
    
    @NotBlank(message = "Code is required")
    private String code;
    
    @NotBlank(message = "Region is required")
    private String region;
    
    @NotBlank(message = "District is required")
    private String district;
    
    @NotNull(message = "Population is required")
    @Positive(message = "Population must be positive")
    private Integer population;
    
    @NotNull(message = "Area is required")
    @Positive(message = "Area must be positive")
    private Double area;
    
    @NotBlank(message = "Address is required")
    private String address;
    
    @NotBlank(message = "Phone is required")
    private String phone;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;
    
    private String website;
}
