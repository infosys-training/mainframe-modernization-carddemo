package com.carddemo.transaction.repository;

import com.carddemo.common.entity.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, String> {

    Page<Transaction> findByCardNumber(String cardNumber, Pageable pageable);

    List<Transaction> findByTypeCode(String typeCode);

    @Query("SELECT t FROM Transaction t WHERE t.cardNumber = :cardNumber " +
           "AND t.originTimestamp BETWEEN :start AND :end ORDER BY t.originTimestamp DESC")
    List<Transaction> findByCardNumberAndDateRange(String cardNumber, LocalDateTime start, LocalDateTime end);

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t WHERE t.cardNumber = :cardNumber AND t.typeCode = '01'")
    BigDecimal getTotalPurchaseAmountByCard(String cardNumber);

    @Query("SELECT t FROM Transaction t WHERE t.processedTimestamp IS NULL")
    List<Transaction> findUnprocessedTransactions();
}
