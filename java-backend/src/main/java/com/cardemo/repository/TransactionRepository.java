package com.cardemo.repository;

import com.cardemo.model.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for Transaction entity.
 * Replaces CICS STARTBR/READNEXT/READPREV/READ/WRITE operations on TRANSACT VSAM KSDS file.
 * The original COBOL program COTRN00C used STARTBR/READNEXT for paginated browsing.
 */
@Repository
public interface TransactionRepository extends JpaRepository<Transaction, String> {

    Page<Transaction> findAllByOrderByTranIdAsc(Pageable pageable);

    Page<Transaction> findByTranIdGreaterThanEqualOrderByTranIdAsc(String tranId, Pageable pageable);

    List<Transaction> findByCardNum(String cardNum);

    @Query("SELECT t FROM Transaction t WHERE t.cardNum IN " +
           "(SELECT cx.cardNum FROM CardXref cx WHERE cx.acctId = :acctId) " +
           "ORDER BY t.tranId ASC")
    List<Transaction> findByAccountId(@Param("acctId") String acctId);

    @Query("SELECT MAX(t.tranId) FROM Transaction t")
    String findMaxTranId();
}
