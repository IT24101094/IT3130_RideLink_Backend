package com.ridelink.account_service.controller;

import com.ridelink.account_service.dto.PassengerDto;
import com.ridelink.account_service.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/passengers")
public class AccountController {

    @Autowired
    private AccountService accountService;

    @PostMapping
    public ResponseEntity<PassengerDto> registerPassenger(@RequestBody PassengerDto passengerDto) {
        PassengerDto createdPassenger = accountService.registerPassenger(passengerDto);
        return new ResponseEntity<>(createdPassenger, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PassengerDto> getPassenger(@PathVariable String id) {
        PassengerDto passengerDto = accountService.getPassengerById(id);
        return ResponseEntity.ok(passengerDto);
    }
}
