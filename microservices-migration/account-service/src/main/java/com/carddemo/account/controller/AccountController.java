package com.carddemo.account.controller;

import com.carddemo.account.service.AccountService;
import com.carddemo.common.dto.ApiResponse;
import com.carddemo.common.entity.Account;
import com.carddemo.common.entity.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST API for account and customer management.
 * Replaces COACTVWC (Account View) and COACTUPC (Account Update) BMS screens.
 */
@RestController
@RequestMapping("/api/v1")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping("/accounts/{accountId}")
    public ResponseEntity<ApiResponse<Account>> getAccount(@PathVariable String accountId) {
        return ResponseEntity.ok(ApiResponse.success(accountService.getAccount(accountId)));
    }

    @GetMapping("/accounts")
    public ResponseEntity<ApiResponse<Page<Account>>> listAccounts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "false") boolean activeOnly) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("accountId"));
        Page<Account> accounts = activeOnly
                ? accountService.listActiveAccounts(pageRequest)
                : accountService.listAccounts(pageRequest);
        return ResponseEntity.ok(ApiResponse.success(accounts));
    }

    @PutMapping("/accounts/{accountId}")
    public ResponseEntity<ApiResponse<Account>> updateAccount(
            @PathVariable String accountId, @RequestBody Account updates) {
        return ResponseEntity.ok(ApiResponse.success(accountService.updateAccount(accountId, updates)));
    }

    @PutMapping("/accounts/{accountId}/activate")
    public ResponseEntity<ApiResponse<Account>> activateAccount(@PathVariable String accountId) {
        return ResponseEntity.ok(ApiResponse.success(accountService.activateAccount(accountId)));
    }

    @PutMapping("/accounts/{accountId}/deactivate")
    public ResponseEntity<ApiResponse<Account>> deactivateAccount(@PathVariable String accountId) {
        return ResponseEntity.ok(ApiResponse.success(accountService.deactivateAccount(accountId)));
    }

    @GetMapping("/customers/{customerId}")
    public ResponseEntity<ApiResponse<Customer>> getCustomer(@PathVariable String customerId) {
        return ResponseEntity.ok(ApiResponse.success(accountService.getCustomer(customerId)));
    }

    @GetMapping("/customers")
    public ResponseEntity<ApiResponse<Page<Customer>>> listCustomers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.success(
                accountService.listCustomers(PageRequest.of(page, size, Sort.by("customerId")))));
    }

    @PutMapping("/customers/{customerId}")
    public ResponseEntity<ApiResponse<Customer>> updateCustomer(
            @PathVariable String customerId, @RequestBody Customer updates) {
        return ResponseEntity.ok(ApiResponse.success(accountService.updateCustomer(customerId, updates)));
    }

    @GetMapping("/accounts/{accountId}/customer")
    public ResponseEntity<ApiResponse<Customer>> getCustomerForAccount(@PathVariable String accountId) {
        return ResponseEntity.ok(ApiResponse.success(accountService.getCustomerForAccount(accountId)));
    }

    @GetMapping("/customers/{customerId}/accounts")
    public ResponseEntity<ApiResponse<List<Account>>> getAccountsForCustomer(@PathVariable String customerId) {
        return ResponseEntity.ok(ApiResponse.success(accountService.getAccountsForCustomer(customerId)));
    }
}
