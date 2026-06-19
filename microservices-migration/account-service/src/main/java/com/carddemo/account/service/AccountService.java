package com.carddemo.account.service;

import com.carddemo.account.repository.AccountRepository;
import com.carddemo.account.repository.CardXrefRepository;
import com.carddemo.account.repository.CustomerRepository;
import com.carddemo.common.entity.Account;
import com.carddemo.common.entity.CardXref;
import com.carddemo.common.entity.Customer;
import com.carddemo.common.exception.BusinessException;
import com.carddemo.common.exception.ResourceNotFoundException;
import com.carddemo.common.validation.CardDemoValidator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Account management service.
 * Migrated from COACTUPC.cbl (4,236 LOC) and COACTVWC.cbl (941 LOC).
 * Handles account view, update, and customer data management.
 */
@Service
@Transactional
public class AccountService {

    private final AccountRepository accountRepository;
    private final CustomerRepository customerRepository;
    private final CardXrefRepository cardXrefRepository;

    public AccountService(AccountRepository accountRepository,
                          CustomerRepository customerRepository,
                          CardXrefRepository cardXrefRepository) {
        this.accountRepository = accountRepository;
        this.customerRepository = customerRepository;
        this.cardXrefRepository = cardXrefRepository;
    }

    @Transactional(readOnly = true)
    public Account getAccount(String accountId) {
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account", accountId));
    }

    @Transactional(readOnly = true)
    public Page<Account> listAccounts(Pageable pageable) {
        return accountRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Page<Account> listActiveAccounts(Pageable pageable) {
        return accountRepository.findByActiveStatus("Y", pageable);
    }

    public Account updateAccount(String accountId, Account updates) {
        Account account = getAccount(accountId);

        if (updates.getCreditLimit() != null) {
            if (updates.getCreditLimit().compareTo(BigDecimal.ZERO) < 0) {
                throw new BusinessException("Credit limit cannot be negative");
            }
            account.setCreditLimit(updates.getCreditLimit());
        }
        if (updates.getCashCreditLimit() != null) {
            account.setCashCreditLimit(updates.getCashCreditLimit());
        }
        if (updates.getExpirationDate() != null) {
            account.setExpirationDate(updates.getExpirationDate());
        }
        if (updates.getActiveStatus() != null) {
            account.setActiveStatus(updates.getActiveStatus());
        }
        if (updates.getGroupId() != null) {
            account.setGroupId(updates.getGroupId());
        }
        if (updates.getAddressZip() != null) {
            account.setAddressZip(updates.getAddressZip());
        }

        return accountRepository.save(account);
    }

    public Account activateAccount(String accountId) {
        Account account = getAccount(accountId);
        account.setActiveStatus("Y");
        return accountRepository.save(account);
    }

    public Account deactivateAccount(String accountId) {
        Account account = getAccount(accountId);
        account.setActiveStatus("N");
        return accountRepository.save(account);
    }

    @Transactional(readOnly = true)
    public Customer getCustomer(String customerId) {
        return customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", customerId));
    }

    @Transactional(readOnly = true)
    public Page<Customer> listCustomers(Pageable pageable) {
        return customerRepository.findAll(pageable);
    }

    public Customer updateCustomer(String customerId, Customer updates) {
        Customer customer = getCustomer(customerId);

        if (updates.getFirstName() != null) customer.setFirstName(updates.getFirstName());
        if (updates.getLastName() != null) customer.setLastName(updates.getLastName());
        if (updates.getMiddleName() != null) customer.setMiddleName(updates.getMiddleName());
        if (updates.getAddressLine1() != null) customer.setAddressLine1(updates.getAddressLine1());
        if (updates.getAddressLine2() != null) customer.setAddressLine2(updates.getAddressLine2());
        if (updates.getAddressLine3() != null) customer.setAddressLine3(updates.getAddressLine3());

        if (updates.getStateCode() != null) {
            if (!CardDemoValidator.isValidStateCode(updates.getStateCode())) {
                throw new BusinessException("Invalid state code: " + updates.getStateCode());
            }
            customer.setStateCode(updates.getStateCode());
        }
        if (updates.getZip() != null) customer.setZip(updates.getZip());
        if (updates.getPhoneNumber1() != null) customer.setPhoneNumber1(updates.getPhoneNumber1());
        if (updates.getPhoneNumber2() != null) customer.setPhoneNumber2(updates.getPhoneNumber2());

        if (updates.getFicoCreditScore() != null) {
            if (!CardDemoValidator.isValidFicoScore(updates.getFicoCreditScore())) {
                throw new BusinessException("FICO score must be between 300 and 850");
            }
            customer.setFicoCreditScore(updates.getFicoCreditScore());
        }

        return customerRepository.save(customer);
    }

    @Transactional(readOnly = true)
    public Customer getCustomerForAccount(String accountId) {
        List<CardXref> xrefs = cardXrefRepository.findByAccountId(accountId);
        if (xrefs.isEmpty()) {
            throw new ResourceNotFoundException("No customer linked to account: " + accountId);
        }
        return getCustomer(xrefs.get(0).getCustomerId());
    }

    @Transactional(readOnly = true)
    public List<Account> getAccountsForCustomer(String customerId) {
        List<CardXref> xrefs = cardXrefRepository.findByCustomerId(customerId);
        return xrefs.stream()
                .map(xref -> accountRepository.findById(xref.getAccountId()).orElse(null))
                .filter(a -> a != null)
                .toList();
    }
}
