package com.example.transaction_service.controller;

import com.example.transaction_service.model.Transaction;
import com.example.transaction_service.repository.TransactionRepository;
import com.example.transaction_service.client.AccountClient;
import feign.FeignException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/transactions")
public class TransactionController {

    private final TransactionRepository transactionRepository;
    private final AccountClient accountClient;

    public TransactionController(TransactionRepository transactionRepository, AccountClient accountClient) {
        this.transactionRepository = transactionRepository;
        this.accountClient = accountClient;
    }

    @GetMapping
    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    @GetMapping("/{id}")
    public Transaction getTransactionById(@PathVariable Integer id) {
        return transactionRepository.findById(id).orElse(null);
    }

    @GetMapping("/user/{userId}")
    public List<Transaction> getTransactionsByUserId(@PathVariable Integer userId) {
        return transactionRepository.findByUserId(userId);
    }

    @PostMapping
    public Transaction createTransaction(@RequestBody Transaction transaction) {
        Double amount = transaction.getAmount();

        try {
            switch (transaction.getTransactionType().toUpperCase()) {
                case "DEPOSIT":
                    // add balance to account
                    accountClient.updateBalance(transaction.getToAccountId(), amount);
                    break;

                case "WITHDRAW":
                    // deduct balance from account
                    accountClient.updateBalance(transaction.getFrmAccountId(), -amount);
                    break;

                case "TRANSFER":
                    // deduct from one account
                    accountClient.updateBalance(transaction.getFrmAccountId(), -amount);
                    // add to another account
                    accountClient.updateBalance(transaction.getToAccountId(), amount);
                    break;

                default:
                    throw new IllegalArgumentException("Invalid transaction type: " + transaction.getTransactionType());
            }
        } catch (FeignException e) {
            if (e.status() == 404) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found while updating balance");
            }
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Account service error: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to update account balance");
        }

        // save transaction in DB
        return transactionRepository.save(transaction);
    }

    @DeleteMapping("/{id}")
    public void deleteTransaction(@PathVariable Integer id) {
        transactionRepository.deleteById(id);
    }
}
