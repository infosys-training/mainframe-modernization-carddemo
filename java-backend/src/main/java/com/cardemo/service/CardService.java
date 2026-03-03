package com.cardemo.service;

import com.cardemo.exception.BusinessValidationException;
import com.cardemo.exception.ResourceNotFoundException;
import com.cardemo.model.Card;
import com.cardemo.model.CardXref;
import com.cardemo.model.Customer;
import com.cardemo.repository.CardRepository;
import com.cardemo.repository.CardXrefRepository;
import com.cardemo.repository.CustomerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Card service - migrated from COBOL programs COCRDLIC.cbl, COCRDSLC.cbl, COCRDUPC.cbl.
 *
 * COCRDLIC (1460 lines): List credit cards with STARTBR/READNEXT pagination.
 *   - Admin users see all cards; regular users see only their account's cards.
 *   - Selection: 'S' for detail view, 'U' for update (XCTL to COCRDSLC/COCRDUPC).
 *
 * COCRDSLC (888 lines): View credit card detail with customer info.
 *   - Reads CARDDAT by card number key, then reads CUSTDAT for customer info.
 *
 * COCRDUPC: Update credit card details.
 *   - Validates card status, expiration date, embossed name.
 *   - Uses CICS READ UPDATE / REWRITE pattern.
 */
@Service
public class CardService {

    private final CardRepository cardRepository;
    private final CardXrefRepository cardXrefRepository;
    private final CustomerRepository customerRepository;

    public CardService(CardRepository cardRepository,
                       CardXrefRepository cardXrefRepository,
                       CustomerRepository customerRepository) {
        this.cardRepository = cardRepository;
        this.cardXrefRepository = cardXrefRepository;
        this.customerRepository = customerRepository;
    }

    public List<Card> getAllCards() {
        return cardRepository.findAll();
    }

    /**
     * Get cards by account ID - migrated from COCRDLIC filtering logic.
     * Original COBOL used CARDAIX (alternate index path) to browse by account.
     */
    public List<Card> getCardsByAccountId(String acctId) {
        return cardRepository.findByAcctId(acctId);
    }

    /**
     * Get card detail - migrated from COCRDSLC 9000-READ-DATA paragraph.
     * Original: CICS READ DATASET('CARDDAT') INTO(CARD-RECORD) RIDFLD(card-key).
     */
    public Card getCardByNumber(String cardNum) {
        return cardRepository.findById(cardNum)
                .orElseThrow(() -> new ResourceNotFoundException("Card", cardNum));
    }

    /**
     * Get customer info for a card - part of COCRDSLC detail view.
     * Original: Read CARDXREF to get customer ID, then read CUSTDAT.
     */
    public Customer getCustomerForCard(String cardNum) {
        CardXref xref = cardXrefRepository.findByCardNum(cardNum)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Card cross-reference not found for card " + cardNum));
        return customerRepository.findById(xref.getCustId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer", xref.getCustId()));
    }

    /**
     * Update card - migrated from COCRDUPC update logic.
     * Original COBOL validated status ('Y'/'N'), embossed name, and expiration date.
     */
    @Transactional
    public Card updateCard(String cardNum, Card updated) {
        Card existing = getCardByNumber(cardNum);

        if (updated.getActiveStatus() != null) {
            String status = updated.getActiveStatus().toUpperCase();
            if (!"Y".equals(status) && !"N".equals(status)) {
                throw new BusinessValidationException("Card status must be Y or N");
            }
            existing.setActiveStatus(status);
        }
        if (updated.getEmbossedName() != null) {
            existing.setEmbossedName(updated.getEmbossedName());
        }
        if (updated.getExpirationDate() != null) {
            existing.setExpirationDate(updated.getExpirationDate());
        }

        return cardRepository.save(existing);
    }
}
