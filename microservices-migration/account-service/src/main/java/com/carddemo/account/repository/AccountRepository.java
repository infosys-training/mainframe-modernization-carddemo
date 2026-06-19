package com.carddemo.account.repository;

import com.carddemo.common.entity.Account;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccountRepository extends JpaRepository<Account, String> {

    List<Account> findByActiveStatus(String activeStatus);

    Page<Account> findByActiveStatus(String activeStatus, Pageable pageable);

    @Query("SELECT a FROM Account a WHERE a.activeStatus = 'Y' ORDER BY a.accountId")
    List<Account> findAllActive();

    @Query("SELECT a FROM Account a WHERE a.groupId = :groupId")
    List<Account> findByGroupId(String groupId);

    @Query("SELECT COUNT(a) FROM Account a WHERE a.activeStatus = 'Y'")
    long countActiveAccounts();
}
