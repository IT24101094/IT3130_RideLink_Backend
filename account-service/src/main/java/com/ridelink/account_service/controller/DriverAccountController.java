package com.ridelink.account_service.controller;

import com.ridelink.account_service.dto.DriverAccountResponse;
import com.ridelink.account_service.dto.DriverRegisterRequest;
import com.ridelink.account_service.dto.LoginRequest;
import com.ridelink.account_service.dto.LoginResponse;
import com.ridelink.account_service.service.AccountService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping({"/api/accounts/drivers", "/api/drivers/accounts"})
public class DriverAccountController {

    @Autowired
    private AccountService accountService;

    @PostMapping("/register")
    public ResponseEntity<DriverAccountResponse> registerDriver(@Valid @RequestBody DriverRegisterRequest request) {
        DriverAccountResponse response = accountService.registerDriver(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping
    public ResponseEntity<DriverAccountResponse> createDriver(@Valid @RequestBody DriverRegisterRequest request) {
        DriverAccountResponse response = accountService.registerDriver(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> loginDriver(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = accountService.driverLogin(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DriverAccountResponse> getDriverAccount(@PathVariable String id) {
        DriverAccountResponse response = accountService.getDriverAccountById(id);
        return ResponseEntity.ok(response);
    }
}
