package com.ridelink.ridelinkmanagementservice.client;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class FarePaymentServiceClientFallbackTest {

    private final FarePaymentServiceClientFallback fallback = new FarePaymentServiceClientFallback();

    @Test
    void calculateFare_ReturnsMockResponse() {
        String result = fallback.calculateFare("ride-123");

        assertNotNull(result);
        assertEquals("Mock Fare Calculation Triggered", result);
    }
}
