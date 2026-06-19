package com.carddemo.transactiontype.service;

import com.carddemo.common.entity.TransactionCategory;
import com.carddemo.common.entity.TransactionCategoryId;
import com.carddemo.common.entity.TransactionType;
import com.carddemo.common.exception.BusinessException;
import com.carddemo.common.exception.ResourceNotFoundException;
import com.carddemo.transactiontype.repository.TransactionCategoryRepository;
import com.carddemo.transactiontype.repository.TransactionTypeRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Transaction type and category management.
 * Migrated from COTRTLIC.cbl (list with cursor-based pagination)
 * and COTRTUPC.cbl (update with cascading deletes).
 */
@Service
@Transactional
public class TransactionTypeService {

    private final TransactionTypeRepository typeRepository;
    private final TransactionCategoryRepository categoryRepository;

    public TransactionTypeService(TransactionTypeRepository typeRepository,
                                  TransactionCategoryRepository categoryRepository) {
        this.typeRepository = typeRepository;
        this.categoryRepository = categoryRepository;
    }

    @Transactional(readOnly = true)
    public List<TransactionType> listTypes() {
        return typeRepository.findAll();
    }

    @Transactional(readOnly = true)
    public TransactionType getType(String typeCode) {
        return typeRepository.findById(typeCode)
                .orElseThrow(() -> new ResourceNotFoundException("TransactionType", typeCode));
    }

    public TransactionType createType(TransactionType type) {
        if (typeRepository.existsById(type.getTypeCode())) {
            throw new BusinessException("Transaction type already exists: " + type.getTypeCode());
        }
        return typeRepository.save(type);
    }

    public TransactionType updateType(String typeCode, TransactionType updates) {
        TransactionType type = getType(typeCode);
        if (updates.getDescription() != null) type.setDescription(updates.getDescription());
        return typeRepository.save(type);
    }

    public void deleteType(String typeCode) {
        TransactionType type = getType(typeCode);
        List<TransactionCategory> categories = categoryRepository.findByTypeCode(typeCode);
        categoryRepository.deleteAll(categories);
        typeRepository.delete(type);
    }

    @Transactional(readOnly = true)
    public Page<TransactionCategory> listCategories(Pageable pageable) {
        return categoryRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public List<TransactionCategory> getCategoriesByType(String typeCode) {
        return categoryRepository.findByTypeCode(typeCode);
    }

    @Transactional(readOnly = true)
    public TransactionCategory getCategory(String typeCode, Integer categoryCode) {
        return categoryRepository.findById(new TransactionCategoryId(typeCode, categoryCode))
                .orElseThrow(() -> new ResourceNotFoundException(
                        "TransactionCategory", typeCode + "-" + categoryCode));
    }

    public TransactionCategory createCategory(TransactionCategory category) {
        TransactionCategoryId id = new TransactionCategoryId(category.getTypeCode(), category.getCategoryCode());
        if (categoryRepository.existsById(id)) {
            throw new BusinessException("Category already exists: " + category.getTypeCode() + "-" + category.getCategoryCode());
        }
        return categoryRepository.save(category);
    }

    public TransactionCategory updateCategory(String typeCode, Integer categoryCode, TransactionCategory updates) {
        TransactionCategory category = getCategory(typeCode, categoryCode);
        if (updates.getDescription() != null) category.setDescription(updates.getDescription());
        return categoryRepository.save(category);
    }

    public void deleteCategory(String typeCode, Integer categoryCode) {
        TransactionCategory category = getCategory(typeCode, categoryCode);
        categoryRepository.delete(category);
    }
}
