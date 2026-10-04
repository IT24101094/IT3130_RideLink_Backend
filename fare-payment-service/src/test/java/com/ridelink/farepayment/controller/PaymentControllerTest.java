package com.ridelink.farepayment.controller;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.ridelink.farepayment.dto.DriverPaymentStatsDto;
import com.ridelink.farepayment.model.Payment;
import com.ridelink.farepayment.service.PaymentService;

@ExtendWith(MockitoExtension.class)
class PaymentControllerTest {

    private MockMvc mockMvc;

    @Mock
    private PaymentService paymentService;

    @InjectMocks
    private PaymentController paymentController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(paymentController).build();
    }

    @Test
    void getPassengerPaymentHistory_returnsList() throws Exception {
        Payment p1 = new Payment("ORD1", "R1", "P1", 500.0, "CASH", "PAID");
        when(paymentService.getPassengerPaymentHistory("P1")).thenReturn(List.of(p1));

        mockMvc.perform(get("/api/payments/passenger/P1/history"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].orderId").value("ORD1"))
                .andExpect(jsonPath("$[0].passengerId").value("P1"))
                .andExpect(jsonPath("$[0].fareAmount").value(500.0))
                .andExpect(jsonPath("$[0].paymentMethod").value("CASH"));
    }

    @Test
    void getDriverStats_returnsStatsDto() throws Exception {
        DriverPaymentStatsDto stats = new DriverPaymentStatsDto("D1", 1500.0, 700.0, 800.0);
        when(paymentService.getDriverStats("D1")).thenReturn(stats);

        mockMvc.perform(get("/api/payments/driver/D1/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.driverId").value("D1"))
                .andExpect(jsonPath("$.totalEarnings").value(1500.0))
                .andExpect(jsonPath("$.cashTotal").value(700.0))
                .andExpect(jsonPath("$.cardTotal").value(800.0));
    }

    @Test
    void getPaymentStatus_returnsStatus() throws Exception {
        when(paymentService.getPaymentStatus("PAY123")).thenReturn("PAID");

        mockMvc.perform(get("/api/payments/PAY123/status"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("PAID"));
    }
}
