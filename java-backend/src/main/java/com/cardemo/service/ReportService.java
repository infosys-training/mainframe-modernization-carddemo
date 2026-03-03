package com.cardemo.service;

import com.cardemo.model.Transaction;
import com.cardemo.repository.TransactionRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Report service - migrated from COBOL programs CORPT00C.cbl and CBSTM03A.CBL.
 *
 * CORPT00C: Online transaction report screen.
 * CBSTM03A (batch): Statement generation program that processes transactions
 *   by account, calculates totals, and generates formatted statements.
 * CBSTM03B: File handling subroutine for CBSTM03A.
 *
 * This service provides the reporting functionality previously split between
 * online (CICS) and batch (JCL) processing.
 */
@Service
public class ReportService {

    private final TransactionRepository transactionRepository;

    public ReportService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    /**
     * Generate transaction summary report.
     * Migrated from CORPT00C report generation and CBSTM03A batch statement logic.
     */
    public Map<String, Object> generateTransactionReport() {
        List<Transaction> allTransactions = transactionRepository.findAll(
                Sort.by(Sort.Direction.ASC, "tranId"));

        Map<String, Object> report = new HashMap<>();
        report.put("totalTransactions", allTransactions.size());
        report.put("totalAmount", allTransactions.stream()
                .map(Transaction::getAmount)
                .filter(a -> a != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add));

        // Group by transaction type (migrated from batch processing categorization)
        Map<String, Long> byType = allTransactions.stream()
                .collect(Collectors.groupingBy(
                        t -> t.getTypeCode() != null ? t.getTypeCode() : "UNKNOWN",
                        Collectors.counting()));
        report.put("transactionsByType", byType);

        // Group by category code
        Map<Integer, Long> byCategory = allTransactions.stream()
                .filter(t -> t.getCategoryCode() != null)
                .collect(Collectors.groupingBy(Transaction::getCategoryCode,
                        Collectors.counting()));
        report.put("transactionsByCategory", byCategory);

        // Amount by type
        Map<String, BigDecimal> amountByType = allTransactions.stream()
                .filter(t -> t.getAmount() != null)
                .collect(Collectors.groupingBy(
                        t -> t.getTypeCode() != null ? t.getTypeCode() : "UNKNOWN",
                        Collectors.reducing(BigDecimal.ZERO, Transaction::getAmount, BigDecimal::add)));
        report.put("amountByType", amountByType);

        return report;
    }

    /**
     * Generate account-level statement.
     * Migrated from CBSTM03A batch statement generation.
     * Original: Read TRANSACT by card number, group by account, calculate totals.
     */
    public Map<String, Object> generateAccountStatement(String acctId) {
        List<Transaction> transactions = transactionRepository.findByAccountId(acctId);

        Map<String, Object> statement = new HashMap<>();
        statement.put("accountId", acctId);
        statement.put("transactionCount", transactions.size());
        statement.put("transactions", transactions);
        statement.put("totalAmount", transactions.stream()
                .map(Transaction::getAmount)
                .filter(a -> a != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add));

        return statement;
    }
}
