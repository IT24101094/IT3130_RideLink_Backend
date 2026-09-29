package com.ridelink.account_service.service;

import com.ridelink.account_service.dto.PassengerDto;
import com.ridelink.account_service.exception.ResourceNotFoundException;
import com.ridelink.account_service.model.Passenger;
import com.ridelink.account_service.repository.PassengerRepository;
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
}
