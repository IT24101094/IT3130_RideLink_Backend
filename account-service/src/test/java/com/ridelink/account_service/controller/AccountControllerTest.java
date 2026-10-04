package com.ridelink.account_service.controller;

import com.ridelink.account_service.dto.PassengerDto;
import com.ridelink.account_service.service.AccountService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AccountController.class)
@AutoConfigureMockMvc(addFilters = false)
class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AccountService accountService;

    @Test
    void shouldGetPassengerByIdAndReturn200Ok() throws Exception {
        PassengerDto passengerDto = new PassengerDto(
                "p123",
                "John Doe",
                "john@example.com",
                "hashedPassword",
                "0771234567",
                "200012345678",
                "123 Main Street"
        );

        when(accountService.getPassengerById("p123")).thenReturn(passengerDto);

        mockMvc.perform(get("/api/passengers/p123")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("p123"))
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andExpect(jsonPath("$.email").value("john@example.com"))
                .andExpect(jsonPath("$.phoneNumber").value("0771234567"))
                .andExpect(jsonPath("$.nic").value("200012345678"))
                .andExpect(jsonPath("$.address").value("123 Main Street"));
    }

    @Test
    void shouldGetPassengerProfileAndReturn200Ok() throws Exception {
        com.ridelink.account_service.dto.PassengerProfileResponse profileResponse =
                com.ridelink.account_service.dto.PassengerProfileResponse.builder()
                        .id("p123")
                        .name("John Doe")
                        .email("john@example.com")
                        .phone("0771234567")
                        .rideHistory(java.util.List.of())
                        .paymentHistory(java.util.List.of())
                        .build();

        when(accountService.getPassengerProfile("p123")).thenReturn(profileResponse);

        mockMvc.perform(get("/api/accounts/passengers/p123/profile")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("p123"))
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andExpect(jsonPath("$.email").value("john@example.com"))
                .andExpect(jsonPath("$.phone").value("0771234567"));
    }
}
