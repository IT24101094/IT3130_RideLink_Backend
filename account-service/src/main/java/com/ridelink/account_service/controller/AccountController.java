package com.ridelink.account_service.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AccountController {

    @GetMapping("/api/accounts/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("Account Service is running on Spring Boot");
    }
}