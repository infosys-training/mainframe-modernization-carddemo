package com.cardemo.repository;

import com.cardemo.model.CardXref;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for CardXref entity.
 * Replaces CICS READ operations on CCXREF and CXACAIX VSAM files.
 * The original COBOL used two paths: by card number (CCXREF) and by account ID (CXACAIX).
 */
@Repository
public interface CardXrefRepository extends JpaRepository<CardXref, String> {

    Optional<CardXref> findByCardNum(String cardNum);

    List<CardXref> findByAcctId(String acctId);

    List<CardXref> findByCustId(String custId);
}
