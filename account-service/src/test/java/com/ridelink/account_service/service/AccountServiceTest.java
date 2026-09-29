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

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private PassengerRepository passengerRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

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
}
