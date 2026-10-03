package com.ridelink.account_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ridelink.account_service.dto.DriverAccountResponse;
import com.ridelink.account_service.dto.DriverRegisterRequest;
import com.ridelink.account_service.dto.LoginRequest;
import com.ridelink.account_service.dto.LoginResponse;
import com.ridelink.account_service.model.Role;
import com.ridelink.account_service.service.AccountService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DriverAccountController.class)
@AutoConfigureMockMvc(addFilters = false)
class DriverAccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AccountService accountService;

    @Test
    void shouldRegisterDriverAndReturn201Created() throws Exception {
        DriverRegisterRequest request = DriverRegisterRequest.builder()
                .name("Nimal Perera")
                .email("nimal@example.com")
                .password("Password123")
                .phoneNumber("0771234567")
                .build();

        DriverAccountResponse response = DriverAccountResponse.builder()
                .id("drv-12345")
                .name("Nimal Perera")
                .email("nimal@example.com")
                .phoneNumber("0771234567")
                .role(Role.DRIVER)
                .build();

        when(accountService.registerDriver(any(DriverRegisterRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/accounts/drivers/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("drv-12345"))
                .andExpect(jsonPath("$.name").value("Nimal Perera"))
                .andExpect(jsonPath("$.email").value("nimal@example.com"))
                .andExpect(jsonPath("$.phoneNumber").value("0771234567"))
                .andExpect(jsonPath("$.role").value("DRIVER"));
    }

    @Test
    void shouldLoginDriverAndReturnJwtToken() throws Exception {
        LoginRequest loginRequest = new LoginRequest("nimal@example.com", "Password123");
        LoginResponse loginResponse = new LoginResponse("mock-jwt-driver-token");

        when(accountService.driverLogin(any(LoginRequest.class))).thenReturn(loginResponse);

        mockMvc.perform(post("/api/accounts/drivers/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("mock-jwt-driver-token"));
    }

    @Test
    void shouldGetDriverAccountById() throws Exception {
        DriverAccountResponse response = DriverAccountResponse.builder()
                .id("drv-12345")
                .name("Nimal Perera")
                .email("nimal@example.com")
                .phoneNumber("0771234567")
                .role(Role.DRIVER)
                .build();

        when(accountService.getDriverAccountById("drv-12345")).thenReturn(response);

        mockMvc.perform(get("/api/accounts/drivers/drv-12345"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("drv-12345"))
                .andExpect(jsonPath("$.role").value("DRIVER"));
    }

    @Test
    void shouldRejectDriverRegistrationWithMissingEmailAndPassword() throws Exception {
        DriverRegisterRequest invalidRequest = DriverRegisterRequest.builder()
                .name("Invalid Driver")
                .email("")
                .password("")
                .build();

        mockMvc.perform(post("/api/accounts/drivers/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation Failed"));
    }
}
