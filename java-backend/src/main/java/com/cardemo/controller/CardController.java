package com.cardemo.controller;

import com.cardemo.dto.ApiResponse;
import com.cardemo.model.Card;
import com.cardemo.model.Customer;
import com.cardemo.service.CardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Card controller - migrated from COBOL programs COCRDLIC.cbl, COCRDSLC.cbl, COCRDUPC.cbl.
 *
 * COCRDLIC (1460 lines): Credit card list with pagination and selection.
 *   - Original CICS transaction: CCLI
 *   - Admin sees all cards; users see only their account's cards.
 *   - Used CARDDAT file with CARDAIX alternate index for account-based access.
 *
 * COCRDSLC (888 lines): Credit card detail view with customer info.
 *   - Original CICS transaction: CCDL
 *   - Reads card record and associated customer via cross-reference.
 *
 * COCRDUPC: Credit card update.
 *   - Original CICS transaction: CCUP
 *   - Validates and updates card status, embossed name, expiration date.
 */
@RestController
@RequestMapping("/api/cards")
@Tag(name = "Credit Cards", description = "Credit card management (migrated from COCRDLIC/COCRDSLC/COCRDUPC)")
public class CardController {

    private final CardService cardService;

    public CardController(CardService cardService) {
        this.cardService = cardService;
    }

    @GetMapping
    @Operation(summary = "List credit cards",
               description = "List all cards or filter by account. " +
                             "Migrated from COCRDLIC card list screen.")
    public ResponseEntity<ApiResponse<List<Card>>> listCards(
            @RequestParam(required = false) String acctId) {
        List<Card> cards;
        if (acctId != null && !acctId.isBlank()) {
            cards = cardService.getCardsByAccountId(acctId);
        } else {
            cards = cardService.getAllCards();
        }
        return ResponseEntity.ok(ApiResponse.ok(cards));
    }

    @GetMapping("/{cardNum}")
    @Operation(summary = "View credit card detail",
               description = "Migrated from COCRDSLC card detail screen.")
    public ResponseEntity<ApiResponse<Card>> getCard(@PathVariable String cardNum) {
        return ResponseEntity.ok(ApiResponse.ok(cardService.getCardByNumber(cardNum)));
    }

    @GetMapping("/{cardNum}/customer")
    @Operation(summary = "View customer for card",
               description = "Get customer info associated with card via cross-reference.")
    public ResponseEntity<ApiResponse<Customer>> getCustomerForCard(@PathVariable String cardNum) {
        return ResponseEntity.ok(ApiResponse.ok(cardService.getCustomerForCard(cardNum)));
    }

    @PutMapping("/{cardNum}")
    @Operation(summary = "Update credit card",
               description = "Migrated from COCRDUPC card update. " +
                             "Validates status (Y/N), embossed name, expiration date.")
    public ResponseEntity<ApiResponse<Card>> updateCard(
            @PathVariable String cardNum, @RequestBody Card card) {
        return ResponseEntity.ok(ApiResponse.ok(
                "Card updated successfully",
                cardService.updateCard(cardNum, card)));
    }
}
