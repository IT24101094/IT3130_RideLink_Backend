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
}
