package com.cardemo.service;

import com.cardemo.dto.BillPaymentRequest;
import com.cardemo.exception.BusinessValidationException;
import com.cardemo.exception.ResourceNotFoundException;
import com.cardemo.model.Account;
import com.cardemo.model.CardXref;
import com.cardemo.model.Transaction;
import com.cardemo.repository.AccountRepository;
import com.cardemo.repository.CardXrefRepository;
import com.cardemo.repository.TransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Bill payment service - migrated from COBOL program COBIL00C.cbl (573 lines).
 *
 * Original COBOL flow (PROCESS-ENTER-KEY):
 * 1. Validate account ID is not empty
 * 2. Read account from ACCTDAT file (CICS READ UPDATE)
 * 3. Check current balance > 0 ("You have nothing to pay...")
 * 4. On confirmation ('Y'):
 *    a. Read CXACAIX to get card number for the account
 *    b. Find last transaction ID via STARTBR HIGH-VALUES / READPREV
 *    c. Create new transaction record:
 *       - Type: '02', Category: 2, Source: 'POS TERM'
 *       - Description: 'BILL PAYMENT - ONLINE'
 *       - Amount: full current balance
 *       - Merchant: ID=999999999, Name='BILL PAYMENT', City/Zip='N/A'
 *    d. Get current timestamp via CICS ASKTIME/FORMATTIME
 *    e. Write transaction (CICS WRITE)
 *    f. Update account: balance = balance - payment amount (CICS REWRITE)
 */
@Service
public class BillPaymentService {

    private static final DateTimeFormatter TIMESTAMP_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS");

    private final AccountRepository accountRepository;
    private final CardXrefRepository cardXrefRepository;
    private final TransactionRepository transactionRepository;

    public BillPaymentService(AccountRepository accountRepository,
                              CardXrefRepository cardXrefRepository,
                              TransactionRepository transactionRepository) {
        this.accountRepository = accountRepository;
        this.cardXrefRepository = cardXrefRepository;
        this.transactionRepository = transactionRepository;
    }

    /**
     * Process bill payment - pays the full current balance.
     * Migrated from COBIL00C PROCESS-ENTER-KEY paragraph.
     */
    @Transactional
    public Transaction processBillPayment(BillPaymentRequest request) {
        // Validate account
        Account account = accountRepository.findById(request.getAccountId())
                .orElseThrow(() -> new ResourceNotFoundException("Account", request.getAccountId()));

        // Check balance (original: ACCT-CURR-BAL <= ZEROS)
        if (account.getCurrentBalance() == null ||
            account.getCurrentBalance().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessValidationException("You have nothing to pay...");
        }

        if (!request.isConfirmed()) {
            throw new BusinessValidationException("Confirm to make a bill payment...");
        }

        // Get card number via cross-reference (original: READ-CXACAIX-FILE)
        List<CardXref> xrefs = cardXrefRepository.findByAcctId(request.getAccountId());
        if (xrefs.isEmpty()) {
            throw new ResourceNotFoundException(
                    "No card cross-reference found for account " + request.getAccountId());
        }
        String cardNum = xrefs.get(0).getCardNum();

        // Generate next transaction ID
        String nextId = generateNextTransactionId();

        // Build transaction record (same field values as original COBOL)
        Transaction transaction = new Transaction();
        transaction.setTranId(nextId);
        transaction.setTypeCode("02");
        transaction.setCategoryCode(2);
        transaction.setSource("POS TERM");
        transaction.setDescription("BILL PAYMENT - ONLINE");
        transaction.setAmount(account.getCurrentBalance());
        transaction.setCardNum(cardNum);
        transaction.setMerchantId(999999999L);
        transaction.setMerchantName("BILL PAYMENT");
        transaction.setMerchantCity("N/A");
        transaction.setMerchantZip("N/A");

        // Timestamp (original: GET-CURRENT-TIMESTAMP via CICS ASKTIME/FORMATTIME)
        String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMAT);
        transaction.setOrigTimestamp(timestamp);
        transaction.setProcTimestamp(timestamp);

        // Write transaction
        transactionRepository.save(transaction);

        // Update account balance (original: COMPUTE ACCT-CURR-BAL = ACCT-CURR-BAL - TRAN-AMT)
        account.setCurrentBalance(
                account.getCurrentBalance().subtract(account.getCurrentBalance()));
        accountRepository.save(account);

        return transaction;
    }

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
