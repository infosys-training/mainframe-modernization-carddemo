package com.carddemo.batch.config;

import com.carddemo.entity.CardXref;
import com.carddemo.entity.Transaction;
import com.carddemo.repository.AccountRepository;
import com.carddemo.repository.CardXrefRepository;
import com.carddemo.repository.TransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import jakarta.persistence.EntityManagerFactory;
import java.util.Optional;

@Configuration
public class DailyTransactionPostingJobConfig {

    private static final Logger log = LoggerFactory.getLogger(DailyTransactionPostingJobConfig.class);

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final EntityManagerFactory entityManagerFactory;
    private final CardXrefRepository cardXrefRepository;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    public DailyTransactionPostingJobConfig(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager,
            EntityManagerFactory entityManagerFactory,
            CardXrefRepository cardXrefRepository,
            AccountRepository accountRepository,
            TransactionRepository transactionRepository) {
        this.jobRepository = jobRepository;
        this.transactionManager = transactionManager;
        this.entityManagerFactory = entityManagerFactory;
        this.cardXrefRepository = cardXrefRepository;
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

    @Bean
    public Job dailyTransactionPostingJob() {
        return new JobBuilder("dailyTransactionPostingJob", jobRepository)
                .start(dailyTransactionPostingStep())
                .build();
    }

    @Bean
    public Step dailyTransactionPostingStep() {
        return new StepBuilder("dailyTransactionPostingStep", jobRepository)
                .<Transaction, Transaction>chunk(10, transactionManager)
                .reader(dailyTransactionReader())
                .processor(dailyTransactionProcessor())
                .writer(dailyTransactionWriter())
                .build();
    }

    @Bean
    public JpaPagingItemReader<Transaction> dailyTransactionReader() {
        return new JpaPagingItemReaderBuilder<Transaction>()
                .name("dailyTransactionReader")
                .entityManagerFactory(entityManagerFactory)
                .queryString("SELECT t FROM Transaction t ORDER BY t.tranId")
                .pageSize(10)
                .build();
    }

    @Bean
    public ItemProcessor<Transaction, Transaction> dailyTransactionProcessor() {
        return transaction -> {
            log.info("Processing transaction: {}", transaction.getTranId());

            String cardNum = transaction.getCardNum();
            Optional<CardXref> xref = cardXrefRepository.findById(cardNum);

            if (xref.isEmpty()) {
                log.warn("CARD NUMBER {} COULD NOT BE VERIFIED. SKIPPING TRANSACTION ID-{}",
                        cardNum, transaction.getTranId());
                return null;
            }

            log.info("SUCCESSFUL READ OF XREF - CARD NUMBER: {} ACCOUNT ID: {} CUSTOMER ID: {}",
                    xref.get().getCardNum(), xref.get().getAcctId(), xref.get().getCustId());

            Long acctId = xref.get().getAcctId();
            boolean accountExists = accountRepository.existsById(acctId);

            if (!accountExists) {
                log.warn("ACCOUNT {} NOT FOUND", acctId);
                return null;
            }

            log.info("SUCCESSFUL READ OF ACCOUNT FILE");
            return transaction;
        };
    }

    @Bean
    public ItemWriter<Transaction> dailyTransactionWriter() {
        return transactions -> {
            for (Transaction transaction : transactions) {
                transactionRepository.save(transaction);
                log.info("Posted transaction: {}", transaction.getTranId());
            }
        };
    }
}
