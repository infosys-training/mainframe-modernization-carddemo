package com.cardemo.controller;

import com.cardemo.dto.ApiResponse;
import com.cardemo.model.Account;
import com.cardemo.model.Customer;
import com.cardemo.service.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Account controller - migrated from COBOL programs COACTVWC.cbl and COACTUPC.cbl.
 *
 * COACTVWC: Account view (CICS transaction for read-only display)
 * COACTUPC: Account update (4237-line program with extensive validation)
 *
 * Original CICS transactions used screen maps (BMS) for data entry.
 * The account update included both account data and associated customer data
 * editing in a single program via CICS READ UPDATE / REWRITE pattern.
 */
@RestController
@RequestMapping("/api/accounts")
@Tag(name = "Accounts", description = "Account management (migrated from COACTVWC/COACTUPC)")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping
    @Operation(summary = "List all accounts")
    public ResponseEntity<ApiResponse<List<Account>>> listAccounts() {
        return ResponseEntity.ok(ApiResponse.ok(accountService.getAllAccounts()));
    }

    @GetMapping("/{acctId}")
    @Operation(summary = "View account details",
               description = "Migrated from COACTVWC account view screen.")
    public ResponseEntity<ApiResponse<Account>> getAccount(@PathVariable String acctId) {
        return ResponseEntity.ok(ApiResponse.ok(accountService.getAccountById(acctId)));
    }

    @PutMapping("/{acctId}")
    @Operation(summary = "Update account",
               description = "Migrated from COACTUPC account update. " +
                             "Validates status (Y/N), credit limits, balances, and dates.")
    public ResponseEntity<ApiResponse<Account>> updateAccount(
            @PathVariable String acctId, @RequestBody Account account) {
        return ResponseEntity.ok(ApiResponse.ok(
                "Account updated successfully",
                accountService.updateAccount(acctId, account)));
    }

    @GetMapping("/{acctId}/customer")
    @Operation(summary = "View customer for account",
               description = "Get customer associated with account via card cross-reference.")
    public ResponseEntity<ApiResponse<Customer>> getCustomer(@PathVariable String acctId) {
        return ResponseEntity.ok(ApiResponse.ok(accountService.getCustomerForAccount(acctId)));
    }

    @PutMapping("/{acctId}/customer")
    @Operation(summary = "Update customer for account",
               description = "Migrated from COACTUPC customer data update section.")
    public ResponseEntity<ApiResponse<Customer>> updateCustomer(
            @PathVariable String acctId, @RequestBody Customer customer) {
        return ResponseEntity.ok(ApiResponse.ok(
                "Customer updated successfully",
                accountService.updateCustomerForAccount(acctId, customer)));
    }
}
