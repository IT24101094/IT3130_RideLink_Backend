package com.ridelink.account_service.service;

import com.ridelink.account_service.dto.PassengerDto;
import com.ridelink.account_service.model.Passenger;
import com.ridelink.account_service.repository.PassengerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AccountService {

    @Autowired
    private PassengerRepository passengerRepository;

    public PassengerDto registerPassenger(PassengerDto passengerDto) {
        Passenger passenger = new Passenger(
                passengerDto.getId(),
                passengerDto.getName(),
                passengerDto.getEmail(),
                passengerDto.getPassword(),
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

    public PassengerDto createPassenger(PassengerDto passengerDto) {
        return registerPassenger(passengerDto);
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
            throw new RuntimeException("Passenger not found with id: " + id);
        }
    }
}
