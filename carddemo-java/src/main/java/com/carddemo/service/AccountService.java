package com.carddemo.service;

import com.carddemo.entity.Account;
import com.carddemo.entity.CardXref;
import com.carddemo.entity.Customer;
import com.carddemo.exception.ResourceNotFoundException;
import com.carddemo.repository.AccountRepository;
import com.carddemo.repository.CardXrefRepository;
import com.carddemo.repository.CustomerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
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

    public Account findById(Long acctId) {
        return accountRepository.findById(acctId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Account " + acctId + " NOT found"));
    }

    public List<Account> findAll() {
        return accountRepository.findAll();
    }

    public Optional<Customer> findCustomerForAccount(Long acctId) {
        Optional<CardXref> xref = cardXrefRepository.findByAcctId(acctId);
        if (xref.isPresent()) {
            return customerRepository.findById(xref.get().getCustId());
        }
        return Optional.empty();
    }

    @Transactional
    public Account save(Account account) {
        return accountRepository.save(account);
    }

    @Transactional
    public Account updateBalance(Long acctId, java.math.BigDecimal interestAmount) {
        Account account = findById(acctId);
        account.setCurrBal(account.getCurrBal().add(interestAmount));
        account.setCurrCycCredit(java.math.BigDecimal.ZERO);
        account.setCurrCycDebit(java.math.BigDecimal.ZERO);
        return accountRepository.save(account);
    }
}
