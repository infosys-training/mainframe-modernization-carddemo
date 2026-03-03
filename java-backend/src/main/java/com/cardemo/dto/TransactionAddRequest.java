package com.cardemo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * Transaction add request DTO - replaces COTRN02C screen input fields.
 * Original COBOL validated: account/card number, type, category, source,
 * description, amount (format -99999999.99), dates (format YYYY-MM-DD),
 * merchant info, and confirmation flag.
 */
public class TransactionAddRequest {

    private String accountId;

    private String cardNumber;

    @NotBlank(message = "Type CD can NOT be empty")
    private String typeCode;

    @NotNull(message = "Category CD can NOT be empty")
    private Integer categoryCode;

    @NotBlank(message = "Source can NOT be empty")
    private String source;

    @NotBlank(message = "Description can NOT be empty")
    private String description;

    @NotNull(message = "Amount can NOT be empty")
    private BigDecimal amount;

    @NotBlank(message = "Orig Date can NOT be empty")
    private String origDate;

    @NotBlank(message = "Proc Date can NOT be empty")
    private String procDate;

    @NotNull(message = "Merchant ID can NOT be empty")
    private Long merchantId;

    @NotBlank(message = "Merchant Name can NOT be empty")
    private String merchantName;

    @NotBlank(message = "Merchant City can NOT be empty")
    private String merchantCity;

    @NotBlank(message = "Merchant Zip can NOT be empty")
    private String merchantZip;

    private boolean confirmed;

    public TransactionAddRequest() {
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getTypeCode() {
        return typeCode;
    }

    public void setTypeCode(String typeCode) {
        this.typeCode = typeCode;
    }

    public Integer getCategoryCode() {
        return categoryCode;
    }

    public void setCategoryCode(Integer categoryCode) {
        this.categoryCode = categoryCode;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getOrigDate() {
        return origDate;
    }

    public void setOrigDate(String origDate) {
        this.origDate = origDate;
    }

    public String getProcDate() {
        return procDate;
    }

    public void setProcDate(String procDate) {
        this.procDate = procDate;
    }

    public Long getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(Long merchantId) {
        this.merchantId = merchantId;
    }

    public String getMerchantName() {
        return merchantName;
    }

    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName;
    }

    public String getMerchantCity() {
        return merchantCity;
    }

    public void setMerchantCity(String merchantCity) {
        this.merchantCity = merchantCity;
    }

    public String getMerchantZip() {
        return merchantZip;
    }

    public void setMerchantZip(String merchantZip) {
        this.merchantZip = merchantZip;
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public void setConfirmed(boolean confirmed) {
        this.confirmed = confirmed;
    }
}
