package com.ridelink.account_service.controller;

import com.ridelink.account_service.model.Account;
import com.ridelink.account_service.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    @Autowired
    private AccountRepository accountRepository;

    @GetMapping("/health")
    public String healthCheck() {
        return "Account Service is running and connected to MongoDB!";
    }

    @PostMapping("/create")
    public Account createAccount(@RequestBody Account account) {
        return accountRepository.save(account);
    }

    @GetMapping("/all")
    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }
}