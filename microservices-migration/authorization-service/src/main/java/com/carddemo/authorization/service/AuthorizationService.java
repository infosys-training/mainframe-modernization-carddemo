package com.carddemo.authorization.service;

import com.carddemo.authorization.dto.AuthorizationRequest;
import com.carddemo.authorization.dto.AuthorizationResponse;
import com.carddemo.authorization.entity.AuthorizationRecord;
import com.carddemo.authorization.repository.AuthorizationRepository;
import com.carddemo.common.exception.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Payment authorization service.
 * Migrated from COPAUA0C (auth decision with IMS/MQ/CICS),
 * COPAUS0C (auth summary), COPAUS1C (auth detail), COPAUS2C (fraud marking).
 * Replaces IMS hierarchical lookups with relational queries,
 * MQ message passing with REST API calls.
 */
@Service
@Transactional
public class AuthorizationService {

    private static final int VELOCITY_WINDOW_HOURS = 24;
    private static final int MAX_TRANSACTIONS_PER_DAY = 50;
    private static final BigDecimal MAX_SINGLE_TRANSACTION = new BigDecimal("10000.00");
    private static final BigDecimal MAX_DAILY_AMOUNT = new BigDecimal("25000.00");

    private final AuthorizationRepository authorizationRepository;

    public AuthorizationService(AuthorizationRepository authorizationRepository) {
        this.authorizationRepository = authorizationRepository;
    }

    public AuthorizationResponse authorize(AuthorizationRequest request) {
        AuthorizationRecord record = new AuthorizationRecord();
        record.setCardNumber(request.getCardNumber());
        record.setAmount(request.getAmount());
        record.setMerchantId(request.getMerchantId());
        record.setMerchantName(request.getMerchantName());
        record.setTransactionTypeCode(request.getTransactionTypeCode());
        record.setRequestTimestamp(LocalDateTime.now());

        // Velocity check
        LocalDateTime windowStart = LocalDateTime.now().minusHours(VELOCITY_WINDOW_HOURS);
        long recentCount = authorizationRepository.countRecentByCard(request.getCardNumber(), windowStart);
        if (recentCount >= MAX_TRANSACTIONS_PER_DAY) {
            record.setStatus("DECLINED");
            record.setDeclineReason("Velocity limit exceeded: " + recentCount + " transactions in 24h");
            record.setFraudFlag(true);
            record.setResponseTimestamp(LocalDateTime.now());
            authorizationRepository.save(record);
            return AuthorizationResponse.fraudAlert(record.getId(),
                    "Velocity limit exceeded");
        }

        // Single transaction amount check
        if (request.getAmount().compareTo(MAX_SINGLE_TRANSACTION) > 0) {
            record.setStatus("DECLINED");
            record.setDeclineReason("Amount exceeds single transaction limit");
            record.setFraudFlag(true);
            record.setResponseTimestamp(LocalDateTime.now());
            authorizationRepository.save(record);
            return AuthorizationResponse.fraudAlert(record.getId(),
                    "Amount exceeds limit: " + request.getAmount());
        }

        // Daily cumulative amount check
        BigDecimal dailyTotal = authorizationRepository.getTotalApprovedAmountSince(
                request.getCardNumber(), windowStart);
        if (dailyTotal.add(request.getAmount()).compareTo(MAX_DAILY_AMOUNT) > 0) {
            record.setStatus("DECLINED");
            record.setDeclineReason("Daily cumulative amount limit exceeded");
            record.setFraudFlag(true);
            record.setResponseTimestamp(LocalDateTime.now());
            authorizationRepository.save(record);
            return AuthorizationResponse.fraudAlert(record.getId(),
                    "Daily amount limit exceeded");
        }

        // Approved
        String authCode = generateAuthCode();
        record.setStatus("APPROVED");
        record.setAuthorizationCode(authCode);
        record.setFraudFlag(false);
        record.setResponseTimestamp(LocalDateTime.now());
        authorizationRepository.save(record);

        return AuthorizationResponse.approved(record.getId(), authCode);
    }

    @Transactional(readOnly = true)
    public AuthorizationRecord getAuthorization(Long id) {
        return authorizationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("AuthorizationRecord", id.toString()));
    }

    @Transactional(readOnly = true)
    public Page<AuthorizationRecord> getAuthorizationsByCard(String cardNumber, Pageable pageable) {
        return authorizationRepository.findByCardNumber(cardNumber, pageable);
    }

    @Transactional(readOnly = true)
    public List<AuthorizationRecord> getFraudAlerts() {
        return authorizationRepository.findFraudulent();
    }

    public AuthorizationRecord markAsFraud(Long authorizationId) {
        AuthorizationRecord record = getAuthorization(authorizationId);
        record.setFraudFlag(true);
        return authorizationRepository.save(record);
    }

    public AuthorizationRecord clearFraudFlag(Long authorizationId) {
        AuthorizationRecord record = getAuthorization(authorizationId);
        record.setFraudFlag(false);
        return authorizationRepository.save(record);
    }

    public int purgeExpiredAuthorizations(int olderThanDays) {
        LocalDateTime cutoff = LocalDateTime.now().minusDays(olderThanDays);
        List<AuthorizationRecord> expired = authorizationRepository
                .findRecentApprovedByCard("", cutoff);
        // Purge all records older than cutoff
        List<AuthorizationRecord> allOld = authorizationRepository.findAll().stream()
                .filter(r -> r.getRequestTimestamp() != null && r.getRequestTimestamp().isBefore(cutoff))
                .toList();
        authorizationRepository.deleteAll(allOld);
        return allOld.size();
    }

    private String generateAuthCode() {
        return UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
