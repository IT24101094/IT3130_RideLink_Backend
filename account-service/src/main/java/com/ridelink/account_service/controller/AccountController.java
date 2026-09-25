package com.ridelink.account_service.controller;

import com.ridelink.account_service.model.Account;
import com.ridelink.account_service.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    @Autowired
    private AccountService accountService;

    // 1. Register a new account
    @PostMapping("/register")
    public ResponseEntity<Account> registerAccount(@RequestBody Account account) {
        Account savedAccount = accountService.createAccount(account);
        return new ResponseEntity<>(savedAccount, HttpStatus.CREATED);
    }

    // 2. Login and issue token (Mocked for now)
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody Account credentials) {
        Map<String, String> response = new HashMap<>();
        response.put("token", "jwt_mock_token_123");
        response.put("role", credentials.getRole() != null ? credentials.getRole() : "PASSENGER");
        return ResponseEntity.ok(response);
    }

    // 3. View profile
    @GetMapping("/{accountId}")
    public ResponseEntity<Account> getAccount(@PathVariable String accountId) {
        Optional<Account> account = accountService.getAccountById(accountId);
        return account.map(ResponseEntity::ok)
                      .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // 4. Update profile details
    @PutMapping("/{accountId}")
    public ResponseEntity<Account> updateAccount(@PathVariable String accountId, @RequestBody Account updatedAccount) {
        Account account = accountService.updateAccount(accountId, updatedAccount);
        if (account != null) {
            return ResponseEntity.ok(account);
        }
        return ResponseEntity.notFound().build();
    }

    // 5. Update account status
    @PatchMapping("/{accountId}/status")
    public ResponseEntity<Map<String, String>> updateStatus(@PathVariable String accountId, @RequestBody Map<String, String> statusUpdate) {
        boolean updated = accountService.updateStatus(accountId, statusUpdate.get("status"));
        if (updated) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Status updated successfully");
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.notFound().build();
    }
}