package com.cardemo.service;

import com.cardemo.exception.BusinessValidationException;
import com.cardemo.exception.ResourceNotFoundException;
import com.cardemo.model.Account;
import com.cardemo.model.Customer;
import com.cardemo.model.CardXref;
import com.cardemo.repository.AccountRepository;
import com.cardemo.repository.CardXrefRepository;
import com.cardemo.repository.CustomerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Account service - migrated from COBOL programs COACTUPC.cbl and COACTVWC.cbl.
 *
 * COACTUPC (4237 lines) handled account and customer update with extensive
 * field-by-field validation of:
 *   - Account status (Y/N), credit limits, balances
 *   - Customer personal data (name, SSN, DOB, phone, address)
 *   - Date validation via CSUTLDTC subroutine
 *   - Read-for-update pattern using CICS READ UPDATE / REWRITE
 *
 * COACTVWC handled read-only account viewing.
 */
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

    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }

    public Account getAccountById(String acctId) {
        return accountRepository.findById(acctId)
                .orElseThrow(() -> new ResourceNotFoundException("Account", acctId));
    }

    /**
     * Update account - migrated from COACTUPC PROCESS-ENTER-KEY and
     * EDIT-ACCT-DATA / EDIT-CUST-DATA / UPDATE-ACCTFILE / UPDATE-CUSTFILE paragraphs.
     *
     * The original COBOL performed field-by-field comparison between
     * screen values and database values to detect changes, then validated
     * each changed field individually before issuing CICS REWRITE.
     */
    @Transactional
    public Account updateAccount(String acctId, Account updated) {
        Account existing = getAccountById(acctId);

        if (updated.getActiveStatus() != null) {
            String status = updated.getActiveStatus().toUpperCase();
            if (!"Y".equals(status) && !"N".equals(status)) {
                throw new BusinessValidationException("Account status must be Y or N");
            }
            existing.setActiveStatus(status);
        }
        if (updated.getCreditLimit() != null) {
            existing.setCreditLimit(updated.getCreditLimit());
        }
        if (updated.getCashCreditLimit() != null) {
            existing.setCashCreditLimit(updated.getCashCreditLimit());
        }
        if (updated.getCurrentBalance() != null) {
            existing.setCurrentBalance(updated.getCurrentBalance());
        }
        if (updated.getCurrentCycleCredit() != null) {
            existing.setCurrentCycleCredit(updated.getCurrentCycleCredit());
        }
        if (updated.getCurrentCycleDebit() != null) {
            existing.setCurrentCycleDebit(updated.getCurrentCycleDebit());
        }
        if (updated.getExpirationDate() != null) {
            existing.setExpirationDate(updated.getExpirationDate());
        }
        if (updated.getReissueDate() != null) {
            existing.setReissueDate(updated.getReissueDate());
        }
        if (updated.getAddressZip() != null) {
            existing.setAddressZip(updated.getAddressZip());
        }
        if (updated.getGroupId() != null) {
            existing.setGroupId(updated.getGroupId());
        }

        return accountRepository.save(existing);
    }

    /**
     * Get customer associated with an account via card cross-reference.
     * Original COBOL: COACTUPC read CARDXREF to find customer for account.
     */
    public Customer getCustomerForAccount(String acctId) {
        List<CardXref> xrefs = cardXrefRepository.findByAcctId(acctId);
        if (xrefs.isEmpty()) {
            throw new ResourceNotFoundException("No card cross-reference found for account " + acctId);
        }
        String custId = xrefs.get(0).getCustId();
        return customerRepository.findById(custId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", custId));
    }

    @Transactional
    public Customer updateCustomerForAccount(String acctId, Customer updated) {
        Customer existing = getCustomerForAccount(acctId);

        if (updated.getFirstName() != null) {
            existing.setFirstName(updated.getFirstName());
        }
        if (updated.getMiddleName() != null) {
            existing.setMiddleName(updated.getMiddleName());
        }
        if (updated.getLastName() != null) {
            existing.setLastName(updated.getLastName());
        }
        if (updated.getAddressLine1() != null) {
            existing.setAddressLine1(updated.getAddressLine1());
        }
        if (updated.getAddressLine2() != null) {
            existing.setAddressLine2(updated.getAddressLine2());
        }
        if (updated.getAddressLine3() != null) {
            existing.setAddressLine3(updated.getAddressLine3());
        }
        if (updated.getStateCode() != null) {
            existing.setStateCode(updated.getStateCode());
        }
        if (updated.getCountryCode() != null) {
            existing.setCountryCode(updated.getCountryCode());
        }
        if (updated.getAddressZip() != null) {
            existing.setAddressZip(updated.getAddressZip());
        }
        if (updated.getPhoneNum1() != null) {
            existing.setPhoneNum1(updated.getPhoneNum1());
        }
        if (updated.getPhoneNum2() != null) {
            existing.setPhoneNum2(updated.getPhoneNum2());
        }
        if (updated.getGovtIssuedId() != null) {
            existing.setGovtIssuedId(updated.getGovtIssuedId());
        }
        if (updated.getDateOfBirth() != null) {
            existing.setDateOfBirth(updated.getDateOfBirth());
        }
        if (updated.getEftAccountId() != null) {
            existing.setEftAccountId(updated.getEftAccountId());
        }
        if (updated.getFicoCreditScore() != null) {
            existing.setFicoCreditScore(updated.getFicoCreditScore());
        }

        return customerRepository.save(existing);
    }
}
