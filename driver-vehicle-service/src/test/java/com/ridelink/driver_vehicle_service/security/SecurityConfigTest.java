package com.ridelink.driver_vehicle_service.security;

import com.ridelink.driver_vehicle_service.config.SecurityConfig;
import com.ridelink.driver_vehicle_service.controller.DriverController;
import com.ridelink.driver_vehicle_service.service.DriverService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DriverController.class)
@Import({SecurityConfig.class, JwtUtil.class, JwtAuthenticationFilter.class})
class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtUtil jwtUtil;

    @MockitoBean
    private DriverService driverService;

    @Test
    void shouldBlockUnauthenticatedPostRequest() throws Exception {
        mockMvc.perform(post("/api/drivers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldBlockPassengerFromPostRequest() throws Exception {
        String passengerToken = jwtUtil.generatePassengerToken("p1", "passenger@example.com");

        mockMvc.perform(post("/api/drivers")
                        .header("Authorization", "Bearer " + passengerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldAllowDriverOnPostRequest() throws Exception {
        String driverToken = jwtUtil.generateDriverToken("d1", "B12345", "Driver John");
        when(driverService.createDriver(any())).thenReturn(new com.ridelink.driver_vehicle_service.dto.DriverResponse());

        mockMvc.perform(post("/api/drivers")
                        .header("Authorization", "Bearer " + driverToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "id": "d1",
                                  "licenseNumber": "B12345",
                                  "serviceArea": "Colombo",
                                  "vehicle": {
                                    "licensePlate": "WP-CAB-1234",
                                    "model": "Toyota Prius",
                                    "vehicleType": "CAR",
                                    "capacity": 4
                                  }
                                }
                                """))
                .andExpect(status().isCreated());
    }

    @Test
    void shouldBlockPassengerFromPatchAvailability() throws Exception {
        String passengerToken = jwtUtil.generatePassengerToken("p1", "passenger@example.com");

        mockMvc.perform(patch("/api/drivers/d1/availability")
                        .header("Authorization", "Bearer " + passengerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"available\": true}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldAllowDriverOnPatchAvailability() throws Exception {
        String driverToken = jwtUtil.generateDriverToken("d1", "B12345", "Driver John");
        when(driverService.updateDriverAvailability(any(), any())).thenReturn(new com.ridelink.driver_vehicle_service.dto.DriverProfileResponse());

        mockMvc.perform(patch("/api/drivers/d1/availability")
                        .header("Authorization", "Bearer " + driverToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"available\": true}"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldAllowPassengerOnGetAvailableDrivers() throws Exception {
        String passengerToken = jwtUtil.generatePassengerToken("p1", "passenger@example.com");
        when(driverService.getAvailableDrivers(any())).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/drivers/available")
                        .header("Authorization", "Bearer " + passengerToken))
                .andExpect(status().isOk());
    }

    @Test
    void shouldAllowDriverOnGetAvailableDrivers() throws Exception {
        String driverToken = jwtUtil.generateDriverToken("d1", "B12345", "Driver John");
        when(driverService.getAvailableDrivers(any())).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/drivers/available")
                        .header("Authorization", "Bearer " + driverToken))
                .andExpect(status().isOk());
    }

    @Test
    void shouldBlockUnauthenticatedGetAvailableDrivers() throws Exception {
        mockMvc.perform(get("/api/drivers/available"))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldAllowPublicSwaggerDocsWithoutAuth() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isNotFound()); // 404 because controller isn't in slice, but NOT 403 Forbidden
    }
}
