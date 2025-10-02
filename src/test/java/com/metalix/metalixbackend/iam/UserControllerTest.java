package com.metalix.metalixbackend.iam;

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
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Sql("/data-sample.sql")
@DisplayName("User Controller Tests")
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtService jwtService;

    private String adminToken;
    private String municipalityAdminToken;
    private String userToken;

    @BeforeEach
    void setUp() {
        User systemAdmin = new User();
        systemAdmin.setEmail("admin@metalix.com");
        systemAdmin.setRole(Role.SYSTEM_ADMIN);
        adminToken = jwtService.generateToken(createUserDetails(systemAdmin));

        User municipalityAdmin = new User();
        municipalityAdmin.setEmail("admin.lima@metalix.com");
        municipalityAdmin.setRole(Role.MUNICIPALITY_ADMIN);
        municipalityAdminToken = jwtService.generateToken(createUserDetails(municipalityAdmin));

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
    @DisplayName("Should get all users with admin role")
    void shouldGetAllUsersWithAdminRole() throws Exception {
        mockMvc.perform(get("/api/v1/users?page=0&size=10")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    @DisplayName("Should fail to get all users without admin role")
    void shouldFailToGetAllUsersWithoutAdminRole() throws Exception {
        mockMvc.perform(get("/api/v1/users?page=0&size=10")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Should get user by ID")
    void shouldGetUserById() throws Exception {
        mockMvc.perform(get("/api/v1/users/4")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(4))
                .andExpect(jsonPath("$.email").exists());
    }

    @Test
    @DisplayName("Should get user points")
    void shouldGetUserPoints() throws Exception {
        mockMvc.perform(get("/api/v1/users/4/points")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Should get users by role with admin")
    void shouldGetUsersByRoleWithAdmin() throws Exception {
        mockMvc.perform(get("/api/v1/users/role/CITIZEN?page=0&size=10")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    @DisplayName("Should get users by municipality with admin")
    void shouldGetUsersByMunicipalityWithAdmin() throws Exception {
        mockMvc.perform(get("/api/v1/users/municipality/1?page=0&size=10")
                        .header("Authorization", "Bearer " + municipalityAdminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    @DisplayName("Should update user")
    void shouldUpdateUser() throws Exception {
        String updateData = """
                {
                    "firstName": "María Updated",
                    "lastName": "López",
                    "phone": "+51-999-555-444",
                    "address": "Jr. Las Flores 234",
                    "city": "Lima",
                    "zipCode": "15001"
                }
                """;

        mockMvc.perform(put("/api/v1/users/4")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                        .content(updateData))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("María Updated"));
    }

    @Test
    @DisplayName("Should deactivate user with admin role")
    void shouldDeactivateUserWithAdminRole() throws Exception {
        mockMvc.perform(patch("/api/v1/users/5/deactivate")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isActive").value(false));
    }

    @Test
    @DisplayName("Should delete user with system admin role")
    void shouldDeleteUserWithSystemAdminRole() throws Exception {
        mockMvc.perform(delete("/api/v1/users/5")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Should fail to delete user without admin role")
    void shouldFailToDeleteUserWithoutAdminRole() throws Exception {
        mockMvc.perform(delete("/api/v1/users/5")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isForbidden());
    }
}

