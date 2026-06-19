package com.carddemo.common.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "card_xref")
public class CardXref {

    @Id
    @Column(name = "xref_card_num", length = 16, nullable = false)
    private String cardNumber;

    @Column(name = "xref_cust_id", length = 9, nullable = false)
    private String customerId;

    @Column(name = "xref_acct_id", length = 11, nullable = false)
    private String accountId;

    public CardXref() {}

    public CardXref(String cardNumber, String customerId, String accountId) {
        this.cardNumber = cardNumber;
        this.customerId = customerId;
        this.accountId = accountId;
    }

    public String getCardNumber() { return cardNumber; }
    public void setCardNumber(String cardNumber) { this.cardNumber = cardNumber; }
    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }
    public String getAccountId() { return accountId; }
    public void setAccountId(String accountId) { this.accountId = accountId; }
}
