package com.cardemo.repository;

import com.cardemo.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for Account entity.
 * Replaces CICS READ/WRITE/REWRITE operations on ACCTDAT VSAM KSDS file.
 */
@Repository
public interface AccountRepository extends JpaRepository<Account, String> {

    List<Account> findByActiveStatus(String activeStatus);

    List<Account> findByGroupId(String groupId);
}
