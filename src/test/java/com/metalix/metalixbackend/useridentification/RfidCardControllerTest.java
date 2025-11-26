package com.metalix.metalixbackend.useridentification;

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
@DisplayName("RFID Card Controller Tests")
class RfidCardControllerTest {

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
    @DisplayName("Should get all RFID cards with admin role")
    void shouldGetAllRfidCardsWithAdminRole() throws Exception {
        mockMvc.perform(get("/api/v1/rfid-cards")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @DisplayName("Should fail to get all RFID cards without admin role")
    void shouldFailToGetAllRfidCardsWithoutAdminRole() throws Exception {
        mockMvc.perform(get("/api/v1/rfid-cards")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Should get RFID card by ID")
    void shouldGetRfidCardById() throws Exception {
        mockMvc.perform(get("/api/v1/rfid-cards/1")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @DisplayName("Should get RFID card by card number")
    void shouldGetRfidCardByCardNumber() throws Exception {
        mockMvc.perform(get("/api/v1/rfid-cards/number/RFID001")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cardNumber").value("RFID001"));
    }

    @Test
    @DisplayName("Should get RFID card by user ID")
    void shouldGetRfidCardByUserId() throws Exception {
        mockMvc.perform(get("/api/v1/rfid-cards/user/4")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(4));
    }

    @Test
    @DisplayName("Should create RFID card with admin role")
    void shouldCreateRfidCardWithAdminRole() throws Exception {
        String newCard = """
                {
                    "cardNumber": "RFID999",
                    "status": "ACTIVE",
                    "issuedDate": "2024-10-01",
                    "expirationDate": "2025-12-31"
                }
                """;

        mockMvc.perform(post("/api/v1/rfid-cards")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newCard))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.cardNumber").value("RFID999"));
    }

    @Test
    @DisplayName("Should assign card to user")
    void shouldAssignCardToUser() throws Exception {
        String assignData = """
                {
                    "cardNumber": "RFID003",
                    "userId": 4
                }
                """;

        mockMvc.perform(post("/api/v1/rfid-cards/assign")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(assignData))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cardNumber").value("RFID003"))
                .andExpect(jsonPath("$.userId").value(4));
    }

    @Test
    @DisplayName("Should use RFID card")
    void shouldUseRfidCard() throws Exception {
        mockMvc.perform(post("/api/v1/rfid-cards/use/RFID001")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cardNumber").value("RFID001"));
    }

    @Test
    @DisplayName("Should block RFID card with admin role")
    void shouldBlockRfidCardWithAdminRole() throws Exception {
        mockMvc.perform(patch("/api/v1/rfid-cards/1/block")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("BLOCKED"));
    }

    @Test
    @DisplayName("Should delete RFID card with system admin role")
    void shouldDeleteRfidCardWithSystemAdminRole() throws Exception {
        User systemAdmin = new User();
        systemAdmin.setEmail("admin@metalix.com");
        systemAdmin.setRole(Role.SYSTEM_ADMIN);
        String systemAdminToken = jwtService.generateToken(createUserDetails(systemAdmin));

        mockMvc.perform(delete("/api/v1/rfid-cards/1")
                        .header("Authorization", "Bearer " + systemAdminToken))
                .andExpect(status().isNoContent());
    }
}

