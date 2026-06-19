package com.carddemo.card.service;

import com.carddemo.card.repository.CardRepository;
import com.carddemo.common.entity.Card;
import com.carddemo.common.exception.BusinessException;
import com.carddemo.common.exception.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Card management service.
 * Migrated from COCRDLIC.cbl (card list), COCRDSLC.cbl (card view), COCRDUPC.cbl (card update).
 */
@Service
@Transactional
public class CardService {

    private final CardRepository cardRepository;

    public CardService(CardRepository cardRepository) {
        this.cardRepository = cardRepository;
    }

    @Transactional(readOnly = true)
    public Card getCard(String cardNumber) {
        return cardRepository.findById(cardNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Card", cardNumber));
    }

    @Transactional(readOnly = true)
    public Page<Card> listCards(Pageable pageable) {
        return cardRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public List<Card> getCardsByAccount(String accountId) {
        return cardRepository.findByAccountId(accountId);
    }

    @Transactional(readOnly = true)
    public Page<Card> getCardsByAccount(String accountId, Pageable pageable) {
        return cardRepository.findByAccountId(accountId, pageable);
    }

    public Card updateCard(String cardNumber, Card updates) {
        Card card = getCard(cardNumber);

        if (updates.getEmbossedName() != null) card.setEmbossedName(updates.getEmbossedName());
        if (updates.getExpirationDate() != null) card.setExpirationDate(updates.getExpirationDate());
        if (updates.getActiveStatus() != null) card.setActiveStatus(updates.getActiveStatus());

        return cardRepository.save(card);
    }

    public Card activateCard(String cardNumber) {
        Card card = getCard(cardNumber);
        card.setActiveStatus("Y");
        return cardRepository.save(card);
    }

    public Card deactivateCard(String cardNumber) {
        Card card = getCard(cardNumber);
        card.setActiveStatus("N");
        return cardRepository.save(card);
    }

    public Card createCard(Card card) {
        if (cardRepository.existsById(card.getCardNumber())) {
            throw new BusinessException("Card already exists: " + card.getCardNumber());
        }
        return cardRepository.save(card);
    }
}
