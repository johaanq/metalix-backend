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
@DisplayName("Waste Collector Controller Tests")
class WasteCollectorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtService jwtService;

    private String adminToken;
    private String collectorToken;
    private String userToken;

    @BeforeEach
    void setUp() {
        User adminUser = new User();
        adminUser.setEmail("admin@metalix.com");
        adminUser.setRole(Role.SYSTEM_ADMIN);
        adminToken = jwtService.generateToken(createUserDetails(adminUser));

        User municipalityAdminUser = new User();
        municipalityAdminUser.setEmail("admin.lima@metalix.com");
        municipalityAdminUser.setRole(Role.MUNICIPALITY_ADMIN);
        collectorToken = jwtService.generateToken(createUserDetails(municipalityAdminUser));

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
    @DisplayName("Should get all waste collectors with authentication")
    void shouldGetAllWasteCollectors() throws Exception {
        mockMvc.perform(get("/api/v1/waste-collectors")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @DisplayName("Should get waste collector by ID")
    void shouldGetWasteCollectorById() throws Exception {
        mockMvc.perform(get("/api/v1/waste-collectors/1")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").exists());
    }

    @Test
    @DisplayName("Should get waste collectors by municipality")
    void shouldGetCollectorsByMunicipality() throws Exception {
        mockMvc.perform(get("/api/v1/waste-collectors/municipality/1")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @DisplayName("Should get waste collectors by zone")
    void shouldGetCollectorsByZone() throws Exception {
        mockMvc.perform(get("/api/v1/waste-collectors/zone/1")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @DisplayName("Should get full waste collectors")
    void shouldGetFullCollectors() throws Exception {
        mockMvc.perform(get("/api/v1/waste-collectors/full")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @DisplayName("Should create waste collector with admin role")
    void shouldCreateWasteCollectorWithAdminRole() throws Exception {
        String newCollector = """
                {
                    "name": "Test Collector",
                    "type": "PLASTIC",
                    "location": "Test Location",
                    "municipalityId": 1,
                    "zoneId": 1,
                    "capacity": 100.0,
                    "currentFill": 0.0,
                    "status": "ACTIVE",
                    "sensorId": "SENSOR_TEST"
                }
                """;

        mockMvc.perform(post("/api/v1/waste-collectors")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newCollector))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Test Collector"));
    }

    @Test
    @DisplayName("Should update waste collector with collector role")
    void shouldUpdateWasteCollectorWithCollectorRole() throws Exception {
        String updateData = """
                {
                    "currentFill": 50.0,
                    "status": "ACTIVE"
                }
                """;

        mockMvc.perform(put("/api/v1/waste-collectors/1")
                        .header("Authorization", "Bearer " + collectorToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateData))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Should empty waste collector")
    void shouldEmptyWasteCollector() throws Exception {
        mockMvc.perform(patch("/api/v1/waste-collectors/1/empty")
                        .header("Authorization", "Bearer " + collectorToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.currentFill").value(0.0));
    }

    @Test
    @DisplayName("Should delete waste collector with admin role")
    void shouldDeleteWasteCollectorWithAdminRole() throws Exception {
        mockMvc.perform(delete("/api/v1/waste-collectors/1")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Should fail to delete waste collector without admin role")
    void shouldFailToDeleteWasteCollectorWithoutAdminRole() throws Exception {
        mockMvc.perform(delete("/api/v1/waste-collectors/1")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isForbidden());
    }
}

