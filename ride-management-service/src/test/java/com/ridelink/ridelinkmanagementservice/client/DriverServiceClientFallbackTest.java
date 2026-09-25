package com.ridelink.ridelinkmanagementservice.client;

import com.ridelink.ridelinkmanagementservice.dto.DriverDto;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class DriverServiceClientFallbackTest {

    private final DriverServiceClientFallback fallback = new DriverServiceClientFallback();

    @Test
    void getDriverDetails_ReturnsMockDriver() {
        DriverDto result = fallback.getDriverDetails("driver-123");

        assertNotNull(result);
        assertEquals("driver-123", result.getId());
        assertEquals("Mock Driver", result.getName());
        assertEquals("LIC-0000", result.getLicenseNumber());
        assertEquals("XXX-0000", result.getVehicleRegistrationNumber());
    }
}
