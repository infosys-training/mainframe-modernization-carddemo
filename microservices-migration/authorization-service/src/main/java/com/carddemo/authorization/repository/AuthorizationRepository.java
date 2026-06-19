package com.carddemo.authorization.repository;

import com.carddemo.authorization.entity.AuthorizationRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AuthorizationRepository extends JpaRepository<AuthorizationRecord, Long> {

    Page<AuthorizationRecord> findByCardNumber(String cardNumber, Pageable pageable);

    List<AuthorizationRecord> findByAccountId(String accountId);

    List<AuthorizationRecord> findByStatus(String status);

    @Query("SELECT a FROM AuthorizationRecord a WHERE a.cardNumber = :cardNumber " +
           "AND a.requestTimestamp > :since AND a.status = 'APPROVED'")
    List<AuthorizationRecord> findRecentApprovedByCard(String cardNumber, LocalDateTime since);

    @Query("SELECT COALESCE(SUM(a.amount), 0) FROM AuthorizationRecord a " +
           "WHERE a.cardNumber = :cardNumber AND a.requestTimestamp > :since AND a.status = 'APPROVED'")
    BigDecimal getTotalApprovedAmountSince(String cardNumber, LocalDateTime since);

    @Query("SELECT a FROM AuthorizationRecord a WHERE a.fraudFlag = true ORDER BY a.requestTimestamp DESC")
    List<AuthorizationRecord> findFraudulent();

    @Query("SELECT COUNT(a) FROM AuthorizationRecord a WHERE a.cardNumber = :cardNumber " +
           "AND a.requestTimestamp > :since")
    long countRecentByCard(String cardNumber, LocalDateTime since);
}
