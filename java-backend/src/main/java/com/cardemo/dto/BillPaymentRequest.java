package com.cardemo.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Bill payment request DTO - replaces COBIL00C screen input.
 * Original COBOL: ACTIDINI OF COBIL0AI (account ID) and CONFIRMI (Y/N confirmation).
 * The bill payment pays the full current balance.
 */
public class BillPaymentRequest {

    @NotBlank(message = "Acct ID can NOT be empty")
    private String accountId;

    private boolean confirmed;

    public BillPaymentRequest() {
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public void setConfirmed(boolean confirmed) {
        this.confirmed = confirmed;
    }
}
