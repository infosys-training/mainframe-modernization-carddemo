package com.carddemo.reporting.controller;

import com.carddemo.common.dto.ApiResponse;
import com.carddemo.reporting.dto.DailyTransactionReport;
import com.carddemo.reporting.dto.StatementReport;
import com.carddemo.reporting.service.ReportingService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/reports")
public class ReportingController {

    private final ReportingService reportingService;

    public ReportingController(ReportingService reportingService) {
        this.reportingService = reportingService;
    }

    @GetMapping("/statement")
    public ResponseEntity<ApiResponse<StatementReport>> generateStatement(
            @RequestParam String cardNumber,
            @RequestParam String accountId,
            @RequestParam(defaultValue = "Customer") String customerName,
            @RequestParam(defaultValue = "0") BigDecimal openingBalance,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate periodStart,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate periodEnd) {
        return ResponseEntity.ok(ApiResponse.success(
                reportingService.generateStatement(cardNumber, accountId, customerName,
                        openingBalance, periodStart, periodEnd)));
    }

    @GetMapping("/daily")
    public ResponseEntity<ApiResponse<DailyTransactionReport>> generateDailyReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(ApiResponse.success(reportingService.generateDailyReport(date)));
    }
}
