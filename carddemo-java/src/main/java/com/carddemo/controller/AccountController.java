package com.carddemo.controller;

import com.carddemo.entity.Account;
import com.carddemo.entity.Customer;
import com.carddemo.service.AccountService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping("/{acctId}")
    public ResponseEntity<Map<String, Object>> getAccount(@PathVariable Long acctId) {
        Account account = accountService.findById(acctId);
        Optional<Customer> customer = accountService.findCustomerForAccount(acctId);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("account", account);
        customer.ifPresent(c -> response.put("customer", c));
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<Account>> getAllAccounts() {
        return ResponseEntity.ok(accountService.findAll());
    }
}
