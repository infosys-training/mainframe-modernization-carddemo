package com.carddemo.controller;

import com.carddemo.entity.Transaction;
import com.carddemo.service.TransactionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping("/{tranId}")
    public ResponseEntity<Transaction> getTransaction(@PathVariable String tranId) {
        return ResponseEntity.ok(transactionService.findById(tranId));
    }

    @GetMapping
    public ResponseEntity<Page<Transaction>> listTransactions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(transactionService.findAll(PageRequest.of(page, size)));
    }

    @GetMapping("/by-card/{cardNum}")
    public ResponseEntity<Page<Transaction>> getByCardNum(
            @PathVariable String cardNum,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(
                transactionService.findByCardNum(cardNum, PageRequest.of(page, size)));
    }
}
