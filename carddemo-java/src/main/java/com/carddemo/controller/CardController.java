package com.carddemo.controller;

import com.carddemo.entity.Card;
import com.carddemo.entity.CardXref;
import com.carddemo.service.CardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/cards")
public class CardController {

    private final CardService cardService;

    public CardController(CardService cardService) {
        this.cardService = cardService;
    }

    @GetMapping("/{cardNum}")
    public ResponseEntity<Card> getCard(@PathVariable String cardNum) {
        return ResponseEntity.ok(cardService.findByCardNum(cardNum));
    }

    @GetMapping("/by-account/{acctId}")
    public ResponseEntity<List<Card>> getCardsByAccount(@PathVariable Long acctId) {
        return ResponseEntity.ok(cardService.findByAcctId(acctId));
    }

    @GetMapping("/xref/{cardNum}")
    public ResponseEntity<CardXref> lookupXref(@PathVariable String cardNum) {
        return cardService.lookupXref(cardNum)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
