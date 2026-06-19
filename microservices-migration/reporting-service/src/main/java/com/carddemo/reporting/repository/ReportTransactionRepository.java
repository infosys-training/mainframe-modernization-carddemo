package com.carddemo.reporting.repository;

import com.carddemo.common.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReportTransactionRepository extends JpaRepository<Transaction, String> {

    @Query("SELECT t FROM Transaction t WHERE t.cardNumber = :cardNumber " +
           "AND t.originTimestamp BETWEEN :start AND :end ORDER BY t.originTimestamp")
    List<Transaction> findByCardAndDateRange(String cardNumber, LocalDateTime start, LocalDateTime end);

    @Query("SELECT t FROM Transaction t WHERE t.originTimestamp BETWEEN :start AND :end ORDER BY t.originTimestamp")
    List<Transaction> findByDateRange(LocalDateTime start, LocalDateTime end);
}
