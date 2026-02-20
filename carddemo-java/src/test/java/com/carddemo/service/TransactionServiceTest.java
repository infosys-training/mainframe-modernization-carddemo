package com.carddemo.service;

import com.carddemo.entity.Transaction;
import com.carddemo.exception.ResourceNotFoundException;
import com.carddemo.repository.TransactionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private TransactionService transactionService;

    @Test
    void findById_success() {
        Transaction tx = new Transaction();
        tx.setTranId("0000000000000001");
        tx.setTranTypeCd("01");
        tx.setTranCatCd(5001);
        tx.setTranAmt(new BigDecimal("125.50"));
        tx.setCardNum("4111111111111111");
        tx.setMerchantName("Amazon.com");

        when(transactionRepository.findById("0000000000000001")).thenReturn(Optional.of(tx));

        Transaction result = transactionService.findById("0000000000000001");
        assertEquals("0000000000000001", result.getTranId());
        assertEquals("01", result.getTranTypeCd());
        assertEquals(new BigDecimal("125.50"), result.getTranAmt());
        assertEquals("Amazon.com", result.getMerchantName());
    }

    @Test
    void findById_notFound_throwsException() {
        when(transactionRepository.findById("INVALID")).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> transactionService.findById("INVALID"));
    }

    @Test
    void findById_emptyId_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> transactionService.findById(""));
    }

    @Test
    void findById_nullId_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> transactionService.findById(null));
    }
}
