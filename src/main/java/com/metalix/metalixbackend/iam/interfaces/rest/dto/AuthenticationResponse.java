package com.metalix.metalixbackend.iam.interfaces.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthenticationResponse {
    private String token;
    private Long userId;
    private String email;
    private String role;
}

