package com.carddemo.common.entity;

import java.io.Serializable;
import java.util.Objects;

public class TransactionCategoryBalanceId implements Serializable {

    private String accountId;
    private String typeCode;
    private Integer categoryCode;

    public TransactionCategoryBalanceId() {}

    public TransactionCategoryBalanceId(String accountId, String typeCode, Integer categoryCode) {
        this.accountId = accountId;
        this.typeCode = typeCode;
        this.categoryCode = categoryCode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TransactionCategoryBalanceId that = (TransactionCategoryBalanceId) o;
        return Objects.equals(accountId, that.accountId)
                && Objects.equals(typeCode, that.typeCode)
                && Objects.equals(categoryCode, that.categoryCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(accountId, typeCode, categoryCode);
    }
}
