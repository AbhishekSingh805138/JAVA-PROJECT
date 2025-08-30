package com.example.transaction_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "account-service")   // this matches the spring.application.name of account-service
public interface AccountClient {

    @PutMapping("/accounts/{id}/updateBalance")
    void updateBalance(
            @PathVariable("id") Integer id,
            @RequestParam Double amount
    );
}
