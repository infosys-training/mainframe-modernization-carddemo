package com.carddemo.transaction.service;

import com.carddemo.common.entity.Transaction;
import com.carddemo.common.exception.BusinessException;
import com.carddemo.common.exception.ResourceNotFoundException;
import com.carddemo.transaction.repository.TransactionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Transaction processing service.
 * Migrated from COTRN00C (list), COTRN01C (view), COTRN02C (add),
 * CBTRN01C/02C (batch posting), COBIL00C (bill payment).
 */
@Service
@Transactional
public class TransactionService {

    private final TransactionRepository transactionRepository;

    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Transactional(readOnly = true)
    public Transaction getTransaction(String transactionId) {
        return transactionRepository.findById(transactionId)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction", transactionId));
    }

    @Transactional(readOnly = true)
    public Page<Transaction> listTransactions(Pageable pageable) {
        return transactionRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Page<Transaction> getTransactionsByCard(String cardNumber, Pageable pageable) {
        return transactionRepository.findByCardNumber(cardNumber, pageable);
    }

    @Transactional(readOnly = true)
    public List<Transaction> getTransactionsByCardAndDateRange(
            String cardNumber, LocalDateTime start, LocalDateTime end) {
        return transactionRepository.findByCardNumberAndDateRange(cardNumber, start, end);
    }

    public Transaction createTransaction(Transaction transaction) {
        if (transaction.getTransactionId() == null || transaction.getTransactionId().isBlank()) {
            transaction.setTransactionId(generateTransactionId());
        }
        if (transaction.getAmount() == null || transaction.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("Transaction amount must be positive");
        }
        if (transaction.getCardNumber() == null || transaction.getCardNumber().isBlank()) {
            throw new BusinessException("Card number is required");
        }
        if (transaction.getTypeCode() == null || transaction.getTypeCode().isBlank()) {
            throw new BusinessException("Transaction type code is required");
        }
        transaction.setOriginTimestamp(LocalDateTime.now());
        return transactionRepository.save(transaction);
    }

    public Transaction processTransaction(String transactionId) {
        Transaction transaction = getTransaction(transactionId);
        if (transaction.getProcessedTimestamp() != null) {
            throw new BusinessException("Transaction already processed: " + transactionId);
        }
        transaction.setProcessedTimestamp(LocalDateTime.now());
        return transactionRepository.save(transaction);
    }

    public List<Transaction> processUnprocessedTransactions() {
        List<Transaction> unprocessed = transactionRepository.findUnprocessedTransactions();
        LocalDateTime now = LocalDateTime.now();
        for (Transaction t : unprocessed) {
            t.setProcessedTimestamp(now);
        }
        return transactionRepository.saveAll(unprocessed);
    }

    @Transactional(readOnly = true)
    public BigDecimal getTotalPurchaseAmount(String cardNumber) {
        return transactionRepository.getTotalPurchaseAmountByCard(cardNumber);
    }

    private String generateTransactionId() {
        return String.format("%016d", Math.abs(UUID.randomUUID().getMostSignificantBits() % 10000000000000000L));
    }
}
