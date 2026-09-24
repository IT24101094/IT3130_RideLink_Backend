package com.ridelink.ridelinkmanagementservice.service;

import com.ridelink.ridelinkmanagementservice.client.AccountServiceClient;
import com.ridelink.ridelinkmanagementservice.client.DriverServiceClient;
import com.ridelink.ridelinkmanagementservice.dto.DriverDto;
import com.ridelink.ridelinkmanagementservice.dto.PassengerDto;
import com.ridelink.ridelinkmanagementservice.model.Ride;
import com.ridelink.ridelinkmanagementservice.model.RideStatus;
import com.ridelink.ridelinkmanagementservice.repository.RideRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RideServiceTest {

    @Mock
    private RideRepository rideRepository;

    @Mock
    private AccountServiceClient accountServiceClient;

    @Mock
    private DriverServiceClient driverServiceClient;

    @InjectMocks
    private RideService rideService;

    private Ride sampleRide;

    @BeforeEach
    void setUp() {
        sampleRide = new Ride();
        sampleRide.setId("ride123");
        sampleRide.setPassengerId("passenger456");
        sampleRide.setStatus(RideStatus.REQUESTED);
    }

    @Test
    void createRideRequest_Success() {
        PassengerDto mockPassenger = new PassengerDto("passenger456", "John Doe", "123-456-7890");
        when(accountServiceClient.getPassengerDetails("passenger456")).thenReturn(mockPassenger);
        when(rideRepository.save(any(Ride.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Ride created = rideService.createRideRequest("passenger456", "LocA", "LocB");

        assertNotNull(created);
        assertEquals("passenger456", created.getPassengerId());
        assertEquals("LocA", created.getPickupLocation());
        assertEquals("LocB", created.getDestination());
        assertEquals(RideStatus.REQUESTED, created.getStatus());
        assertNotNull(created.getRequestedTime());
        verify(accountServiceClient).getPassengerDetails("passenger456");
        verify(rideRepository).save(any(Ride.class));
    }

    @Test
    void assignDriver_Success() {
        DriverDto mockDriver = new DriverDto("driver789", "Mock Driver", "LIC-0000", "XXX-0000");
        when(driverServiceClient.getDriverDetails("driver789")).thenReturn(mockDriver);
        when(rideRepository.findById("ride123")).thenReturn(Optional.of(sampleRide));
        when(rideRepository.save(any(Ride.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Ride updated = rideService.assignDriver("ride123", "driver789");

        assertEquals(RideStatus.ASSIGNED, updated.getStatus());
        assertEquals("driver789", updated.getDriverId());
        verify(driverServiceClient).getDriverDetails("driver789");
        verify(rideRepository).save(sampleRide);
    }

    @Test
    void acceptRide_Success() {
        DriverDto mockDriver = new DriverDto("driver789", "Mock Driver", "LIC-0000", "XXX-0000");
        when(driverServiceClient.getDriverDetails("driver789")).thenReturn(mockDriver);
        when(rideRepository.findById("ride123")).thenReturn(Optional.of(sampleRide));
        when(rideRepository.save(any(Ride.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Ride updated = rideService.acceptRide("ride123", "driver789");

        assertEquals(RideStatus.ACCEPTED, updated.getStatus());
        assertEquals("driver789", updated.getDriverId());
        verify(driverServiceClient).getDriverDetails("driver789");
        verify(rideRepository).save(sampleRide);
    }

    @Test
    void startRide_Success() {
        when(rideRepository.findById("ride123")).thenReturn(Optional.of(sampleRide));
        when(rideRepository.save(any(Ride.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Ride updated = rideService.startRide("ride123");

        assertEquals(RideStatus.IN_PROGRESS, updated.getStatus());
        verify(rideRepository).save(sampleRide);
    }

    @Test
    void completeRide_Success() {
        when(rideRepository.findById("ride123")).thenReturn(Optional.of(sampleRide));
        when(rideRepository.save(any(Ride.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Ride updated = rideService.completeRide("ride123");

        assertEquals(RideStatus.COMPLETED, updated.getStatus());
        assertNotNull(updated.getCompletedTime());
        verify(rideRepository).save(sampleRide);
    }

    @Test
    void cancelRide_Success() {
        when(rideRepository.findById("ride123")).thenReturn(Optional.of(sampleRide));
        when(rideRepository.save(any(Ride.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Ride updated = rideService.cancelRide("ride123");

        assertEquals(RideStatus.CANCELLED, updated.getStatus());
        verify(rideRepository).save(sampleRide);
    }

    @Test
    void rideNotFound_ThrowsRuntimeException() {
        when(rideRepository.findById("nonexistent")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> rideService.assignDriver("nonexistent", "d1"));
        assertThrows(RuntimeException.class, () -> rideService.acceptRide("nonexistent", "d1"));
        assertThrows(RuntimeException.class, () -> rideService.startRide("nonexistent"));
        assertThrows(RuntimeException.class, () -> rideService.completeRide("nonexistent"));
        assertThrows(RuntimeException.class, () -> rideService.cancelRide("nonexistent"));
    }
}
