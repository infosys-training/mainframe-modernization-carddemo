package com.carddemo.service;

import com.carddemo.entity.Card;
import com.carddemo.entity.CardXref;
import com.carddemo.exception.ResourceNotFoundException;
import com.carddemo.repository.CardRepository;
import com.carddemo.repository.CardXrefRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CardService {

    private final CardRepository cardRepository;
    private final CardXrefRepository cardXrefRepository;

    public CardService(CardRepository cardRepository, CardXrefRepository cardXrefRepository) {
        this.cardRepository = cardRepository;
        this.cardXrefRepository = cardXrefRepository;
    }

    public Card findByCardNum(String cardNum) {
        return cardRepository.findById(cardNum)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Card number " + cardNum + " NOT found"));
    }

    public List<Card> findByAcctId(Long acctId) {
        return cardRepository.findByAcctId(acctId);
    }

    public Optional<CardXref> lookupXref(String cardNum) {
        return cardXrefRepository.findById(cardNum);
    }

    public Optional<CardXref> lookupXrefByAcctId(Long acctId) {
        return cardXrefRepository.findByAcctId(acctId);
    }
}
