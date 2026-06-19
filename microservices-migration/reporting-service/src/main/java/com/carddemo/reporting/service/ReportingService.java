package com.carddemo.reporting.service;

import com.carddemo.common.entity.Transaction;
import com.carddemo.reporting.dto.DailyTransactionReport;
import com.carddemo.reporting.dto.StatementLineItem;
import com.carddemo.reporting.dto.StatementReport;
import com.carddemo.reporting.repository.ReportTransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Statement and report generation service.
 * Migrated from CBSTM03A/B (statement generation), CBTRN03C (transaction report),
 * CORPT00C (report request screen).
 */
@Service
@Transactional(readOnly = true)
public class ReportingService {

    private static final BigDecimal MINIMUM_PAYMENT_RATE = new BigDecimal("0.02");
    private static final BigDecimal MINIMUM_PAYMENT_FLOOR = new BigDecimal("25.00");

    private final ReportTransactionRepository transactionRepository;

    public ReportingService(ReportTransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public StatementReport generateStatement(String cardNumber, String accountId,
                                             String customerName, BigDecimal openingBalance,
                                             LocalDate periodStart, LocalDate periodEnd) {
        List<Transaction> transactions = transactionRepository.findByCardAndDateRange(
                cardNumber,
                periodStart.atStartOfDay(),
                periodEnd.atTime(LocalTime.MAX));

        StatementReport report = new StatementReport();
        report.setAccountId(accountId);
        report.setCustomerName(customerName);
        report.setStatementDate(LocalDate.now());
        report.setPeriodStart(periodStart);
        report.setPeriodEnd(periodEnd);
        report.setOpeningBalance(openingBalance);

        BigDecimal totalDebits = BigDecimal.ZERO;
        BigDecimal totalCredits = BigDecimal.ZERO;

        for (Transaction t : transactions) {
            StatementLineItem item = new StatementLineItem(
                    t.getTransactionId(), t.getOriginTimestamp(), t.getDescription(),
                    t.getMerchantName(), t.getTypeCode(), t.getAmount());
            report.getLineItems().add(item);

            if (t.isPurchase() || "04".equals(t.getTypeCode())) {
                totalDebits = totalDebits.add(t.getAmount());
            } else if (t.isPayment() || t.isCredit() || t.isRefund()) {
                totalCredits = totalCredits.add(t.getAmount());
            }
        }

        report.setTotalDebits(totalDebits);
        report.setTotalCredits(totalCredits);

        BigDecimal closingBalance = openingBalance.add(totalDebits).subtract(totalCredits);
        report.setClosingBalance(closingBalance);

        BigDecimal minPayment = closingBalance.multiply(MINIMUM_PAYMENT_RATE)
                .setScale(2, RoundingMode.HALF_UP);
        if (minPayment.compareTo(MINIMUM_PAYMENT_FLOOR) < 0 && closingBalance.compareTo(MINIMUM_PAYMENT_FLOOR) > 0) {
            minPayment = MINIMUM_PAYMENT_FLOOR;
        }
        report.setMinimumPaymentDue(minPayment.max(BigDecimal.ZERO));
        report.setPaymentDueDate(periodEnd.plusDays(25));

        return report;
    }

    public DailyTransactionReport generateDailyReport(LocalDate date) {
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.atTime(LocalTime.MAX);
        List<Transaction> transactions = transactionRepository.findByDateRange(start, end);

        DailyTransactionReport report = new DailyTransactionReport();
        report.setReportDate(date);
        report.setTotalTransactions(transactions.size());

        BigDecimal totalAmount = BigDecimal.ZERO;
        Map<String, Integer> countByType = new HashMap<>();
        Map<String, BigDecimal> amountByType = new HashMap<>();

        List<StatementLineItem> items = new java.util.ArrayList<>();
        for (Transaction t : transactions) {
            totalAmount = totalAmount.add(t.getAmount() != null ? t.getAmount() : BigDecimal.ZERO);
            countByType.merge(t.getTypeCode(), 1, Integer::sum);
            amountByType.merge(t.getTypeCode(),
                    t.getAmount() != null ? t.getAmount() : BigDecimal.ZERO,
                    BigDecimal::add);
            items.add(new StatementLineItem(
                    t.getTransactionId(), t.getOriginTimestamp(), t.getDescription(),
                    t.getMerchantName(), t.getTypeCode(), t.getAmount()));
        }

        report.setTotalAmount(totalAmount);
        report.setTransactionsByType(countByType);
        report.setAmountsByType(amountByType);
        report.setTransactions(items);

        return report;
    }
}
