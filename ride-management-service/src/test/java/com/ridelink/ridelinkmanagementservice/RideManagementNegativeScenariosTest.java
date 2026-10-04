package com.ridelink.ridelinkmanagementservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ridelink.ridelinkmanagementservice.controller.RideController;
import com.ridelink.ridelinkmanagementservice.dto.RideRequestDto;
import com.ridelink.ridelinkmanagementservice.exception.BadRequestException;
import com.ridelink.ridelinkmanagementservice.exception.GlobalExceptionHandler;
import com.ridelink.ridelinkmanagementservice.exception.ResourceNotFoundException;
import com.ridelink.ridelinkmanagementservice.model.Ride;
import com.ridelink.ridelinkmanagementservice.model.RideStatus;
import com.ridelink.ridelinkmanagementservice.service.RideService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class RideManagementNegativeScenariosTest {

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private RideService rideService;

    @InjectMocks
    private RideController rideController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(rideController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    private void authenticateAsPassenger(String userId) {
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                userId,
                "token",
                List.of(new SimpleGrantedAuthority("ROLE_PASSENGER"), new SimpleGrantedAuthority("PASSENGER"))
        );
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    private void authenticateAsDriver(String driverId) {
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                driverId,
                "token",
                List.of(new SimpleGrantedAuthority("ROLE_DRIVER"), new SimpleGrantedAuthority("DRIVER"))
        );
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    // ==========================================
    // 1. SECURITY & ROLES (403 Role Mismatch)
    // ==========================================

    @Test
    void driverCannotCreateRide_Returns403() throws Exception {
        authenticateAsDriver("driver123");

        RideRequestDto request = new RideRequestDto("Loc A", "Loc B", "CASH");

        mockMvc.perform(post("/api/rides")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.error").value("Forbidden"))
                .andExpect(jsonPath("$.message").value("Access Denied: Only passengers can create rides"))
                .andExpect(jsonPath("$.timestamp").isNotEmpty());
    }

    @Test
    void passengerCannotAcceptRide_Returns403() throws Exception {
        authenticateAsPassenger("pass123");

        mockMvc.perform(put("/api/rides/ride123/accept"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.error").value("Forbidden"))
                .andExpect(jsonPath("$.message").value("Access Denied: Only drivers can accept rides"))
                .andExpect(jsonPath("$.timestamp").isNotEmpty());
    }

    @Test
    void passengerCannotStartRide_Returns403() throws Exception {
        authenticateAsPassenger("pass123");

        mockMvc.perform(put("/api/rides/ride123/start"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.error").value("Forbidden"))
                .andExpect(jsonPath("$.message").value("Access Denied: Only drivers can start rides"))
                .andExpect(jsonPath("$.timestamp").isNotEmpty());
    }

    @Test
    void passengerCannotCompleteRide_Returns403() throws Exception {
        authenticateAsPassenger("pass123");

        mockMvc.perform(put("/api/rides/ride123/complete"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.error").value("Forbidden"))
                .andExpect(jsonPath("$.message").value("Access Denied: Only drivers can complete rides"))
                .andExpect(jsonPath("$.timestamp").isNotEmpty());
    }

    // ==========================================
    // 2. RESOURCE NOT FOUND (404)
    // ==========================================

    @Test
    void getRideById_NotFound_Returns404() throws Exception {
        authenticateAsPassenger("pass123");
        when(rideService.getRideById("non-existent"))
                .thenThrow(new ResourceNotFoundException("Ride not found with id: non-existent"));

        mockMvc.perform(get("/api/rides/non-existent"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Ride not found with id: non-existent"))
                .andExpect(jsonPath("$.timestamp").isNotEmpty());
    }

    // ==========================================
    // 3. INVALID STATE TRANSITION (400)
    // ==========================================

    @Test
    void cannotAcceptAlreadyCompletedRide_Returns400() throws Exception {
        authenticateAsDriver("driver123");
        when(rideService.acceptRide(eq("ride123"), any()))
                .thenThrow(new BadRequestException("Ride is already completed"));

        mockMvc.perform(put("/api/rides/ride123/accept"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Ride is already completed"))
                .andExpect(jsonPath("$.timestamp").isNotEmpty());
    }

    @Test
    void cannotStartAlreadyCompletedRide_Returns400() throws Exception {
        authenticateAsDriver("driver123");
        when(rideService.startRide("ride123"))
                .thenThrow(new BadRequestException("Ride is already completed"));

        mockMvc.perform(put("/api/rides/ride123/start"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Ride is already completed"))
                .andExpect(jsonPath("$.timestamp").isNotEmpty());
    }

    @Test
    void cannotAcceptAlreadyInProgressRide_Returns400() throws Exception {
        authenticateAsDriver("driver123");
        when(rideService.acceptRide(eq("ride123"), any()))
                .thenThrow(new BadRequestException("Ride is already in progress"));

        mockMvc.perform(put("/api/rides/ride123/accept"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Ride is already in progress"))
                .andExpect(jsonPath("$.timestamp").isNotEmpty());
    }

    @Test
    void cannotStartAlreadyInProgressRide_Returns400() throws Exception {
        authenticateAsDriver("driver123");
        when(rideService.startRide("ride123"))
                .thenThrow(new BadRequestException("Ride is already in progress"));

        mockMvc.perform(put("/api/rides/ride123/start"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Ride is already in progress"))
                .andExpect(jsonPath("$.timestamp").isNotEmpty());
    }

    @Test
    void cannotCompleteCancelledRide_Returns400() throws Exception {
        authenticateAsDriver("driver123");
        when(rideService.completeRide("ride123"))
                .thenThrow(new BadRequestException("Cannot complete a cancelled ride"));

        mockMvc.perform(put("/api/rides/ride123/complete"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Cannot complete a cancelled ride"))
                .andExpect(jsonPath("$.timestamp").isNotEmpty());
    }

    // ==========================================
    // 4. INPUT VALIDATION (400)
    // ==========================================

    @Test
    void createRide_BlankLocations_Returns400() throws Exception {
        authenticateAsPassenger("pass123");
        RideRequestDto request = new RideRequestDto("", "", "CASH");

        mockMvc.perform(post("/api/rides")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").isNotEmpty())
                .andExpect(jsonPath("$.errors.pickupLocation").value("Pickup location is required"))
                .andExpect(jsonPath("$.errors.destination").value("Destination is required"))
                .andExpect(jsonPath("$.timestamp").isNotEmpty());
    }

    // ==========================================
    // 5. POSITIVE VERIFICATION (WITH ROLES)
    // ==========================================

    @Test
    void passengerCanCreateRide_Returns201() throws Exception {
        authenticateAsPassenger("pass123");
        RideRequestDto request = new RideRequestDto("Colombo 03", "Kandy", "CASH");

        Ride createdRide = new Ride();
        createdRide.setId("ride123");
        createdRide.setPassengerId("pass123");
        createdRide.setPickupLocation("Colombo 03");
        createdRide.setDestination("Kandy");
        createdRide.setStatus(RideStatus.REQUESTED);

        when(rideService.createRideRequest(eq("pass123"), eq("Colombo 03"), eq("Kandy"), eq("CASH")))
                .thenReturn(createdRide);

        mockMvc.perform(post("/api/rides")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("ride123"))
                .andExpect(jsonPath("$.pickupLocation").value("Colombo 03"))
                .andExpect(jsonPath("$.destination").value("Kandy"))
                .andExpect(jsonPath("$.status").value("REQUESTED"));
    }
}
