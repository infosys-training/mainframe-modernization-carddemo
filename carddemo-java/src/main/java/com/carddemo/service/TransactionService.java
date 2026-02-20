package com.carddemo.service;

import com.carddemo.entity.Transaction;
import com.carddemo.exception.ResourceNotFoundException;
import com.carddemo.repository.TransactionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;

    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public Transaction findById(String tranId) {
        if (tranId == null || tranId.isBlank()) {
            throw new IllegalArgumentException("Tran ID can NOT be empty...");
        }
        return transactionRepository.findById(tranId)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction ID NOT found..."));
    }

    public Page<Transaction> findAll(Pageable pageable) {
        return transactionRepository.findAllByOrderByTranIdAsc(pageable);
    }

    public Page<Transaction> findByCardNum(String cardNum, Pageable pageable) {
        return transactionRepository.findByCardNum(cardNum, pageable);
    }

    @Transactional
    public Transaction save(Transaction transaction) {
        return transactionRepository.save(transaction);
    }
}
