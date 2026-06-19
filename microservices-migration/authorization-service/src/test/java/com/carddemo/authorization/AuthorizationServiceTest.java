package com.carddemo.authorization;

import com.carddemo.authorization.dto.AuthorizationRequest;
import com.carddemo.authorization.dto.AuthorizationResponse;
import com.carddemo.authorization.entity.AuthorizationRecord;
import com.carddemo.authorization.repository.AuthorizationRepository;
import com.carddemo.authorization.service.AuthorizationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthorizationServiceTest {

    @Mock private AuthorizationRepository authorizationRepository;

    @InjectMocks
    private AuthorizationService authorizationService;

    private AuthorizationRequest validRequest;

    @BeforeEach
    void setUp() {
        validRequest = new AuthorizationRequest();
        validRequest.setCardNumber("0500024453765740");
        validRequest.setAmount(new BigDecimal("100.00"));
        validRequest.setMerchantId("800000000");
        validRequest.setMerchantName("Test Merchant");
        validRequest.setTransactionTypeCode("01");
    }

    @Test
    void authorize_validRequest_approved() {
        when(authorizationRepository.countRecentByCard(anyString(), any())).thenReturn(0L);
        when(authorizationRepository.getTotalApprovedAmountSince(anyString(), any()))
                .thenReturn(BigDecimal.ZERO);
        when(authorizationRepository.save(any())).thenAnswer(i -> {
            AuthorizationRecord r = i.getArgument(0);
            r.setId(1L);
            return r;
        });

        AuthorizationResponse response = authorizationService.authorize(validRequest);
        assertEquals("APPROVED", response.getStatus());
        assertNotNull(response.getAuthorizationCode());
        assertFalse(response.isFraudDetected());
    }

    @Test
    void authorize_velocityLimitExceeded_declined() {
        when(authorizationRepository.countRecentByCard(anyString(), any())).thenReturn(51L);
        when(authorizationRepository.save(any())).thenAnswer(i -> {
            AuthorizationRecord r = i.getArgument(0);
            r.setId(2L);
            return r;
        });

        AuthorizationResponse response = authorizationService.authorize(validRequest);
        assertEquals("DECLINED", response.getStatus());
        assertTrue(response.isFraudDetected());
    }

    @Test
    void authorize_singleTransactionLimitExceeded_declined() {
        validRequest.setAmount(new BigDecimal("15000.00"));
        when(authorizationRepository.countRecentByCard(anyString(), any())).thenReturn(0L);
        when(authorizationRepository.save(any())).thenAnswer(i -> {
            AuthorizationRecord r = i.getArgument(0);
            r.setId(3L);
            return r;
        });

        AuthorizationResponse response = authorizationService.authorize(validRequest);
        assertEquals("DECLINED", response.getStatus());
        assertTrue(response.isFraudDetected());
    }

    @Test
    void authorize_dailyAmountLimitExceeded_declined() {
        when(authorizationRepository.countRecentByCard(anyString(), any())).thenReturn(5L);
        when(authorizationRepository.getTotalApprovedAmountSince(anyString(), any()))
                .thenReturn(new BigDecimal("24950.00"));
        when(authorizationRepository.save(any())).thenAnswer(i -> {
            AuthorizationRecord r = i.getArgument(0);
            r.setId(4L);
            return r;
        });

        AuthorizationResponse response = authorizationService.authorize(validRequest);
        assertEquals("DECLINED", response.getStatus());
        assertTrue(response.isFraudDetected());
    }

    @Test
    void authorizationRecord_statusChecks() {
        AuthorizationRecord record = new AuthorizationRecord();
        record.setStatus("APPROVED");
        assertTrue(record.isApproved());
        assertFalse(record.isDeclined());

        record.setStatus("DECLINED");
        assertTrue(record.isDeclined());
        assertFalse(record.isApproved());
    }

    @Test
    void authorizationResponse_factoryMethods() {
        AuthorizationResponse approved = AuthorizationResponse.approved(1L, "ABC123");
        assertEquals("APPROVED", approved.getStatus());
        assertEquals("ABC123", approved.getAuthorizationCode());

        AuthorizationResponse declined = AuthorizationResponse.declined(2L, "Insufficient funds");
        assertEquals("DECLINED", declined.getStatus());
        assertEquals("Insufficient funds", declined.getDeclineReason());

        AuthorizationResponse fraud = AuthorizationResponse.fraudAlert(3L, "Velocity");
        assertTrue(fraud.isFraudDetected());
    }
}
