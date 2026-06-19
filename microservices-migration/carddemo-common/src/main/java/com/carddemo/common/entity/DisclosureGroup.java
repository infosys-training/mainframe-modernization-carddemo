package com.carddemo.common.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "disclosure_groups")
@IdClass(DisclosureGroupId.class)
public class DisclosureGroup {

    @Id
    @Column(name = "dis_acct_group_id", length = 10, nullable = false)
    private String accountGroupId;

    @Id
    @Column(name = "dis_tran_type_cd", length = 2, nullable = false)
    private String transactionTypeCode;

    @Id
    @Column(name = "dis_tran_cat_cd", nullable = false)
    private Integer transactionCategoryCode;

    @Column(name = "dis_int_rate", precision = 6, scale = 2)
    private BigDecimal interestRate;

    public DisclosureGroup() {}

    public String getAccountGroupId() { return accountGroupId; }
    public void setAccountGroupId(String accountGroupId) { this.accountGroupId = accountGroupId; }
    public String getTransactionTypeCode() { return transactionTypeCode; }
    public void setTransactionTypeCode(String transactionTypeCode) { this.transactionTypeCode = transactionTypeCode; }
    public Integer getTransactionCategoryCode() { return transactionCategoryCode; }
    public void setTransactionCategoryCode(Integer transactionCategoryCode) { this.transactionCategoryCode = transactionCategoryCode; }
    public BigDecimal getInterestRate() { return interestRate; }
    public void setInterestRate(BigDecimal interestRate) { this.interestRate = interestRate; }
}
