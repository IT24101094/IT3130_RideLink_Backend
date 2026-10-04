package com.ridelink.account_service.controller;

import com.ridelink.account_service.dto.LoginRequest;
import com.ridelink.account_service.dto.LoginResponse;
import com.ridelink.account_service.dto.PassengerDto;
import com.ridelink.account_service.dto.PassengerProfileResponse;
import com.ridelink.account_service.dto.PassengerResponseDto;
import com.ridelink.account_service.service.AccountService;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping({"/api/passengers", "/api/accounts/passengers"})
@SecurityRequirement(name = "Bearer Authentication")
public class AccountController {

    @Autowired
    private AccountService accountService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        LoginResponse response = accountService.login(loginRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<PassengerDto> createPassenger(@Valid @RequestBody PassengerDto passengerDto) {
        PassengerDto createdPassenger = accountService.createPassenger(passengerDto);
        return new ResponseEntity<>(createdPassenger, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PassengerResponseDto> getPassenger(@PathVariable String id) {
        PassengerDto passengerDto = accountService.getPassengerById(id);
        return ResponseEntity.ok(PassengerResponseDto.fromDto(passengerDto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PassengerDto> updatePassenger(@PathVariable String id, @Valid @RequestBody PassengerDto passengerDto) {
        PassengerDto updatedPassenger = accountService.updatePassenger(id, passengerDto);
        return ResponseEntity.ok(updatedPassenger);
    }

    @GetMapping("/{passengerId}/profile")
    public ResponseEntity<PassengerProfileResponse> getPassengerProfile(@PathVariable String passengerId) {
        PassengerProfileResponse response = accountService.getPassengerProfile(passengerId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePassenger(@PathVariable String id) {
        accountService.deletePassenger(id);
        return ResponseEntity.noContent().build();
    }
}
