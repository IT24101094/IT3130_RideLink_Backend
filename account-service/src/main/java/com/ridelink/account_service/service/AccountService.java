package com.ridelink.account_service.service;

import com.ridelink.account_service.dto.DriverAccountResponse;
import com.ridelink.account_service.dto.DriverRegisterRequest;
import com.ridelink.account_service.dto.LoginRequest;
import com.ridelink.account_service.dto.LoginResponse;
import com.ridelink.account_service.dto.PassengerDto;
import com.ridelink.account_service.exception.InvalidCredentialsException;
import com.ridelink.account_service.exception.ResourceNotFoundException;
import com.ridelink.account_service.model.DriverAccount;
import com.ridelink.account_service.model.Passenger;
import com.ridelink.account_service.model.Role;
import com.ridelink.account_service.repository.DriverAccountRepository;
import com.ridelink.account_service.repository.PassengerRepository;
import com.ridelink.account_service.util.JwtUtil;
import com.ridelink.account_service.client.FarePaymentServiceClient;
import com.ridelink.account_service.client.RideServiceClient;
import com.ridelink.account_service.dto.PassengerProfileResponse;
import com.ridelink.account_service.dto.PaymentHistoryDto;
import com.ridelink.account_service.dto.RideHistoryDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AccountService {

    private static final Logger log = LoggerFactory.getLogger(AccountService.class);

    @Autowired
    private PassengerRepository passengerRepository;

    @Autowired
    private DriverAccountRepository driverAccountRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired(required = false)
    private RideServiceClient rideServiceClient;

    @Autowired(required = false)
    private FarePaymentServiceClient farePaymentServiceClient;

    public AccountService() {
    }

    public AccountService(PassengerRepository passengerRepository,
                          DriverAccountRepository driverAccountRepository,
                          PasswordEncoder passwordEncoder,
                          JwtUtil jwtUtil,
                          RideServiceClient rideServiceClient,
                          FarePaymentServiceClient farePaymentServiceClient) {
        this.passengerRepository = passengerRepository;
        this.driverAccountRepository = driverAccountRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.rideServiceClient = rideServiceClient;
        this.farePaymentServiceClient = farePaymentServiceClient;
    }

    // =========================================================
    // DRIVER AUTHENTICATION & MANAGEMENT
    // =========================================================

    public DriverAccountResponse registerDriver(DriverRegisterRequest request) {
        if (driverAccountRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email is already registered");
        }

        String hashedPassword = passwordEncoder.encode(request.getPassword());

        DriverAccount driverAccount = DriverAccount.builder()
                .id(request.getId())
                .name(request.getName())
                .email(request.getEmail())
                .password(hashedPassword)
                .phoneNumber(request.getPhoneNumber())
                .role(Role.DRIVER)
                .build();

        DriverAccount saved = driverAccountRepository.save(driverAccount);

        return DriverAccountResponse.builder()
                .id(saved.getId())
                .name(saved.getName())
                .email(saved.getEmail())
                .phoneNumber(saved.getPhoneNumber())
                .role(saved.getRole())
                .build();
    }

    public LoginResponse driverLogin(LoginRequest request) {
        DriverAccount driver = driverAccountRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), driver.getPassword())) {
            throw new InvalidCredentialsException("Invalid email or password");
        }

        String token = jwtUtil.generateDriverToken(driver.getEmail(), driver.getId(), "DRIVER");
        return new LoginResponse(token);
    }

    public DriverAccountResponse getDriverAccountById(String id) {
        DriverAccount driver = driverAccountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Driver account not found with id: " + id));

        return DriverAccountResponse.builder()
                .id(driver.getId())
                .name(driver.getName())
                .email(driver.getEmail())
                .phoneNumber(driver.getPhoneNumber())
                .role(driver.getRole())
                .build();
    }

    // =========================================================
    // PASSENGER AUTHENTICATION & MANAGEMENT
    // =========================================================

    public LoginResponse login(LoginRequest request) {
        Passenger passenger = passengerRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), passenger.getPassword())) {
            throw new InvalidCredentialsException("Invalid email or password");
        }

        String token = jwtUtil.generateToken(passenger.getEmail(), passenger.getId(), "ROLE_PASSENGER");
        return new LoginResponse(token);
    }

    public PassengerDto createPassenger(PassengerDto passengerDto) {
        if (passengerDto.getEmail() != null && passengerRepository.existsByEmail(passengerDto.getEmail())) {
            throw new IllegalArgumentException("Email is already registered");
        }

        String hashedPassword = passengerDto.getPassword() != null
                ? passwordEncoder.encode(passengerDto.getPassword())
                : null;

        Passenger passenger = new Passenger(
                null, // Enforce fresh unique ID generation by MongoDB
                passengerDto.getName(),
                passengerDto.getEmail(),
                hashedPassword,
                passengerDto.getPhoneNumber(),
                passengerDto.getNic(),
                passengerDto.getAddress()
        );
        Passenger savedPassenger = passengerRepository.save(passenger);
        return new PassengerDto(
                savedPassenger.getId(),
                savedPassenger.getName(),
                savedPassenger.getEmail(),
                savedPassenger.getPassword(),
                savedPassenger.getPhoneNumber(),
                savedPassenger.getNic(),
                savedPassenger.getAddress()
        );
    }

    public PassengerDto registerPassenger(PassengerDto passengerDto) {
        return createPassenger(passengerDto);
    }

    public PassengerDto getPassengerById(String id) {
        Optional<Passenger> passengerOpt = passengerRepository.findById(id);
        if (passengerOpt.isPresent()) {
            Passenger p = passengerOpt.get();
            return new PassengerDto(
                    p.getId(),
                    p.getName(),
                    p.getEmail(),
                    p.getPassword(),
                    p.getPhoneNumber(),
                    p.getNic(),
                    p.getAddress()
            );
        } else {
            throw new ResourceNotFoundException("Passenger not found with id: " + id);
        }
    }

    public PassengerDto updatePassenger(String id, PassengerDto dto) {
        Passenger existingPassenger = passengerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Passenger not found with id: " + id));

        existingPassenger.setName(dto.getName());
        existingPassenger.setEmail(dto.getEmail());
        existingPassenger.setPhoneNumber(dto.getPhoneNumber());
        existingPassenger.setNic(dto.getNic());
        existingPassenger.setAddress(dto.getAddress());

        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            existingPassenger.setPassword(passwordEncoder.encode(dto.getPassword()));
        }

        Passenger savedPassenger = passengerRepository.save(existingPassenger);
        return new PassengerDto(
                savedPassenger.getId(),
                savedPassenger.getName(),
                savedPassenger.getEmail(),
                savedPassenger.getPassword(),
                savedPassenger.getPhoneNumber(),
                savedPassenger.getNic(),
                savedPassenger.getAddress()
        );
    }

    public void deletePassenger(String id) {
        Passenger existingPassenger = passengerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Passenger not found with id: " + id));
        passengerRepository.delete(existingPassenger);
    }

    // =========================================================
    // PASSENGER PROFILE COMPOSITION
    // =========================================================

    public PassengerProfileResponse getPassengerProfile(String passengerId) {
        Passenger passenger = passengerRepository.findById(passengerId)
                .orElseThrow(() -> new ResourceNotFoundException("Passenger not found with id: " + passengerId));

        List<RideHistoryDto> rideHistory = new ArrayList<>();
        if (rideServiceClient != null) {
            try {
                List<RideHistoryDto> rides = rideServiceClient.getPassengerRideHistory(passengerId);
                if (rides != null) {
                    rideHistory = rides;
                }
            } catch (Exception e) {
                log.warn("Failed to fetch ride history for passenger {}: {}", passengerId, e.getMessage());
            }
        }

        List<PaymentHistoryDto> paymentHistory = new ArrayList<>();
        if (farePaymentServiceClient != null) {
            try {
                List<PaymentHistoryDto> payments = farePaymentServiceClient.getPassengerPaymentHistory(passengerId);
                if (payments != null) {
                    paymentHistory = payments;
                }
            } catch (Exception e) {
                log.warn("Failed to fetch payment history for passenger {}: {}", passengerId, e.getMessage());
            }
        }

        return PassengerProfileResponse.builder()
                .id(passenger.getId())
                .name(passenger.getName())
                .email(passenger.getEmail())
                .phone(passenger.getPhoneNumber())
                .phoneNumber(passenger.getPhoneNumber())
                .nic(passenger.getNic())
                .address(passenger.getAddress())
                .rideHistory(rideHistory)
                .paymentHistory(paymentHistory)
                .build();
    }
}
