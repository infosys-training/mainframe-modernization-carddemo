package com.carddemo.authorization.controller;

import com.carddemo.authorization.dto.AuthorizationRequest;
import com.carddemo.authorization.dto.AuthorizationResponse;
import com.carddemo.authorization.entity.AuthorizationRecord;
import com.carddemo.authorization.service.AuthorizationService;
import com.carddemo.common.dto.ApiResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/authorizations")
public class AuthorizationController {

    private final AuthorizationService authorizationService;

    public AuthorizationController(AuthorizationService authorizationService) {
        this.authorizationService = authorizationService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<AuthorizationResponse>> authorize(
            @RequestBody AuthorizationRequest request) {
        return ResponseEntity.ok(ApiResponse.success(authorizationService.authorize(request)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AuthorizationRecord>> getAuthorization(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(authorizationService.getAuthorization(id)));
    }

    @GetMapping("/card/{cardNumber}")
    public ResponseEntity<ApiResponse<Page<AuthorizationRecord>>> getByCard(
            @PathVariable String cardNumber,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.success(
                authorizationService.getAuthorizationsByCard(cardNumber,
                        PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "requestTimestamp")))));
    }

    @GetMapping("/fraud-alerts")
    public ResponseEntity<ApiResponse<List<AuthorizationRecord>>> getFraudAlerts() {
        return ResponseEntity.ok(ApiResponse.success(authorizationService.getFraudAlerts()));
    }

    @PutMapping("/{id}/mark-fraud")
    public ResponseEntity<ApiResponse<AuthorizationRecord>> markAsFraud(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(authorizationService.markAsFraud(id)));
    }

    @PutMapping("/{id}/clear-fraud")
    public ResponseEntity<ApiResponse<AuthorizationRecord>> clearFraudFlag(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(authorizationService.clearFraudFlag(id)));
    }

    @PostMapping("/purge")
    public ResponseEntity<ApiResponse<Map<String, Integer>>> purgeExpired(
            @RequestParam(defaultValue = "90") int olderThanDays) {
        int purged = authorizationService.purgeExpiredAuthorizations(olderThanDays);
        return ResponseEntity.ok(ApiResponse.success(Map.of("purgedCount", purged)));
    }
}
