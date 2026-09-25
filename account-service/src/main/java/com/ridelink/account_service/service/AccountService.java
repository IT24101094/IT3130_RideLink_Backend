package com.ridelink.account_service.service;

import com.ridelink.account_service.model.Account;
import com.ridelink.account_service.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;

    // 1. Create account
    public Account createAccount(Account account) {
        if (account.getStatus() == null) {
            account.setStatus("ACTIVE"); // Default status
        }
        return accountRepository.save(account);
    }

    // 3. View profile
    public Optional<Account> getAccountById(String id) {
        return accountRepository.findById(id);
    }

    // 4. Update profile
    public Account updateAccount(String id, Account updatedAccount) {
        Optional<Account> existing = accountRepository.findById(id);
        if (existing.isPresent()) {
            Account account = existing.get();
            if (updatedAccount.getName() != null) account.setName(updatedAccount.getName());
            if (updatedAccount.getEmail() != null) account.setEmail(updatedAccount.getEmail());
            if (updatedAccount.getRole() != null) account.setRole(updatedAccount.getRole());
            return accountRepository.save(account);
        }
        return null;
    }

    // 5. Update status
    public boolean updateStatus(String id, String status) {
        Optional<Account> existing = accountRepository.findById(id);
        if (existing.isPresent()) {
            Account account = existing.get();
            account.setStatus(status);
            accountRepository.save(account);
            return true;
        }
        return false;
    }
}