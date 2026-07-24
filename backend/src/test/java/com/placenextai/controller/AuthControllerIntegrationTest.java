package com.placenextai.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * End-to-end check of the full register -> login -> /me flow against a real
 * Spring context and an in-memory H2 database (see application-test.properties),
 * exercising JWT issuance and validation the way a real client would.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void studentCanRegisterLoginAndFetchOwnProfile() throws Exception {
        String email = "integration-test-student@example.com";

        String registerBody = objectMapper.writeValueAsString(Map.of(
                "fullName", "Integration Test Student",
                "email", email,
                "password", "password123"
        ));

        mockMvc.perform(post("/api/auth/student/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.role").value("ROLE_STUDENT"))
                .andExpect(jsonPath("$.token").isNotEmpty());

        String loginBody = objectMapper.writeValueAsString(Map.of(
                "email", email,
                "password", "password123"
        ));

        String loginResponse = mockMvc.perform(post("/api/auth/student/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginBody))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        String token = objectMapper.readTree(loginResponse).get("token").asText();

        mockMvc.perform(get("/api/auth/me")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(email))
                .andExpect(jsonPath("$.role").value("ROLE_STUDENT"));
    }

    @Test
    void loginWithWrongPassword_isRejected() throws Exception {
        String email = "wrong-password-student@example.com";
        String registerBody = objectMapper.writeValueAsString(Map.of(
                "fullName", "Wrong Password Student",
                "email", email,
                "password", "password123"
        ));
        mockMvc.perform(post("/api/auth/student/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerBody))
                .andExpect(status().isCreated());

        String badLoginBody = objectMapper.writeValueAsString(Map.of(
                "email", email,
                "password", "totally-wrong-password"
        ));

        mockMvc.perform(post("/api/auth/student/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(badLoginBody))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void accessingProtectedEndpoint_withoutToken_isRejected() throws Exception {
        mockMvc.perform(get("/api/auth/me"))
                .andExpect(status().isUnauthorized());
    }
}
