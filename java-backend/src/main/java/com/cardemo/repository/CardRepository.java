package com.cardemo.repository;

import com.cardemo.model.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for Card entity.
 * Replaces CICS READ/STARTBR/READNEXT operations on CARDDAT VSAM KSDS file.
 */
@Repository
public interface CardRepository extends JpaRepository<Card, String> {

    List<Card> findByAcctId(String acctId);

    List<Card> findByActiveStatus(String activeStatus);

    List<Card> findByAcctIdAndActiveStatus(String acctId, String activeStatus);
}
