package com.ridelink.account_service.service;

import com.ridelink.account_service.dto.LoginRequest;
import com.ridelink.account_service.dto.LoginResponse;
import com.ridelink.account_service.dto.PassengerDto;
import com.ridelink.account_service.exception.InvalidCredentialsException;
import com.ridelink.account_service.exception.ResourceNotFoundException;
import com.ridelink.account_service.model.Passenger;
import com.ridelink.account_service.repository.PassengerRepository;
import com.ridelink.account_service.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AccountService {

    @Autowired
    private PassengerRepository passengerRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

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
        String hashedPassword = passengerDto.getPassword() != null
                ? passwordEncoder.encode(passengerDto.getPassword())
                : null;

        Passenger passenger = new Passenger(
                passengerDto.getId(),
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
}
