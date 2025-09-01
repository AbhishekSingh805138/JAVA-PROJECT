package com.example.account_service.controller;

import com.example.account_service.model.Account;
import com.example.account_service.repository.AccountRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/accounts")
public class AccountController {

    private final AccountRepository accountRepository;

    public AccountController(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @GetMapping
    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }

    @GetMapping("/{id}")
    public Account getAccountById(@PathVariable Integer id) {
        return accountRepository.findById(id).orElse(null);
    }

    @GetMapping("/user/{userId}")
    public List<Account> getAccountsByUserId(@PathVariable Integer userId) {
        return accountRepository.findByUserId(userId);
    }

    @PostMapping
    public Account createAccount(@RequestBody Account account) {
        return accountRepository.save(account);
    }

    @PutMapping("/{id}")
    public Account updateAccount(@PathVariable Integer id, @RequestBody Account account) {
        return accountRepository.findById(id).map(existing -> {
            existing.setAccountType(account.getAccountType());
            existing.setAccountBalance(account.getAccountBalance());
            existing.setSecretPassword(account.getSecretPassword());
            return accountRepository.save(existing);
        }).orElse(null);
    }

    @DeleteMapping("/{id}")
    public void deleteAccount(@PathVariable Integer id) {
        accountRepository.deleteById(id);
    }

    // ✅ New endpoint for balance updates (used by Transaction Service)
    @PutMapping("/{id}/updateBalance")
    public Account updateBalance(@PathVariable Integer id, @RequestParam Double amount) {
        return accountRepository.findById(id).map(account -> {
            account.setAccountBalance(account.getAccountBalance() + amount);
            return accountRepository.save(account);
        }).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found with ID: " + id));
    }
}
