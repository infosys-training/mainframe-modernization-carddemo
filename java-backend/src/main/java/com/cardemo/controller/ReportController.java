package com.cardemo.controller;

import com.cardemo.dto.ApiResponse;
import com.cardemo.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Report controller - migrated from COBOL programs CORPT00C.cbl and CBSTM03A.CBL.
 *
 * CORPT00C: Online transaction report screen (CICS).
 * CBSTM03A: Batch statement generation (JCL CBSTM03A step).
 *
 * Provides both summary reports and account-level statements.
 */
@RestController
@RequestMapping("/api/reports")
@Tag(name = "Reports", description = "Transaction reports and statements (migrated from CORPT00C/CBSTM03A)")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/transactions")
    @Operation(summary = "Transaction summary report",
               description = "Migrated from CORPT00C. Provides totals, counts by type/category.")
    public ResponseEntity<ApiResponse<Map<String, Object>>> transactionReport() {
        return ResponseEntity.ok(ApiResponse.ok(reportService.generateTransactionReport()));
    }

    @GetMapping("/statements/{acctId}")
    @Operation(summary = "Account statement",
               description = "Migrated from CBSTM03A batch statement generation. " +
                             "Returns transactions and totals for a specific account.")
    public ResponseEntity<ApiResponse<Map<String, Object>>> accountStatement(
            @PathVariable String acctId) {
        return ResponseEntity.ok(ApiResponse.ok(reportService.generateAccountStatement(acctId)));
    }
}
