package com.carddemo.common.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
public class Transaction {

    @Id
    @Column(name = "tran_id", length = 16, nullable = false)
    private String transactionId;

    @Column(name = "tran_type_cd", length = 2)
    private String typeCode;

    @Column(name = "tran_cat_cd")
    private Integer categoryCode;

    @Column(name = "tran_source", length = 10)
    private String source;

    @Column(name = "tran_desc", length = 100)
    private String description;

    @Column(name = "tran_amt", precision = 11, scale = 2)
    private BigDecimal amount;

    @Column(name = "tran_merchant_id", length = 9)
    private String merchantId;

    @Column(name = "tran_merchant_name", length = 50)
    private String merchantName;

    @Column(name = "tran_merchant_city", length = 50)
    private String merchantCity;

    @Column(name = "tran_merchant_zip", length = 10)
    private String merchantZip;

    @Column(name = "tran_card_num", length = 16)
    private String cardNumber;

    @Column(name = "tran_orig_ts")
    private LocalDateTime originTimestamp;

    @Column(name = "tran_proc_ts")
    private LocalDateTime processedTimestamp;

    public Transaction() {}

    public Transaction(String transactionId) {
        this.transactionId = transactionId;
    }

    public boolean isPurchase() { return "01".equals(typeCode); }
    public boolean isPayment() { return "02".equals(typeCode); }
    public boolean isCredit() { return "03".equals(typeCode); }
    public boolean isRefund() { return "05".equals(typeCode); }

    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }
    public String getTypeCode() { return typeCode; }
    public void setTypeCode(String typeCode) { this.typeCode = typeCode; }
    public Integer getCategoryCode() { return categoryCode; }
    public void setCategoryCode(Integer categoryCode) { this.categoryCode = categoryCode; }
    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public String getMerchantId() { return merchantId; }
    public void setMerchantId(String merchantId) { this.merchantId = merchantId; }
    public String getMerchantName() { return merchantName; }
    public void setMerchantName(String merchantName) { this.merchantName = merchantName; }
    public String getMerchantCity() { return merchantCity; }
    public void setMerchantCity(String merchantCity) { this.merchantCity = merchantCity; }
    public String getMerchantZip() { return merchantZip; }
    public void setMerchantZip(String merchantZip) { this.merchantZip = merchantZip; }
    public String getCardNumber() { return cardNumber; }
    public void setCardNumber(String cardNumber) { this.cardNumber = cardNumber; }
    public LocalDateTime getOriginTimestamp() { return originTimestamp; }
    public void setOriginTimestamp(LocalDateTime originTimestamp) { this.originTimestamp = originTimestamp; }
    public LocalDateTime getProcessedTimestamp() { return processedTimestamp; }
    public void setProcessedTimestamp(LocalDateTime processedTimestamp) { this.processedTimestamp = processedTimestamp; }
}
