package com.metalix.metalixbackend.iam;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.metalix.metalixbackend.iam.interfaces.rest.dto.LoginRequest;
import com.metalix.metalixbackend.iam.interfaces.rest.dto.RegisterRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@DisplayName("Authentication Controller Tests")
class AuthenticationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Should register a new citizen successfully")
    void shouldRegisterCitizenSuccessfully() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("newuser@test.com");
        request.setPassword("password123");
        request.setFirstName("Test");
        request.setLastName("User");
        request.setPhone("+51-999-888-777");
        request.setAddress("Av. Test 123");
        request.setCity("Lima");
        request.setZipCode("15001");

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.user.email").value("newuser@test.com"))
                .andExpect(jsonPath("$.user.role").value("CITIZEN"));
    }

    @Test
    @DisplayName("Should login with valid credentials")
    @Sql("/data-sample.sql")
    void shouldLoginWithValidCredentials() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setEmail("admin@metalix.com");
        request.setPassword("password123");

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.user.email").value("admin@metalix.com"))
                .andExpect(jsonPath("$.user.role").value("SYSTEM_ADMIN"));
    }

    @Test
    @DisplayName("Should fail login with invalid credentials")
    void shouldFailLoginWithInvalidCredentials() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setEmail("admin@metalix.com");
        request.setPassword("wrongpassword");

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Should fail registration with duplicate email")
    @Sql("/data-sample.sql")
    void shouldFailRegistrationWithDuplicateEmail() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("admin@metalix.com");
        request.setPassword("password123");
        request.setFirstName("Test");
        request.setLastName("User");
        request.setPhone("+51-999-888-777");
        request.setAddress("Av. Test 123");
        request.setCity("Lima");
        request.setZipCode("15001");

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().is4xxClientError());
    }
}

