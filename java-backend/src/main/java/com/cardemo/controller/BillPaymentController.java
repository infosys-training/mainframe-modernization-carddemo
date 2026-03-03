package com.cardemo.controller;

import com.cardemo.dto.ApiResponse;
import com.cardemo.dto.BillPaymentRequest;
import com.cardemo.model.Transaction;
import com.cardemo.service.BillPaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Bill payment controller - migrated from COBOL program COBIL00C.cbl.
 *
 * Original CICS transaction: CB00
 * Original program flow:
 *   1. Enter account ID on COBIL0A screen
 *   2. Display current balance
 *   3. Confirm payment (Y/N)
 *   4. On confirm: create transaction, zero out balance
 *
 * Pays the full current balance of the account. Creates a transaction
 * record with type '02' (bill payment) and updates the account balance.
 */
@RestController
@RequestMapping("/api/bill-payments")
@Tag(name = "Bill Payments", description = "Bill payment processing (migrated from COBIL00C)")
public class BillPaymentController {

    private final BillPaymentService billPaymentService;

    public BillPaymentController(BillPaymentService billPaymentService) {
        this.billPaymentService = billPaymentService;
    }

    @PostMapping
    @Operation(summary = "Process bill payment",
               description = "Pay full account balance. Migrated from COBIL00C. " +
                             "Creates a transaction record and zeros the account balance.")
    public ResponseEntity<ApiResponse<Transaction>> processBillPayment(
            @Valid @RequestBody BillPaymentRequest request) {
        Transaction transaction = billPaymentService.processBillPayment(request);
        return ResponseEntity.ok(ApiResponse.ok("Bill payment processed successfully", transaction));
    }
}
