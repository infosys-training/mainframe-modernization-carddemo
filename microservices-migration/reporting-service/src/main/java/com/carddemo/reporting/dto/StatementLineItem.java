package com.carddemo.reporting.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class StatementLineItem {

    private String transactionId;
    private LocalDateTime date;
    private String description;
    private String merchantName;
    private String typeCode;
    private String typeDescription;
    private BigDecimal amount;

    public StatementLineItem() {}

    public StatementLineItem(String transactionId, LocalDateTime date, String description,
                             String merchantName, String typeCode, BigDecimal amount) {
        this.transactionId = transactionId;
        this.date = date;
        this.description = description;
        this.merchantName = merchantName;
        this.typeCode = typeCode;
        this.amount = amount;
    }

    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }
    public LocalDateTime getDate() { return date; }
    public void setDate(LocalDateTime date) { this.date = date; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getMerchantName() { return merchantName; }
    public void setMerchantName(String merchantName) { this.merchantName = merchantName; }
    public String getTypeCode() { return typeCode; }
    public void setTypeCode(String typeCode) { this.typeCode = typeCode; }
    public String getTypeDescription() { return typeDescription; }
    public void setTypeDescription(String typeDescription) { this.typeDescription = typeDescription; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
}
