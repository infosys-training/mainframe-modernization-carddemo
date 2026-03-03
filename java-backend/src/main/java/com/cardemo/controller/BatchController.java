package com.cardemo.controller;

import com.cardemo.batch.DailyTransactionProcessor;
import com.cardemo.batch.DailyTransactionProcessor.BatchResult;
import com.cardemo.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

/**
 * Batch processing controller - exposes batch operations as REST endpoints.
 *
 * Migrated from JCL-scheduled batch programs:
 * - CBTRN01C: Daily transaction posting
 * - CBACT03C: Interest calculation
 *
 * In the original mainframe, these were run as scheduled JCL jobs via JES.
 * This controller provides on-demand execution; for scheduled execution,
 * use Spring's @Scheduled annotation or an external scheduler.
 */
@RestController
@RequestMapping("/api/batch")
@Tag(name = "Batch Processing", description = "Batch operations (migrated from JCL batch jobs)")
public class BatchController {

    private final DailyTransactionProcessor processor;

    public BatchController(DailyTransactionProcessor processor) {
        this.processor = processor;
    }

    @PostMapping("/daily-transactions")
    @Operation(summary = "Process daily transactions",
               description = "Migrated from CBTRN01C. Updates account cycle totals.")
    public ResponseEntity<ApiResponse<BatchResult>> processDailyTransactions() {
        BatchResult result = processor.processDailyTransactions();
        return ResponseEntity.ok(ApiResponse.ok("Daily transaction processing complete", result));
    }

    @PostMapping("/interest-calculation")
    @Operation(summary = "Calculate monthly interest",
               description = "Migrated from CBACT03C. Applies interest to active account balances.")
    public ResponseEntity<ApiResponse<BatchResult>> calculateInterest(
            @RequestParam(defaultValue = "22.99") BigDecimal annualRate) {
        BatchResult result = processor.calculateInterest(annualRate);
        return ResponseEntity.ok(ApiResponse.ok("Interest calculation complete", result));
    }
}
