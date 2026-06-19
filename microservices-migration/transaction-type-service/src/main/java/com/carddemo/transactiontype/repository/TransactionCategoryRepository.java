package com.carddemo.transactiontype.repository;

import com.carddemo.common.entity.TransactionCategory;
import com.carddemo.common.entity.TransactionCategoryId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionCategoryRepository extends JpaRepository<TransactionCategory, TransactionCategoryId> {

    List<TransactionCategory> findByTypeCode(String typeCode);
}
