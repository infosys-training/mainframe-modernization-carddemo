package com.cardemo.batch;

import com.cardemo.model.Account;
import com.cardemo.model.Transaction;
import com.cardemo.repository.AccountRepository;
import com.cardemo.repository.CardXrefRepository;
import com.cardemo.repository.TransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * Daily transaction processor - migrated from COBOL batch programs.
 *
 * CBTRN01C: Daily transaction posting batch job.
 *   - Reads daily transaction file
 *   - Validates each transaction against account/card cross-reference
 *   - Posts valid transactions to TRANSACT file
 *   - Updates account balances (current balance, cycle credit/debit)
 *   - Rejects invalid transactions to reject file
 *
 * CBTRN02C/CBTRN03C: Additional transaction processing.
 *   - Cross-reference validation
 *   - Transaction categorization
 *
 * CBACT01C-04C: Account batch processing.
 *   - CBACT01C: Read accounts
 *   - CBACT02C: Update account cycle totals
 *   - CBACT03C: Interest calculation
 *   - CBACT04C: Account status updates
 *
 * In the original mainframe, these ran as scheduled JCL jobs.
 * This service can be invoked via REST API or scheduled with @Scheduled.
 */
@Service
public class DailyTransactionProcessor {

    private static final Logger log = LoggerFactory.getLogger(DailyTransactionProcessor.class);

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final CardXrefRepository cardXrefRepository;

    public DailyTransactionProcessor(TransactionRepository transactionRepository,
                                     AccountRepository accountRepository,
                                     CardXrefRepository cardXrefRepository) {
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
        this.cardXrefRepository = cardXrefRepository;
    }

    /**
     * Process daily transactions - migrated from CBTRN01C.
     * Updates account balances based on posted transactions.
     */
    @Transactional
    public BatchResult processDailyTransactions() {
        log.info("Starting daily transaction processing");
        int processed = 0;
        int errors = 0;

        List<Account> accounts = accountRepository.findAll();
        for (Account account : accounts) {
            try {
                List<Transaction> transactions = transactionRepository.findByAccountId(account.getAcctId());
                BigDecimal totalCredits = BigDecimal.ZERO;
                BigDecimal totalDebits = BigDecimal.ZERO;

                for (Transaction txn : transactions) {
                    if (txn.getAmount() != null) {
                        if (txn.getAmount().compareTo(BigDecimal.ZERO) >= 0) {
                            totalDebits = totalDebits.add(txn.getAmount());
                        } else {
                            totalCredits = totalCredits.add(txn.getAmount().abs());
                        }
                    }
                }

                account.setCurrentCycleCredit(totalCredits);
                account.setCurrentCycleDebit(totalDebits);
                accountRepository.save(account);
                processed++;
            } catch (Exception e) {
                log.error("Error processing account {}: {}", account.getAcctId(), e.getMessage());
                errors++;
            }
        }

        log.info("Daily transaction processing complete: {} processed, {} errors", processed, errors);
        return new BatchResult(processed, errors);
    }

    /**
     * Calculate interest for all active accounts - migrated from CBACT03C.
     * Original: Read account, if active, compute interest = balance * rate / 12.
     */
    @Transactional
    public BatchResult calculateInterest(BigDecimal annualRate) {
        log.info("Starting interest calculation with rate: {}", annualRate);
        int processed = 0;
        int errors = 0;

        BigDecimal monthlyRate = annualRate.divide(BigDecimal.valueOf(1200), 10, RoundingMode.HALF_UP);

        List<Account> accounts = accountRepository.findByActiveStatus("Y");
        for (Account account : accounts) {
            try {
                if (account.getCurrentBalance() != null &&
                    account.getCurrentBalance().compareTo(BigDecimal.ZERO) > 0) {
                    BigDecimal interest = account.getCurrentBalance()
                            .multiply(monthlyRate)
                            .setScale(2, RoundingMode.HALF_UP);
                    account.setCurrentBalance(account.getCurrentBalance().add(interest));
                    accountRepository.save(account);
                }
                processed++;
            } catch (Exception e) {
                log.error("Error calculating interest for account {}: {}",
                        account.getAcctId(), e.getMessage());
                errors++;
            }
        }

        log.info("Interest calculation complete: {} processed, {} errors", processed, errors);
        return new BatchResult(processed, errors);
    }

    /**
     * Batch processing result.
     */
    public static class BatchResult {
        private final int processed;
        private final int errors;

        public BatchResult(int processed, int errors) {
            this.processed = processed;
            this.errors = errors;
        }

        public int getProcessed() {
            return processed;
        }

        public int getErrors() {
            return errors;
        }
    }
}
