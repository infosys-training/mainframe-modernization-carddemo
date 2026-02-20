package com.carddemo.service;

import com.carddemo.entity.Account;
import com.carddemo.entity.CardXref;
import com.carddemo.entity.Customer;
import com.carddemo.exception.ResourceNotFoundException;
import com.carddemo.repository.AccountRepository;
import com.carddemo.repository.CardXrefRepository;
import com.carddemo.repository.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private CardXrefRepository cardXrefRepository;

    @InjectMocks
    private AccountService accountService;

    @Test
    void findById_success() {
        Account account = new Account();
        account.setAcctId(10000000001L);
        account.setActiveStatus("Y");
        account.setCurrBal(new BigDecimal("5000.00"));
        account.setCreditLimit(new BigDecimal("15000.00"));

        when(accountRepository.findById(10000000001L)).thenReturn(Optional.of(account));

        Account result = accountService.findById(10000000001L);
        assertEquals(10000000001L, result.getAcctId());
        assertEquals("Y", result.getActiveStatus());
        assertEquals(new BigDecimal("5000.00"), result.getCurrBal());
    }

    @Test
    void findById_notFound_throwsException() {
        when(accountRepository.findById(99999999999L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> accountService.findById(99999999999L));
    }

    @Test
    void findCustomerForAccount_success() {
        CardXref xref = new CardXref();
        xref.setCardNum("4111111111111111");
        xref.setCustId(1L);
        xref.setAcctId(10000000001L);

        Customer customer = new Customer();
        customer.setCustId(1L);
        customer.setFirstName("John");
        customer.setLastName("Smith");

        when(cardXrefRepository.findByAcctId(10000000001L)).thenReturn(Optional.of(xref));
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));

        Optional<Customer> result = accountService.findCustomerForAccount(10000000001L);
        assertTrue(result.isPresent());
        assertEquals("John", result.get().getFirstName());
    }

    @Test
    void updateBalance_success() {
        Account account = new Account();
        account.setAcctId(10000000001L);
        account.setCurrBal(new BigDecimal("5000.00"));
        account.setCurrCycCredit(new BigDecimal("500.00"));
        account.setCurrCycDebit(new BigDecimal("1200.00"));

        when(accountRepository.findById(10000000001L)).thenReturn(Optional.of(account));
        when(accountRepository.save(account)).thenReturn(account);

        Account result = accountService.updateBalance(10000000001L, new BigDecimal("79.13"));
        assertEquals(new BigDecimal("5079.13"), result.getCurrBal());
        assertEquals(BigDecimal.ZERO, result.getCurrCycCredit());
        assertEquals(BigDecimal.ZERO, result.getCurrCycDebit());
    }
}
