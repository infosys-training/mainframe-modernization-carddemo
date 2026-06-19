package com.carddemo.common.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "cards")
public class Card {

    @Id
    @Column(name = "card_num", length = 16, nullable = false)
    private String cardNumber;

    @Column(name = "card_acct_id", length = 11, nullable = false)
    private String accountId;

    @Column(name = "card_cvv_cd", length = 3)
    private String cvvCode;

    @Column(name = "card_embossed_name", length = 50)
    private String embossedName;

    @Column(name = "card_expiration_date", length = 10)
    private String expirationDate;

    @Column(name = "card_active_status", length = 1)
    private String activeStatus = "Y";

    public Card() {}

    public Card(String cardNumber, String accountId) {
        this.cardNumber = cardNumber;
        this.accountId = accountId;
    }

    public boolean isActive() {
        return "Y".equals(activeStatus);
    }

    public String getMaskedCardNumber() {
        if (cardNumber == null || cardNumber.length() < 4) return "****";
        return "****-****-****-" + cardNumber.substring(cardNumber.length() - 4);
    }

    public String getCardNumber() { return cardNumber; }
    public void setCardNumber(String cardNumber) { this.cardNumber = cardNumber; }
    public String getAccountId() { return accountId; }
    public void setAccountId(String accountId) { this.accountId = accountId; }
    public String getCvvCode() { return cvvCode; }
    public void setCvvCode(String cvvCode) { this.cvvCode = cvvCode; }
    public String getEmbossedName() { return embossedName; }
    public void setEmbossedName(String embossedName) { this.embossedName = embossedName; }
    public String getExpirationDate() { return expirationDate; }
    public void setExpirationDate(String expirationDate) { this.expirationDate = expirationDate; }
    public String getActiveStatus() { return activeStatus; }
    public void setActiveStatus(String activeStatus) { this.activeStatus = activeStatus; }
}
