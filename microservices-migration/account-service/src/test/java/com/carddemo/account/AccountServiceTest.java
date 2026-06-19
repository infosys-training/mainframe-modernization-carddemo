package com.carddemo.account;

import com.carddemo.account.repository.AccountRepository;
import com.carddemo.account.repository.CardXrefRepository;
import com.carddemo.account.repository.CustomerRepository;
import com.carddemo.account.service.AccountService;
import com.carddemo.common.entity.Account;
import com.carddemo.common.entity.CardXref;
import com.carddemo.common.entity.Customer;
import com.carddemo.common.exception.BusinessException;
import com.carddemo.common.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock private AccountRepository accountRepository;
    @Mock private CustomerRepository customerRepository;
    @Mock private CardXrefRepository cardXrefRepository;

    @InjectMocks
    private AccountService accountService;

    private Account testAccount;
    private Customer testCustomer;

    @BeforeEach
    void setUp() {
        testAccount = new Account("00000000001");
        testAccount.setActiveStatus("Y");
        testAccount.setCurrentBalance(new BigDecimal("1940.00"));
        testAccount.setCreditLimit(new BigDecimal("20200.00"));

        testCustomer = new Customer("000000001");
        testCustomer.setFirstName("Immanuel");
        testCustomer.setLastName("Kessler");
        testCustomer.setStateCode("NC");
    }

    @Test
    void getAccount_exists_returnsAccount() {
        when(accountRepository.findById("00000000001")).thenReturn(Optional.of(testAccount));
        Account result = accountService.getAccount("00000000001");
        assertEquals("00000000001", result.getAccountId());
        assertTrue(result.isActive());
    }

    @Test
    void getAccount_notFound_throwsException() {
        when(accountRepository.findById("99999999999")).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> accountService.getAccount("99999999999"));
    }

    @Test
    void updateAccount_validUpdate_succeeds() {
        when(accountRepository.findById("00000000001")).thenReturn(Optional.of(testAccount));
        when(accountRepository.save(any())).thenReturn(testAccount);

        Account updates = new Account();
        updates.setCreditLimit(new BigDecimal("25000.00"));
        Account result = accountService.updateAccount("00000000001", updates);

        assertEquals(new BigDecimal("25000.00"), result.getCreditLimit());
    }

    @Test
    void updateAccount_negativeCreditLimit_throwsBusinessException() {
        when(accountRepository.findById("00000000001")).thenReturn(Optional.of(testAccount));

        Account updates = new Account();
        updates.setCreditLimit(new BigDecimal("-100.00"));
        assertThrows(BusinessException.class,
                () -> accountService.updateAccount("00000000001", updates));
    }

    @Test
    void updateCustomer_invalidStateCode_throwsBusinessException() {
        when(customerRepository.findById("000000001")).thenReturn(Optional.of(testCustomer));

        Customer updates = new Customer();
        updates.setStateCode("XX");
        assertThrows(BusinessException.class,
                () -> accountService.updateCustomer("000000001", updates));
    }

    @Test
    void updateCustomer_invalidFicoScore_throwsBusinessException() {
        when(customerRepository.findById("000000001")).thenReturn(Optional.of(testCustomer));

        Customer updates = new Customer();
        updates.setFicoCreditScore(100);
        assertThrows(BusinessException.class,
                () -> accountService.updateCustomer("000000001", updates));
    }

    @Test
    void getCustomerForAccount_returnsLinkedCustomer() {
        CardXref xref = new CardXref("0500024453765740", "000000001", "00000000001");
        when(cardXrefRepository.findByAccountId("00000000001")).thenReturn(List.of(xref));
        when(customerRepository.findById("000000001")).thenReturn(Optional.of(testCustomer));

        Customer result = accountService.getCustomerForAccount("00000000001");
        assertEquals("Immanuel", result.getFirstName());
    }

    @Test
    void activateAccount_setsStatusToY() {
        testAccount.setActiveStatus("N");
        when(accountRepository.findById("00000000001")).thenReturn(Optional.of(testAccount));
        when(accountRepository.save(any())).thenReturn(testAccount);

        Account result = accountService.activateAccount("00000000001");
        assertEquals("Y", result.getActiveStatus());
    }

    @Test
    void deactivateAccount_setsStatusToN() {
        when(accountRepository.findById("00000000001")).thenReturn(Optional.of(testAccount));
        when(accountRepository.save(any())).thenReturn(testAccount);

        accountService.deactivateAccount("00000000001");
        assertEquals("N", testAccount.getActiveStatus());
    }

    @Test
    void account_availableCredit_calculated() {
        assertEquals(new BigDecimal("18260.00"), testAccount.getAvailableCredit());
    }
}
