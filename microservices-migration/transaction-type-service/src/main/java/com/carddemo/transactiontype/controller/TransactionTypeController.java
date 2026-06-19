package com.carddemo.transactiontype.controller;

import com.carddemo.common.dto.ApiResponse;
import com.carddemo.common.entity.TransactionCategory;
import com.carddemo.common.entity.TransactionType;
import com.carddemo.transactiontype.service.TransactionTypeService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class TransactionTypeController {

    private final TransactionTypeService transactionTypeService;

    public TransactionTypeController(TransactionTypeService transactionTypeService) {
        this.transactionTypeService = transactionTypeService;
    }

    @GetMapping("/transaction-types")
    public ResponseEntity<ApiResponse<List<TransactionType>>> listTypes() {
        return ResponseEntity.ok(ApiResponse.success(transactionTypeService.listTypes()));
    }

    @GetMapping("/transaction-types/{typeCode}")
    public ResponseEntity<ApiResponse<TransactionType>> getType(@PathVariable String typeCode) {
        return ResponseEntity.ok(ApiResponse.success(transactionTypeService.getType(typeCode)));
    }

    @PostMapping("/transaction-types")
    public ResponseEntity<ApiResponse<TransactionType>> createType(@RequestBody TransactionType type) {
        return ResponseEntity.ok(ApiResponse.success(transactionTypeService.createType(type)));
    }

    @PutMapping("/transaction-types/{typeCode}")
    public ResponseEntity<ApiResponse<TransactionType>> updateType(
            @PathVariable String typeCode, @RequestBody TransactionType updates) {
        return ResponseEntity.ok(ApiResponse.success(transactionTypeService.updateType(typeCode, updates)));
    }

    @DeleteMapping("/transaction-types/{typeCode}")
    public ResponseEntity<ApiResponse<Void>> deleteType(@PathVariable String typeCode) {
        transactionTypeService.deleteType(typeCode);
        return ResponseEntity.ok(ApiResponse.success("Transaction type deleted", null));
    }

    @GetMapping("/transaction-categories")
    public ResponseEntity<ApiResponse<Page<TransactionCategory>>> listCategories(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.success(
                transactionTypeService.listCategories(PageRequest.of(page, size))));
    }

    @GetMapping("/transaction-types/{typeCode}/categories")
    public ResponseEntity<ApiResponse<List<TransactionCategory>>> getCategoriesByType(
            @PathVariable String typeCode) {
        return ResponseEntity.ok(ApiResponse.success(
                transactionTypeService.getCategoriesByType(typeCode)));
    }

    @PostMapping("/transaction-categories")
    public ResponseEntity<ApiResponse<TransactionCategory>> createCategory(
            @RequestBody TransactionCategory category) {
        return ResponseEntity.ok(ApiResponse.success(transactionTypeService.createCategory(category)));
    }

    @PutMapping("/transaction-types/{typeCode}/categories/{categoryCode}")
    public ResponseEntity<ApiResponse<TransactionCategory>> updateCategory(
            @PathVariable String typeCode, @PathVariable Integer categoryCode,
            @RequestBody TransactionCategory updates) {
        return ResponseEntity.ok(ApiResponse.success(
                transactionTypeService.updateCategory(typeCode, categoryCode, updates)));
    }

    @DeleteMapping("/transaction-types/{typeCode}/categories/{categoryCode}")
    public ResponseEntity<ApiResponse<Void>> deleteCategory(
            @PathVariable String typeCode, @PathVariable Integer categoryCode) {
        transactionTypeService.deleteCategory(typeCode, categoryCode);
        return ResponseEntity.ok(ApiResponse.success("Category deleted", null));
    }
}
