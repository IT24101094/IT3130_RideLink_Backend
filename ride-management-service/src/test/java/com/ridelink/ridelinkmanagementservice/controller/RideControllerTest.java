package com.ridelink.ridelinkmanagementservice.controller;

import com.ridelink.ridelinkmanagementservice.dto.DriverStatsDto;
import com.ridelink.ridelinkmanagementservice.model.Ride;
import com.ridelink.ridelinkmanagementservice.model.RideStatus;
import com.ridelink.ridelinkmanagementservice.service.RideService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class RideControllerTest {

    private MockMvc mockMvc;

    @Mock
    private RideService rideService;

    @InjectMocks
    private RideController rideController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(rideController).build();
    }

    @Test
    void assignDriver_ReturnsOk() throws Exception {
        Ride ride = new Ride();
        ride.setId("r1");
        ride.setDriverId("d1");
        ride.setStatus(RideStatus.ASSIGNED);

        when(rideService.assignDriver("r1", "d1")).thenReturn(ride);

        mockMvc.perform(put("/api/rides/r1/assign").param("driverId", "d1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("r1"))
                .andExpect(jsonPath("$.driverId").value("d1"))
                .andExpect(jsonPath("$.status").value("ASSIGNED"));
    }

    @Test
    void acceptRide_ReturnsOk() throws Exception {
        Ride ride = new Ride();
        ride.setId("r1");
        ride.setDriverId("d1");
        ride.setStatus(RideStatus.ACCEPTED);

        when(rideService.acceptRide("r1", "d1")).thenReturn(ride);

        mockMvc.perform(put("/api/rides/r1/accept").param("driverId", "d1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("r1"))
                .andExpect(jsonPath("$.driverId").value("d1"))
                .andExpect(jsonPath("$.status").value("ACCEPTED"));
    }

    @Test
    void startRide_ReturnsOk() throws Exception {
        Ride ride = new Ride();
        ride.setId("r1");
        ride.setStatus(RideStatus.IN_PROGRESS);

        when(rideService.startRide("r1")).thenReturn(ride);

        mockMvc.perform(put("/api/rides/r1/start"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("r1"))
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"));
    }

    @Test
    void completeRide_ReturnsOk() throws Exception {
        Ride ride = new Ride();
        ride.setId("r1");
        ride.setStatus(RideStatus.COMPLETED);
        ride.setCompletedTime(LocalDateTime.now());

        when(rideService.completeRide("r1")).thenReturn(ride);

        mockMvc.perform(put("/api/rides/r1/complete"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("r1"))
                .andExpect(jsonPath("$.status").value("COMPLETED"));
    }

    @Test
    void cancelRide_ReturnsOk() throws Exception {
        Ride ride = new Ride();
        ride.setId("r1");
        ride.setStatus(RideStatus.CANCELLED);

        when(rideService.cancelRide("r1")).thenReturn(ride);

        mockMvc.perform(put("/api/rides/r1/cancel"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("r1"))
                .andExpect(jsonPath("$.status").value("CANCELLED"));
    }

    @Test
    void getPassengerRideHistory_ReturnsList() throws Exception {
        Ride r1 = new Ride();
        r1.setId("r1");
        r1.setPassengerId("p1");
        r1.setStatus(RideStatus.COMPLETED);

        when(rideService.getPassengerRideHistory("p1")).thenReturn(List.of(r1));

        mockMvc.perform(get("/api/rides/passenger/p1/history"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("r1"))
                .andExpect(jsonPath("$[0].passengerId").value("p1"))
                .andExpect(jsonPath("$[0].status").value("COMPLETED"));
    }

    @Test
    void getDriverStats_ReturnsStatsDto() throws Exception {
        DriverStatsDto stats = new DriverStatsDto("d1", 5);
        when(rideService.getDriverStats("d1")).thenReturn(stats);

        mockMvc.perform(get("/api/rides/driver/d1/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.driverId").value("d1"))
                .andExpect(jsonPath("$.totalRides").value(5));
    }
}
