package com.carddemo.common.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "transaction_category_balances")
@IdClass(TransactionCategoryBalanceId.class)
public class TransactionCategoryBalance {

    @Id
    @Column(name = "trancat_acct_id", length = 11, nullable = false)
    private String accountId;

    @Id
    @Column(name = "trancat_type_cd", length = 2, nullable = false)
    private String typeCode;

    @Id
    @Column(name = "trancat_cd", nullable = false)
    private Integer categoryCode;

    @Column(name = "tran_cat_bal", precision = 11, scale = 2)
    private BigDecimal balance = BigDecimal.ZERO;

    public TransactionCategoryBalance() {}

    public String getAccountId() { return accountId; }
    public void setAccountId(String accountId) { this.accountId = accountId; }
    public String getTypeCode() { return typeCode; }
    public void setTypeCode(String typeCode) { this.typeCode = typeCode; }
    public Integer getCategoryCode() { return categoryCode; }
    public void setCategoryCode(Integer categoryCode) { this.categoryCode = categoryCode; }
    public BigDecimal getBalance() { return balance; }
    public void setBalance(BigDecimal balance) { this.balance = balance; }
}
