package com.carddemo.transaction.controller;

import com.carddemo.common.dto.ApiResponse;
import com.carddemo.common.entity.Transaction;
import com.carddemo.transaction.service.TransactionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping("/{transactionId}")
    public ResponseEntity<ApiResponse<Transaction>> getTransaction(@PathVariable String transactionId) {
        return ResponseEntity.ok(ApiResponse.success(transactionService.getTransaction(transactionId)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<Transaction>>> listTransactions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.success(
                transactionService.listTransactions(PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "originTimestamp")))));
    }

    @GetMapping("/card/{cardNumber}")
    public ResponseEntity<ApiResponse<Page<Transaction>>> getTransactionsByCard(
            @PathVariable String cardNumber,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.success(
                transactionService.getTransactionsByCard(cardNumber, PageRequest.of(page, size))));
    }

    @GetMapping("/card/{cardNumber}/range")
    public ResponseEntity<ApiResponse<List<Transaction>>> getTransactionsByDateRange(
            @PathVariable String cardNumber,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        return ResponseEntity.ok(ApiResponse.success(
                transactionService.getTransactionsByCardAndDateRange(cardNumber, start, end)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Transaction>> createTransaction(@RequestBody Transaction transaction) {
        return ResponseEntity.ok(ApiResponse.success(transactionService.createTransaction(transaction)));
    }

    @PostMapping("/{transactionId}/process")
    public ResponseEntity<ApiResponse<Transaction>> processTransaction(@PathVariable String transactionId) {
        return ResponseEntity.ok(ApiResponse.success(transactionService.processTransaction(transactionId)));
    }

    @PostMapping("/process-batch")
    public ResponseEntity<ApiResponse<List<Transaction>>> processUnprocessed() {
        return ResponseEntity.ok(ApiResponse.success(transactionService.processUnprocessedTransactions()));
    }

    @GetMapping("/card/{cardNumber}/total-purchases")
    public ResponseEntity<ApiResponse<BigDecimal>> getTotalPurchases(@PathVariable String cardNumber) {
        return ResponseEntity.ok(ApiResponse.success(transactionService.getTotalPurchaseAmount(cardNumber)));
    }
}
