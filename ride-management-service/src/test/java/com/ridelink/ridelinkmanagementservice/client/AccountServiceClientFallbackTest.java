package com.ridelink.ridelinkmanagementservice.client;

import com.ridelink.ridelinkmanagementservice.dto.PassengerDto;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class AccountServiceClientFallbackTest {

    private final AccountServiceClientFallback fallback = new AccountServiceClientFallback();

    @Test
    void getPassengerDetails_ReturnsMockPassenger() {
        PassengerDto result = fallback.getPassengerDetails("pass-123");

        assertNotNull(result);
        assertEquals("pass-123", result.getId());
        assertEquals("Mock Passenger", result.getName());
        assertEquals("000-000-0000", result.getPhoneNumber());
    }
}
