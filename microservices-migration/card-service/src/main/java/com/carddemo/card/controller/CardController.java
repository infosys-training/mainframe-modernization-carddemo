package com.carddemo.card.controller;

import com.carddemo.card.service.CardService;
import com.carddemo.common.dto.ApiResponse;
import com.carddemo.common.entity.Card;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/cards")
public class CardController {

    private final CardService cardService;

    public CardController(CardService cardService) {
        this.cardService = cardService;
    }

    @GetMapping("/{cardNumber}")
    public ResponseEntity<ApiResponse<Card>> getCard(@PathVariable String cardNumber) {
        return ResponseEntity.ok(ApiResponse.success(cardService.getCard(cardNumber)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<Card>>> listCards(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.success(
                cardService.listCards(PageRequest.of(page, size, Sort.by("cardNumber")))));
    }

    @GetMapping("/account/{accountId}")
    public ResponseEntity<ApiResponse<List<Card>>> getCardsByAccount(@PathVariable String accountId) {
        return ResponseEntity.ok(ApiResponse.success(cardService.getCardsByAccount(accountId)));
    }

    @PutMapping("/{cardNumber}")
    public ResponseEntity<ApiResponse<Card>> updateCard(
            @PathVariable String cardNumber, @RequestBody Card updates) {
        return ResponseEntity.ok(ApiResponse.success(cardService.updateCard(cardNumber, updates)));
    }

    @PutMapping("/{cardNumber}/activate")
    public ResponseEntity<ApiResponse<Card>> activateCard(@PathVariable String cardNumber) {
        return ResponseEntity.ok(ApiResponse.success(cardService.activateCard(cardNumber)));
    }

    @PutMapping("/{cardNumber}/deactivate")
    public ResponseEntity<ApiResponse<Card>> deactivateCard(@PathVariable String cardNumber) {
        return ResponseEntity.ok(ApiResponse.success(cardService.deactivateCard(cardNumber)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Card>> createCard(@RequestBody Card card) {
        return ResponseEntity.ok(ApiResponse.success(cardService.createCard(card)));
    }
}
