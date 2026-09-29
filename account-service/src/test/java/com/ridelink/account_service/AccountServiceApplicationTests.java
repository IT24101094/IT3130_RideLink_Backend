package com.ridelink.account_service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AccountServiceApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void contextLoads() {
    }

    @Test
    void testApiDocs() throws Exception {
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/v3/api-docs"))
                .andDo(org.springframework.test.web.servlet.result.MockMvcResultHandlers.print())
                .andExpect(status().isOk());
    }

    @Test
    void shouldRejectInvalidPassengerData() throws Exception {
        String invalidPassengerJson = """
                {
                    "name": "",
                    "email": "not-an-email",
                    "password": "",
                    "phoneNumber": "",
                    "nic": "",
                    "address": ""
                }
                """;

        mockMvc.perform(post("/api/passengers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidPassengerJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation Failed"))
                .andExpect(jsonPath("$.messages.name").value("Name is required"))
                .andExpect(jsonPath("$.messages.email").value("Invalid email format"))
                .andExpect(jsonPath("$.messages.password").value("Password is required"))
                .andExpect(jsonPath("$.messages.phoneNumber").value("Phone number is required"))
                .andExpect(jsonPath("$.messages.nic").value("NIC is required"))
                .andExpect(jsonPath("$.messages.address").value("Address is required"));
    }

    @Test
    void shouldRejectInvalidLoginPayload() throws Exception {
        String invalidLoginJson = """
                {
                    "email": "not-an-email",
                    "password": ""
                }
                """;

        mockMvc.perform(post("/api/passengers/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidLoginJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation Failed"))
                .andExpect(jsonPath("$.messages.email").value("Invalid email format"))
                .andExpect(jsonPath("$.messages.password").value("Password is required"));
    }

    @Test
    void shouldFailLoginWhenUserNotFound() throws Exception {
        String loginJson = """
                {
                    "email": "nonexistent@example.com",
                    "password": "wrongpassword"
                }
                """;

        mockMvc.perform(post("/api/passengers/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Unauthorized"))
                .andExpect(jsonPath("$.message").value("Invalid email or password"));
    }

    @Test
    void shouldLoginSuccessfullyAndReturnJwt() throws Exception {
        String uniqueEmail = "jwtuser" + System.currentTimeMillis() + "@example.com";
        String registerJson = String.format("""
                {
                    "name": "JWT Test User",
                    "email": "%s",
                    "password": "SecurePassword123",
                    "phoneNumber": "0771234567",
                    "nic": "200012345678",
                    "address": "123 Main Street"
                }
                """, uniqueEmail);

        mockMvc.perform(post("/api/passengers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerJson))
                .andExpect(status().isCreated());

        String loginJson = String.format("""
                {
                    "email": "%s",
                    "password": "SecurePassword123"
                }
                """, uniqueEmail);

        mockMvc.perform(post("/api/passengers/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isString());
    }
}
