package com.carddemo.authorization.dto;

import java.math.BigDecimal;

public class AuthorizationRequest {

    private String cardNumber;
    private BigDecimal amount;
    private String merchantId;
    private String merchantName;
    private String transactionTypeCode;

    public AuthorizationRequest() {}

    public String getCardNumber() { return cardNumber; }
    public void setCardNumber(String cardNumber) { this.cardNumber = cardNumber; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public String getMerchantId() { return merchantId; }
    public void setMerchantId(String merchantId) { this.merchantId = merchantId; }
    public String getMerchantName() { return merchantName; }
    public void setMerchantName(String merchantName) { this.merchantName = merchantName; }
    public String getTransactionTypeCode() { return transactionTypeCode; }
    public void setTransactionTypeCode(String transactionTypeCode) { this.transactionTypeCode = transactionTypeCode; }
}
