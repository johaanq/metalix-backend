package com.metalix.metalixbackend.reward;

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
@DisplayName("Reward Controller Tests")
class RewardControllerTest {

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
        User adminUser = new User();
        adminUser.setEmail("admin.lima@metalix.com");
        adminUser.setRole(Role.MUNICIPALITY_ADMIN);
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
    @DisplayName("Should get all rewards")
    void shouldGetAllRewards() throws Exception {
        mockMvc.perform(get("/api/v1/rewards")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @DisplayName("Should get reward by ID")
    void shouldGetRewardById() throws Exception {
        mockMvc.perform(get("/api/v1/rewards/1")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @DisplayName("Should get available rewards")
    void shouldGetAvailableRewards() throws Exception {
        mockMvc.perform(get("/api/v1/rewards/available")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @DisplayName("Should get rewards by municipality")
    void shouldGetRewardsByMunicipality() throws Exception {
        mockMvc.perform(get("/api/v1/rewards/municipality/1")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @DisplayName("Should create reward with admin role")
    void shouldCreateRewardWithAdminRole() throws Exception {
        String newReward = """
                {
                    "name": "Test Reward",
                    "description": "Test Description",
                    "pointsRequired": 100,
                    "type": "DISCOUNT",
                    "municipalityId": 1,
                    "isAvailable": true,
                    "stock": 50
                }
                """;

        mockMvc.perform(post("/api/v1/rewards")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newReward))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Test Reward"))
                .andExpect(jsonPath("$.pointsRequired").value(100));
    }

    @Test
    @DisplayName("Should fail to create reward without admin role")
    void shouldFailToCreateRewardWithoutAdminRole() throws Exception {
        String newReward = """
                {
                    "name": "Test Reward",
                    "description": "Test Description",
                    "pointsRequired": 100,
                    "type": "DISCOUNT",
                    "municipalityId": 1,
                    "isAvailable": true,
                    "stock": 50
                }
                """;

        mockMvc.perform(post("/api/v1/rewards")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newReward))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Should update reward with admin role")
    void shouldUpdateRewardWithAdminRole() throws Exception {
        String updateData = """
                {
                    "pointsRequired": 150,
                    "stock": 40
                }
                """;

        mockMvc.perform(put("/api/v1/rewards/1")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateData))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Should delete reward with admin role")
    void shouldDeleteRewardWithAdminRole() throws Exception {
        mockMvc.perform(delete("/api/v1/rewards/1")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNoContent());
    }
}

