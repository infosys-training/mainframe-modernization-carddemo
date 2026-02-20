package com.carddemo.batch.config;

import com.carddemo.entity.Account;
import com.carddemo.entity.CardXref;
import com.carddemo.entity.DisclosureGroup;
import com.carddemo.entity.Transaction;
import com.carddemo.repository.AccountRepository;
import com.carddemo.repository.CardXrefRepository;
import com.carddemo.repository.DisclosureGroupRepository;
import com.carddemo.repository.TransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

@Configuration
public class InterestCalculationJobConfig {

    private static final Logger log = LoggerFactory.getLogger(InterestCalculationJobConfig.class);
    private static final BigDecimal TWELVE_HUNDRED = new BigDecimal("1200");
    private static final DateTimeFormatter DB2_TS_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd-HH.mm.ss.SS0000");

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final AccountRepository accountRepository;
    private final CardXrefRepository cardXrefRepository;
    private final DisclosureGroupRepository disclosureGroupRepository;
    private final TransactionRepository transactionRepository;

    public InterestCalculationJobConfig(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager,
            AccountRepository accountRepository,
            CardXrefRepository cardXrefRepository,
            DisclosureGroupRepository disclosureGroupRepository,
            TransactionRepository transactionRepository) {
        this.jobRepository = jobRepository;
        this.transactionManager = transactionManager;
        this.accountRepository = accountRepository;
        this.cardXrefRepository = cardXrefRepository;
        this.disclosureGroupRepository = disclosureGroupRepository;
        this.transactionRepository = transactionRepository;
    }

    @Bean
    public Job interestCalculationJob() {
        return new JobBuilder("interestCalculationJob", jobRepository)
                .start(interestCalculationStep())
                .build();
    }

    @Bean
    public Step interestCalculationStep() {
        return new StepBuilder("interestCalculationStep", jobRepository)
                .tasklet(interestCalculationTasklet(), transactionManager)
                .build();
    }

    @Bean
    public Tasklet interestCalculationTasklet() {
        return (StepContribution contribution, ChunkContext chunkContext) -> {
            log.info("START OF EXECUTION OF INTEREST CALCULATION (CBACT04C equivalent)");

            String parmDate = chunkContext.getStepContext()
                    .getJobParameters().get("parmDate").toString();

            processInterestCalculation(parmDate);

            log.info("END OF EXECUTION OF INTEREST CALCULATION");
            return RepeatStatus.FINISHED;
        };
    }

    private void processInterestCalculation(String parmDate) {
        AtomicInteger tranIdSuffix = new AtomicInteger(0);
        Long lastAcctId = null;
        BigDecimal totalInterest = BigDecimal.ZERO;
        Account currentAccount = null;
        CardXref currentXref = null;
        boolean firstTime = true;

        List<Account> allAccounts = accountRepository.findAll();

        for (Account account : allAccounts) {
            Long acctId = account.getAcctId();

            if (lastAcctId != null && !acctId.equals(lastAcctId)) {
                if (!firstTime && currentAccount != null) {
                    updateAccountBalance(currentAccount, totalInterest);
                }
                totalInterest = BigDecimal.ZERO;
            }

            if (firstTime || !acctId.equals(lastAcctId)) {
                if (firstTime) {
                    firstTime = false;
                }
                lastAcctId = acctId;
                currentAccount = account;

                Optional<CardXref> xrefOpt = cardXrefRepository.findByAcctId(acctId);
                currentXref = xrefOpt.orElse(null);
                if (currentXref == null) {
                    log.warn("No XREF found for account {}", acctId);
                    continue;
                }
            }

            String groupId = account.getGroupId();
            if (groupId == null || groupId.isBlank()) {
                continue;
            }

            Optional<DisclosureGroup> discGroupOpt =
                    disclosureGroupRepository.findByAcctGroupIdAndTranTypeCdAndTranCatCd(
                            groupId, "01", 5);

            if (discGroupOpt.isEmpty()) {
                discGroupOpt = disclosureGroupRepository
                        .findByAcctGroupIdAndTranTypeCdAndTranCatCd("DEFAULT", "01", 5);
            }

            if (discGroupOpt.isPresent() && discGroupOpt.get().getIntRate()
                    .compareTo(BigDecimal.ZERO) != 0) {
                BigDecimal intRate = discGroupOpt.get().getIntRate();
                BigDecimal balance = account.getCurrBal() != null ?
                        account.getCurrBal() : BigDecimal.ZERO;

                BigDecimal monthlyInt = balance.multiply(intRate)
                        .divide(TWELVE_HUNDRED, 2, RoundingMode.HALF_UP);

                totalInterest = totalInterest.add(monthlyInt);

                writeInterestTransaction(parmDate, tranIdSuffix, acctId,
                        monthlyInt, currentXref);
            }
        }

        if (currentAccount != null && !firstTime) {
            updateAccountBalance(currentAccount, totalInterest);
        }
    }

    private void updateAccountBalance(Account account, BigDecimal totalInterest) {
        BigDecimal currentBal = account.getCurrBal() != null ?
                account.getCurrBal() : BigDecimal.ZERO;
        account.setCurrBal(currentBal.add(totalInterest));
        account.setCurrCycCredit(BigDecimal.ZERO);
        account.setCurrCycDebit(BigDecimal.ZERO);
        accountRepository.save(account);
        log.info("Updated account {} balance with interest {}", account.getAcctId(), totalInterest);
    }

    private void writeInterestTransaction(String parmDate, AtomicInteger tranIdSuffix,
                                           Long acctId, BigDecimal monthlyInt,
                                           CardXref xref) {
        int suffix = tranIdSuffix.incrementAndGet();
        String tranId = parmDate + String.format("%06d", suffix);

        Transaction interestTx = new Transaction();
        interestTx.setTranId(tranId);
        interestTx.setTranTypeCd("01");
        interestTx.setTranCatCd(5);
        interestTx.setTranSource("System");
        interestTx.setTranDesc("Int. for a/c " + acctId);
        interestTx.setTranAmt(monthlyInt);
        interestTx.setMerchantId(0L);
        interestTx.setMerchantName("");
        interestTx.setMerchantCity("");
        interestTx.setMerchantZip("");
        if (xref != null) {
            interestTx.setCardNum(xref.getCardNum());
        }

        String timestamp = LocalDateTime.now().format(DB2_TS_FORMAT);
        interestTx.setOrigTs(timestamp);
        interestTx.setProcTs(timestamp);

        transactionRepository.save(interestTx);
        log.info("Written interest transaction {} for account {}", tranId, acctId);
    }
}
