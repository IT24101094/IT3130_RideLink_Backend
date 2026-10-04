package com.ridelink.account_service.service;

import com.ridelink.account_service.dto.PassengerDto;
import com.ridelink.account_service.exception.ResourceNotFoundException;
import com.ridelink.account_service.model.Passenger;
import com.ridelink.account_service.repository.PassengerRepository;
import com.ridelink.account_service.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.ridelink.account_service.client.FarePaymentServiceClient;
import com.ridelink.account_service.client.RideServiceClient;
import com.ridelink.account_service.dto.PassengerProfileResponse;
import com.ridelink.account_service.dto.PaymentHistoryDto;
import com.ridelink.account_service.dto.RideHistoryDto;

import java.util.List;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private PassengerRepository passengerRepository;

    @Mock
    private com.ridelink.account_service.repository.DriverAccountRepository driverAccountRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private RideServiceClient rideServiceClient;

    @Mock
    private FarePaymentServiceClient farePaymentServiceClient;

    @InjectMocks
    private AccountService accountService;

    private PassengerDto passengerDto;
    private Passenger passenger;

    @BeforeEach
    void setUp() {
        passengerDto = new PassengerDto(
                "p123",
                "John Doe",
                "john@example.com",
                "plainPassword",
                "0771234567",
                "200012345678",
                "123 Main Street"
        );

        passenger = new Passenger(
                "p123",
                "John Doe",
                "john@example.com",
                "hashedPassword",
                "0771234567",
                "200012345678",
                "123 Main Street"
        );
    }

    @Test
    void shouldCreatePassengerAndHashPassword() {
        when(passwordEncoder.encode("plainPassword")).thenReturn("hashedPassword");
        when(passengerRepository.save(any(Passenger.class))).thenReturn(passenger);

        PassengerDto created = accountService.createPassenger(passengerDto);

        assertNotNull(created);
        assertEquals("p123", created.getId());
        assertEquals("John Doe", created.getName());
        assertEquals("john@example.com", created.getEmail());
        assertEquals("hashedPassword", created.getPassword());
        assertEquals("0771234567", created.getPhoneNumber());
        assertEquals("200012345678", created.getNic());
        assertEquals("123 Main Street", created.getAddress());

        verify(passwordEncoder, times(1)).encode("plainPassword");
        verify(passengerRepository, times(1)).save(any(Passenger.class));
    }

    @Test
    void shouldThrowExceptionWhenCreatingPassengerWithExistingEmail() {
        when(passengerRepository.existsByEmail("john@example.com")).thenReturn(true);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> accountService.createPassenger(passengerDto)
        );

        assertEquals("Email is already registered", exception.getMessage());
        verify(passengerRepository, never()).save(any(Passenger.class));
    }

    @Test
    void shouldGetPassengerByIdSuccessfully() {
        when(passengerRepository.findById("p123")).thenReturn(Optional.of(passenger));

        PassengerDto found = accountService.getPassengerById("p123");

        assertNotNull(found);
        assertEquals("p123", found.getId());
        assertEquals("John Doe", found.getName());
        assertEquals("john@example.com", found.getEmail());
        assertEquals("hashedPassword", found.getPassword());
        verify(passengerRepository, times(1)).findById("p123");
    }

    @Test
    void shouldThrowResourceNotFoundExceptionWhenPassengerNotFound() {
        when(passengerRepository.findById("non-existent")).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> accountService.getPassengerById("non-existent")
        );

        assertEquals("Passenger not found with id: non-existent", exception.getMessage());
        verify(passengerRepository, times(1)).findById("non-existent");
    }

    // =========================================================
    // DRIVER ACCOUNT TESTS
    // =========================================================

    @Test
    void shouldRegisterDriverSuccessfullyWithHashedPasswordAndDriverRole() {
        com.ridelink.account_service.dto.DriverRegisterRequest request =
                new com.ridelink.account_service.dto.DriverRegisterRequest(
                        "d123",
                        "Nimal Driver",
                        "driver@example.com",
                        "rawPassword123",
                        "0779998888"
                );

        when(driverAccountRepository.existsByEmail("driver@example.com")).thenReturn(false);
        when(passwordEncoder.encode("rawPassword123")).thenReturn("encodedPassword123");

        com.ridelink.account_service.model.DriverAccount savedAccount =
                com.ridelink.account_service.model.DriverAccount.builder()
                        .id("d123")
                        .name("Nimal Driver")
                        .email("driver@example.com")
                        .password("encodedPassword123")
                        .phoneNumber("0779998888")
                        .role(com.ridelink.account_service.model.Role.DRIVER)
                        .build();

        when(driverAccountRepository.save(any(com.ridelink.account_service.model.DriverAccount.class)))
                .thenReturn(savedAccount);

        com.ridelink.account_service.dto.DriverAccountResponse response = accountService.registerDriver(request);

        assertNotNull(response);
        assertEquals("d123", response.getId());
        assertEquals("Nimal Driver", response.getName());
        assertEquals("driver@example.com", response.getEmail());
        assertEquals(com.ridelink.account_service.model.Role.DRIVER, response.getRole());

        verify(passwordEncoder, times(1)).encode("rawPassword123");
        verify(driverAccountRepository, times(1)).save(any(com.ridelink.account_service.model.DriverAccount.class));
    }

    @Test
    void shouldThrowExceptionWhenDriverEmailAlreadyRegistered() {
        com.ridelink.account_service.dto.DriverRegisterRequest request =
                new com.ridelink.account_service.dto.DriverRegisterRequest(
                        null,
                        "Nimal Driver",
                        "existing@example.com",
                        "password123",
                        "0779998888"
                );

        when(driverAccountRepository.existsByEmail("existing@example.com")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> accountService.registerDriver(request));
        verify(driverAccountRepository, never()).save(any());
    }

    @Test
    void shouldLoginDriverSuccessfullyAndReturnTokenWithDriverRole() {
        com.ridelink.account_service.dto.LoginRequest loginRequest =
                new com.ridelink.account_service.dto.LoginRequest("driver@example.com", "correctPassword");

        com.ridelink.account_service.model.DriverAccount driver =
                com.ridelink.account_service.model.DriverAccount.builder()
                        .id("d123")
                        .email("driver@example.com")
                        .password("hashedPassword")
                        .role(com.ridelink.account_service.model.Role.DRIVER)
                        .build();

        when(driverAccountRepository.findByEmail("driver@example.com")).thenReturn(Optional.of(driver));
        when(passwordEncoder.matches("correctPassword", "hashedPassword")).thenReturn(true);
        when(jwtUtil.generateDriverToken("driver@example.com", "d123", "DRIVER")).thenReturn("mock-driver-jwt-token");

        com.ridelink.account_service.dto.LoginResponse response = accountService.driverLogin(loginRequest);

        assertNotNull(response);
        assertEquals("mock-driver-jwt-token", response.getToken());

        verify(jwtUtil, times(1)).generateDriverToken("driver@example.com", "d123", "DRIVER");
    }

    @Test
    void shouldThrowInvalidCredentialsWhenDriverNotFound() {
        com.ridelink.account_service.dto.LoginRequest loginRequest =
                new com.ridelink.account_service.dto.LoginRequest("unknown@example.com", "password");

        when(driverAccountRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

        assertThrows(
                com.ridelink.account_service.exception.InvalidCredentialsException.class,
                () -> accountService.driverLogin(loginRequest)
        );
    }

    @Test
    void shouldThrowInvalidCredentialsWhenDriverPasswordMismatch() {
        com.ridelink.account_service.dto.LoginRequest loginRequest =
                new com.ridelink.account_service.dto.LoginRequest("driver@example.com", "wrongPassword");

        com.ridelink.account_service.model.DriverAccount driver =
                com.ridelink.account_service.model.DriverAccount.builder()
                        .id("d123")
                        .email("driver@example.com")
                        .password("hashedPassword")
                        .role(com.ridelink.account_service.model.Role.DRIVER)
                        .build();

        when(driverAccountRepository.findByEmail("driver@example.com")).thenReturn(Optional.of(driver));
        when(passwordEncoder.matches("wrongPassword", "hashedPassword")).thenReturn(false);

        assertThrows(
                com.ridelink.account_service.exception.InvalidCredentialsException.class,
                () -> accountService.driverLogin(loginRequest)
        );
    }

    @Test
    void shouldGetDriverAccountByIdSuccessfully() {
        com.ridelink.account_service.model.DriverAccount driver =
                com.ridelink.account_service.model.DriverAccount.builder()
                        .id("d123")
                        .name("Nimal Driver")
                        .email("driver@example.com")
                        .role(com.ridelink.account_service.model.Role.DRIVER)
                        .build();

        when(driverAccountRepository.findById("d123")).thenReturn(Optional.of(driver));

        com.ridelink.account_service.dto.DriverAccountResponse response = accountService.getDriverAccountById("d123");

        assertNotNull(response);
        assertEquals("d123", response.getId());
        assertEquals("Nimal Driver", response.getName());
        assertEquals("driver@example.com", response.getEmail());
        assertEquals(com.ridelink.account_service.model.Role.DRIVER, response.getRole());
    }

    @Test
    void shouldGetPassengerProfileSuccessfullyWithAggregatedData() {
        when(passengerRepository.findById("p123")).thenReturn(Optional.of(passenger));

        RideHistoryDto ride = RideHistoryDto.builder()
                .id("ride1")
                .passengerId("p123")
                .driverId("d1")
                .pickupLocation("A")
                .destination("B")
                .status("COMPLETED")
                .finalFare(500.0)
                .build();
        when(rideServiceClient.getPassengerRideHistory("p123")).thenReturn(List.of(ride));

        PaymentHistoryDto payment = PaymentHistoryDto.builder()
                .id("pay1")
                .orderId("ord1")
                .rideId("ride1")
                .passengerId("p123")
                .fareAmount(500.0)
                .paymentMethod("CARD")
                .paymentStatus("PAID")
                .build();
        when(farePaymentServiceClient.getPassengerPaymentHistory("p123")).thenReturn(List.of(payment));

        PassengerProfileResponse profile = accountService.getPassengerProfile("p123");

        assertNotNull(profile);
        assertEquals("p123", profile.getId());
        assertEquals("John Doe", profile.getName());
        assertEquals("john@example.com", profile.getEmail());
        assertEquals("0771234567", profile.getPhone());
        assertEquals(1, profile.getRideHistory().size());
        assertEquals("ride1", profile.getRideHistory().get(0).getId());
        assertEquals(1, profile.getPaymentHistory().size());
        assertEquals("pay1", profile.getPaymentHistory().get(0).getId());
    }

    @Test
    void shouldGetPassengerProfileWhenFeignClientsFail() {
        when(passengerRepository.findById("p123")).thenReturn(Optional.of(passenger));
        when(rideServiceClient.getPassengerRideHistory("p123")).thenThrow(new RuntimeException("Ride service down"));
        when(farePaymentServiceClient.getPassengerPaymentHistory("p123")).thenThrow(new RuntimeException("Payment service down"));

        PassengerProfileResponse profile = accountService.getPassengerProfile("p123");

        assertNotNull(profile);
        assertEquals("p123", profile.getId());
        assertTrue(profile.getRideHistory().isEmpty());
        assertTrue(profile.getPaymentHistory().isEmpty());
    }
}
