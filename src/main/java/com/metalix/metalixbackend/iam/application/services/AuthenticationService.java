package com.metalix.metalixbackend.iam.application.services;

import com.metalix.metalixbackend.iam.domain.model.aggregates.User;
import com.metalix.metalixbackend.iam.domain.model.valueobjects.Role;
import com.metalix.metalixbackend.iam.domain.repository.UserRepository;
import com.metalix.metalixbackend.iam.interfaces.rest.dto.LoginRequest;
import com.metalix.metalixbackend.iam.interfaces.rest.dto.RegisterRequest;
import com.metalix.metalixbackend.iam.interfaces.rest.dto.RegisterMunicipalityRequest;
import com.metalix.metalixbackend.iam.interfaces.rest.dto.AuthenticationResponse;
import com.metalix.metalixbackend.municipality.application.services.MunicipalityService;
import com.metalix.metalixbackend.municipality.domain.model.aggregates.Municipality;
import com.metalix.metalixbackend.municipality.domain.repository.MunicipalityRepository;
import com.metalix.metalixbackend.municipality.interfaces.rest.dto.CreateMunicipalityRequest;
import com.metalix.metalixbackend.shared.exception.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService userDetailsService;
    private final MunicipalityService municipalityService;
    private final com.metalix.metalixbackend.municipality.domain.repository.MunicipalityRepository municipalityRepository;
    
    @Transactional
    public AuthenticationResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ValidationException("Email already exists");
        }
        
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setRole(request.getRole() != null ? request.getRole() : Role.CITIZEN);
        user.setPhone(request.getPhone());
        user.setAddress(request.getAddress());
        user.setCity(request.getCity());
        user.setZipCode(request.getZipCode());
        user.setMunicipalityId(request.getMunicipalityId());
        user.setIsActive(true);
        user.setTotalPoints(0);
        
        userRepository.save(user);
        
        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("userId", user.getId());
        extraClaims.put("role", user.getRole().name());
        
        String token = jwtService.generateToken(extraClaims, userDetails);
        
        return new AuthenticationResponse(token, user.getId(), user.getEmail(), user.getRole().name());
    }
    
    public AuthenticationResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ValidationException("User not found"));
        
        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("userId", user.getId());
        extraClaims.put("role", user.getRole().name());
        
        String token = jwtService.generateToken(extraClaims, userDetails);
        
        return new AuthenticationResponse(token, user.getId(), user.getEmail(), user.getRole().name());
    }
    
    @Transactional
    public AuthenticationResponse registerMunicipality(RegisterMunicipalityRequest request) {
        // Generate municipality code automatically from name
        String code = generateMunicipalityCode(request.getMunicipalityName());
        
        // Create municipality first
        CreateMunicipalityRequest municipalityRequest = new CreateMunicipalityRequest();
        municipalityRequest.setName(request.getMunicipalityName());
        municipalityRequest.setCode(code);
        municipalityRequest.setRegion(request.getRegion());
        municipalityRequest.setDistrict(request.getDistrict());
        municipalityRequest.setPopulation(0); // Default, can be updated later
        municipalityRequest.setArea(0.0); // Default, can be updated later
        municipalityRequest.setAddress(request.getMunicipalityAddress());
        municipalityRequest.setPhone(""); // Can be updated later in profile
        municipalityRequest.setEmail(request.getEmail()); // Use admin email as municipality contact email
        municipalityRequest.setWebsite(null); // Can be added later
        
        Municipality municipality = municipalityService.createMunicipality(municipalityRequest);
        
        // Check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ValidationException("Email already exists");
        }
        
        // Create admin user for the municipality (institutional account)
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFirstName("Administrador");
        user.setLastName(municipality.getName());
        user.setRole(Role.MUNICIPALITY_ADMIN);
        user.setPhone(municipality.getContactPhone());
        user.setAddress(municipality.getContactEmail()); // Store municipality email in address field
        user.setCity(municipality.getRegion());
        user.setMunicipalityId(municipality.getId());
        user.setIsActive(true);
        user.setTotalPoints(0);
        
        userRepository.save(user);
        
        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("userId", user.getId());
        extraClaims.put("role", user.getRole().name());
        
        String token = jwtService.generateToken(extraClaims, userDetails);
        
        return new AuthenticationResponse(token, user.getId(), user.getEmail(), user.getRole().name());
    }
    
    private String generateMunicipalityCode(String name) {
        // Generate code from municipality name: take first 3-6 letters
        String normalized = name.toUpperCase()
                .replaceAll("[^A-Z0-9]", "")
                .replaceAll("\\s+", "");
        
        if (normalized.length() < 3) {
            normalized = normalized + "000".substring(0, 3 - normalized.length());
        }
        
        String baseCode = normalized.substring(0, Math.min(6, normalized.length()));
        
        // Add timestamp suffix to ensure uniqueness (last 4 digits)
        String suffix = String.valueOf(System.currentTimeMillis()).substring(9);
        
        String code = baseCode + suffix;
        
        // Check if code already exists, if so add more digits
        int attempts = 0;
        while (municipalityRepository.existsByCode(code) && attempts < 10) {
            suffix = String.valueOf(System.currentTimeMillis() + attempts).substring(8);
            code = baseCode + suffix;
            attempts++;
        }
        
        return code;
    }
}

