package com.cardemo.service;

import com.cardemo.dto.TransactionAddRequest;
import com.cardemo.exception.BusinessValidationException;
import com.cardemo.exception.ResourceNotFoundException;
import com.cardemo.model.CardXref;
import com.cardemo.model.Transaction;
import com.cardemo.repository.CardXrefRepository;
import com.cardemo.repository.TransactionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Transaction service - migrated from COBOL programs COTRN00C, COTRN01C, COTRN02C.
 *
 * COTRN00C (700 lines): List transactions with page-forward/backward browsing.
 *   - Uses CICS STARTBR/READNEXT/READPREV on TRANSACT file.
 *   - 10 records per page with next-page indicator.
 *   - Selection 'S' transfers to COTRN01C for detail view.
 *
 * COTRN01C (331 lines): View single transaction by ID.
 *   - CICS READ DATASET('TRANSACT') with UPDATE option.
 *
 * COTRN02C (784 lines): Add new transaction with validation.
 *   - Validates account/card via CCXREF/CXACAIX cross-reference files.
 *   - Validates type code (numeric), category (numeric), amount format,
 *     date formats (YYYY-MM-DD), and merchant information.
 *   - Generates next transaction ID: reads last ID and increments.
 *   - Date validation via CSUTLDTC subroutine call.
 *   - Requires 'Y' confirmation before writing.
 */
@Service
public class TransactionService {

    private static final int PAGE_SIZE = 10;
    private static final DateTimeFormatter TIMESTAMP_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS");

    private final TransactionRepository transactionRepository;
    private final CardXrefRepository cardXrefRepository;

    public TransactionService(TransactionRepository transactionRepository,
                              CardXrefRepository cardXrefRepository) {
        this.transactionRepository = transactionRepository;
        this.cardXrefRepository = cardXrefRepository;
    }

    /**
     * List transactions with pagination - migrated from COTRN00C PROCESS-PAGE-FORWARD.
     * Original: STARTBR/READNEXT loop reading 10 records, then one more to check
     * NEXT-PAGE-YES flag.
     */
    public Page<Transaction> listTransactions(int page) {
        return transactionRepository.findAllByOrderByTranIdAsc(
                PageRequest.of(page, PAGE_SIZE));
    }

    /**
     * List transactions starting from a specific ID - migrated from COTRN00C
     * when user enters a transaction ID filter.
     */
    public Page<Transaction> listTransactionsFrom(String startTranId, int page) {
        return transactionRepository.findByTranIdGreaterThanEqualOrderByTranIdAsc(
                startTranId, PageRequest.of(page, PAGE_SIZE));
    }

    /**
     * View single transaction - migrated from COTRN01C READ-TRANSACT-FILE.
     * Original: CICS READ DATASET(WS-TRANSACT-FILE) INTO(TRAN-RECORD)
     *           RIDFLD(TRAN-ID) KEYLENGTH(LENGTH OF TRAN-ID).
     */
    public Transaction getTransaction(String tranId) {
        return transactionRepository.findById(tranId)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction ID NOT found..."));
    }

    public List<Transaction> getTransactionsByCardNumber(String cardNum) {
        return transactionRepository.findByCardNum(cardNum);
    }

    public List<Transaction> getTransactionsByAccountId(String acctId) {
        return transactionRepository.findByAccountId(acctId);
    }

    /**
     * Add transaction - migrated from COTRN02C.
     *
     * Original COBOL flow:
     * 1. VALIDATE-INPUT-KEY-FIELDS: resolve account/card via cross-reference
     * 2. VALIDATE-INPUT-DATA-FIELDS: validate all fields
     * 3. PROCESS-ENTER-KEY: check confirmation flag
     * 4. ADD-TRANSACTION: generate ID, build record, CICS WRITE
     */
    @Transactional
    public Transaction addTransaction(TransactionAddRequest request) {
        if (!request.isConfirmed()) {
            throw new BusinessValidationException("Confirm to add this transaction...");
        }

        // Resolve card number from account or validate card number
        String cardNum = resolveCardNumber(request);

        // Validate dates (YYYY-MM-DD format)
        validateDateFormat(request.getOrigDate(), "Orig Date");
        validateDateFormat(request.getProcDate(), "Proc Date");

        // Generate next transaction ID (original: read last + increment)
        String nextId = generateNextTransactionId();

        Transaction transaction = new Transaction();
        transaction.setTranId(nextId);
        transaction.setTypeCode(request.getTypeCode());
        transaction.setCategoryCode(request.getCategoryCode());
        transaction.setSource(request.getSource());
        transaction.setDescription(request.getDescription());
        transaction.setAmount(request.getAmount());
        transaction.setMerchantId(request.getMerchantId());
        transaction.setMerchantName(request.getMerchantName());
        transaction.setMerchantCity(request.getMerchantCity());
        transaction.setMerchantZip(request.getMerchantZip());
        transaction.setCardNum(cardNum);
        transaction.setOrigTimestamp(request.getOrigDate() + " 00:00:00.000000");
        transaction.setProcTimestamp(request.getProcDate() + " 00:00:00.000000");

        return transactionRepository.save(transaction);
    }

    /**
     * Resolve card number from account ID or card number input.
     * Migrated from COTRN02C VALIDATE-INPUT-KEY-FIELDS.
     * Original: If account ID provided, read CXACAIX to get card.
     *           If card provided, read CCXREF to get account.
     */
    private String resolveCardNumber(TransactionAddRequest request) {
        if (request.getCardNumber() != null && !request.getCardNumber().isBlank()) {
            // Validate card exists in cross-reference
            cardXrefRepository.findByCardNum(request.getCardNumber())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Card number not found in cross-reference"));
            return request.getCardNumber();
        } else if (request.getAccountId() != null && !request.getAccountId().isBlank()) {
            List<CardXref> xrefs = cardXrefRepository.findByAcctId(request.getAccountId());
            if (xrefs.isEmpty()) {
                throw new ResourceNotFoundException(
                        "No card found for account " + request.getAccountId());
            }
            return xrefs.get(0).getCardNum();
        } else {
            throw new BusinessValidationException(
                    "Account or Card Number must be entered...");
        }
    }

    private void validateDateFormat(String date, String fieldName) {
        if (date == null || date.length() != 10) {
            throw new BusinessValidationException(
                    fieldName + " should be in format YYYY-MM-DD");
        }
        if (!date.matches("\\d{4}-\\d{2}-\\d{2}")) {
            throw new BusinessValidationException(
                    fieldName + " should be in format YYYY-MM-DD");
        }
    }

    /**
     * Generate next transaction ID.
     * Original COBOL: STARTBR with HIGH-VALUES, READPREV to get last, add 1.
     */
    private String generateNextTransactionId() {
        String maxId = transactionRepository.findMaxTranId();
        if (maxId == null) {
            return String.format("%016d", 1);
        }
        try {
            long nextNum = Long.parseLong(maxId.trim()) + 1;
            return String.format("%016d", nextNum);
        } catch (NumberFormatException e) {
            return String.format("%016d", System.currentTimeMillis());
        }
    }
}
