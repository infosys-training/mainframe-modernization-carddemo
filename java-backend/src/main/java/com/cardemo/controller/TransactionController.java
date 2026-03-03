package com.cardemo.controller;

import com.cardemo.dto.ApiResponse;
import com.cardemo.dto.TransactionAddRequest;
import com.cardemo.model.Transaction;
import com.cardemo.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Transaction controller - migrated from COBOL programs COTRN00C, COTRN01C, COTRN02C.
 *
 * COTRN00C (700 lines): Transaction list with paginated browsing.
 *   - Original CICS transaction: CT00
 *   - STARTBR/READNEXT loop showing 10 records per page.
 *   - PF7/PF8 for page backward/forward navigation.
 *   - Selection 'S' to view transaction detail.
 *
 * COTRN01C (331 lines): Transaction view.
 *   - Original CICS transaction: CT01
 *   - Single record read by transaction ID.
 *
 * COTRN02C (784 lines): Transaction add with validation.
 *   - Original CICS transaction: CT02
 *   - Cross-reference lookup, field validation, confirmation, ID generation.
 */
@RestController
@RequestMapping("/api/transactions")
@Tag(name = "Transactions", description = "Transaction management (migrated from COTRN00C/01C/02C)")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping
    @Operation(summary = "List transactions with pagination",
               description = "Migrated from COTRN00C. Page size = 10 (same as original). " +
                             "Optionally filter by starting transaction ID or card/account.")
    public ResponseEntity<ApiResponse<Page<Transaction>>> listTransactions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) String startTranId,
            @RequestParam(required = false) String cardNum,
            @RequestParam(required = false) String acctId) {
        if (cardNum != null && !cardNum.isBlank()) {
            List<Transaction> list = transactionService.getTransactionsByCardNumber(cardNum);
            return ResponseEntity.ok(ApiResponse.ok("Transactions retrieved", null));
        }
        Page<Transaction> result;
        if (startTranId != null && !startTranId.isBlank()) {
            result = transactionService.listTransactionsFrom(startTranId, page);
        } else {
            result = transactionService.listTransactions(page);
        }
        return ResponseEntity.ok(ApiResponse.ok(result));
    }

    @GetMapping("/by-card/{cardNum}")
    @Operation(summary = "List transactions by card number")
    public ResponseEntity<ApiResponse<List<Transaction>>> getByCard(@PathVariable String cardNum) {
        return ResponseEntity.ok(ApiResponse.ok(
                transactionService.getTransactionsByCardNumber(cardNum)));
    }

    @GetMapping("/by-account/{acctId}")
    @Operation(summary = "List transactions by account ID")
    public ResponseEntity<ApiResponse<List<Transaction>>> getByAccount(@PathVariable String acctId) {
        return ResponseEntity.ok(ApiResponse.ok(
                transactionService.getTransactionsByAccountId(acctId)));
    }

    @GetMapping("/{tranId}")
    @Operation(summary = "View transaction detail",
               description = "Migrated from COTRN01C. Read single transaction by ID.")
    public ResponseEntity<ApiResponse<Transaction>> getTransaction(@PathVariable String tranId) {
        return ResponseEntity.ok(ApiResponse.ok(transactionService.getTransaction(tranId)));
    }

    @PostMapping
    @Operation(summary = "Add new transaction",
               description = "Migrated from COTRN02C. Validates account/card cross-reference, " +
                             "field formats, and requires confirmation.")
    public ResponseEntity<ApiResponse<Transaction>> addTransaction(
            @Valid @RequestBody TransactionAddRequest request) {
        Transaction created = transactionService.addTransaction(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Transaction added successfully", created));
    }
}
