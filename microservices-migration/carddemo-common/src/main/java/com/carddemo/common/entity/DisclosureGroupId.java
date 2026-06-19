package com.carddemo.common.entity;

import java.io.Serializable;
import java.util.Objects;

public class DisclosureGroupId implements Serializable {

    private String accountGroupId;
    private String transactionTypeCode;
    private Integer transactionCategoryCode;

    public DisclosureGroupId() {}

    public DisclosureGroupId(String accountGroupId, String transactionTypeCode, Integer transactionCategoryCode) {
        this.accountGroupId = accountGroupId;
        this.transactionTypeCode = transactionTypeCode;
        this.transactionCategoryCode = transactionCategoryCode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DisclosureGroupId that = (DisclosureGroupId) o;
        return Objects.equals(accountGroupId, that.accountGroupId)
                && Objects.equals(transactionTypeCode, that.transactionTypeCode)
                && Objects.equals(transactionCategoryCode, that.transactionCategoryCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(accountGroupId, transactionTypeCode, transactionCategoryCode);
    }
}
