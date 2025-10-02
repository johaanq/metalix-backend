package com.metalix.metalixbackend.municipality;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.metalix.metalixbackend.iam.application.services.JwtService;
import com.metalix.metalixbackend.iam.domain.model.aggregates.User;
import com.metalix.metalixbackend.iam.domain.model.valueobjects.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Sql("/data-sample.sql")
@DisplayName("Municipality Controller Tests")
class MunicipalityControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtService jwtService;

    private String adminToken;
    private String userToken;

    @BeforeEach
    void setUp() {
        // Generate tokens for testing
        User adminUser = new User();
        adminUser.setEmail("admin@metalix.com");
        adminUser.setRole(Role.SYSTEM_ADMIN);
        adminToken = jwtService.generateToken(createUserDetails(adminUser));

        User normalUser = new User();
        normalUser.setEmail("maria.lopez@email.com");
        normalUser.setRole(Role.CITIZEN);
        userToken = jwtService.generateToken(createUserDetails(normalUser));
    }
    
    private org.springframework.security.core.userdetails.UserDetails createUserDetails(User user) {
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password("password")
                .authorities("ROLE_" + user.getRole().name())
                .build();
    }

    @Test
    @DisplayName("Should get all municipalities without authentication")
    void shouldGetAllMunicipalitiesWithoutAuth() throws Exception {
        mockMvc.perform(get("/api/v1/municipalities"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Should get all municipalities with valid token")
    void shouldGetAllMunicipalitiesWithAuth() throws Exception {
        mockMvc.perform(get("/api/v1/municipalities")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].name").exists());
    }

    @Test
    @DisplayName("Should get municipality by ID")
    void shouldGetMunicipalityById() throws Exception {
        mockMvc.perform(get("/api/v1/municipalities/1")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").exists());
    }

    @Test
    @DisplayName("Should get municipality by code")
    void shouldGetMunicipalityByCode() throws Exception {
        mockMvc.perform(get("/api/v1/municipalities/code/LIM-001")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("LIM-001"));
    }

    @Test
    @DisplayName("Should create municipality with admin role")
    void shouldCreateMunicipalityWithAdminRole() throws Exception {
        String newMunicipality = """
                {
                    "name": "Test Municipality",
                    "code": "TST-001",
                    "region": "Test Region",
                    "population": 100000,
                    "area": 100.0,
                    "contactEmail": "test@test.com",
                    "contactPhone": "+51-999-999-999",
                    "isActive": true
                }
                """;

        mockMvc.perform(post("/api/v1/municipalities")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newMunicipality))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Test Municipality"))
                .andExpect(jsonPath("$.code").value("TST-001"));
    }

    @Test
    @DisplayName("Should fail to create municipality without admin role")
    void shouldFailToCreateMunicipalityWithoutAdminRole() throws Exception {
        String newMunicipality = """
                {
                    "name": "Test Municipality",
                    "code": "TST-001",
                    "region": "Test Region",
                    "population": 100000,
                    "area": 100.0,
                    "contactEmail": "test@test.com",
                    "contactPhone": "+51-999-999-999",
                    "isActive": true
                }
                """;

        mockMvc.perform(post("/api/v1/municipalities")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newMunicipality))
                .andExpect(status().isForbidden());
    }
}

