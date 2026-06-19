package com.carddemo.reporting.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class DailyTransactionReport {

    private LocalDate reportDate;
    private int totalTransactions;
    private BigDecimal totalAmount;
    private Map<String, Integer> transactionsByType;
    private Map<String, BigDecimal> amountsByType;
    private List<StatementLineItem> transactions;

    public LocalDate getReportDate() { return reportDate; }
    public void setReportDate(LocalDate reportDate) { this.reportDate = reportDate; }
    public int getTotalTransactions() { return totalTransactions; }
    public void setTotalTransactions(int totalTransactions) { this.totalTransactions = totalTransactions; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
    public Map<String, Integer> getTransactionsByType() { return transactionsByType; }
    public void setTransactionsByType(Map<String, Integer> transactionsByType) { this.transactionsByType = transactionsByType; }
    public Map<String, BigDecimal> getAmountsByType() { return amountsByType; }
    public void setAmountsByType(Map<String, BigDecimal> amountsByType) { this.amountsByType = amountsByType; }
    public List<StatementLineItem> getTransactions() { return transactions; }
    public void setTransactions(List<StatementLineItem> transactions) { this.transactions = transactions; }
}
