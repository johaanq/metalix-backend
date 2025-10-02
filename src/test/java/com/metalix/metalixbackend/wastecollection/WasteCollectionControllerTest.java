package com.metalix.metalixbackend.wastecollection;

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
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Sql("/data-sample.sql")
@DisplayName("Waste Collection Controller Tests")
class WasteCollectionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtService jwtService;

    private String userToken;
    private String collectorToken;

    @BeforeEach
    void setUp() {
        User normalUser = new User();
        normalUser.setEmail("maria.lopez@email.com");
        normalUser.setRole(Role.CITIZEN);
        userToken = jwtService.generateToken(createUserDetails(normalUser));

        User municipalityAdminUser = new User();
        municipalityAdminUser.setEmail("admin.lima@metalix.com");
        municipalityAdminUser.setRole(Role.MUNICIPALITY_ADMIN);
        collectorToken = jwtService.generateToken(createUserDetails(municipalityAdminUser));
    }
    
    private org.springframework.security.core.userdetails.UserDetails createUserDetails(User user) {
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password("password")
                .authorities("ROLE_" + user.getRole().name())
                .build();
    }

    @Test
    @DisplayName("Should get all waste collections")
    void shouldGetAllWasteCollections() throws Exception {
        mockMvc.perform(get("/api/v1/waste-collections")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @DisplayName("Should get waste collection by ID")
    void shouldGetWasteCollectionById() throws Exception {
        mockMvc.perform(get("/api/v1/waste-collections/1")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @DisplayName("Should get waste collections by user")
    void shouldGetCollectionsByUser() throws Exception {
        mockMvc.perform(get("/api/v1/waste-collections/user/4?page=0&size=10")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    @DisplayName("Should get waste collections by collector")
    void shouldGetCollectionsByCollector() throws Exception {
        mockMvc.perform(get("/api/v1/waste-collections/collector/1")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @DisplayName("Should get waste collections by municipality")
    void shouldGetCollectionsByMunicipality() throws Exception {
        mockMvc.perform(get("/api/v1/waste-collections/municipality/1")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @DisplayName("Should create waste collection")
    void shouldCreateWasteCollection() throws Exception {
        String newCollection = """
                {
                    "userId": 4,
                    "collectorId": 1,
                    "weight": 5.5,
                    "recyclableType": "PLASTIC",
                    "municipalityId": 1,
                    "zoneId": 1,
                    "verified": false
                }
                """;

        mockMvc.perform(post("/api/v1/waste-collections")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newCollection))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userId").value(4))
                .andExpect(jsonPath("$.weight").value(5.5));
    }

    @Test
    @DisplayName("Should update waste collection")
    void shouldUpdateWasteCollection() throws Exception {
        String updateData = """
                {
                    "verified": true,
                    "verificationMethod": "MANUAL"
                }
                """;

        mockMvc.perform(put("/api/v1/waste-collections/1")
                        .header("Authorization", "Bearer " + collectorToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateData))
                .andExpect(status().isOk());
    }
}

