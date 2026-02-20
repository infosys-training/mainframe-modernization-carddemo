package com.carddemo.entity;

import java.io.Serializable;
import java.util.Objects;

public class TranCatBalanceId implements Serializable {

    private Long acctId;
    private String tranTypeCd;
    private Integer tranCatCd;

    public TranCatBalanceId() {}

    public TranCatBalanceId(Long acctId, String tranTypeCd, Integer tranCatCd) {
        this.acctId = acctId;
        this.tranTypeCd = tranTypeCd;
        this.tranCatCd = tranCatCd;
    }

    public Long getAcctId() { return acctId; }
    public void setAcctId(Long acctId) { this.acctId = acctId; }
    public String getTranTypeCd() { return tranTypeCd; }
    public void setTranTypeCd(String tranTypeCd) { this.tranTypeCd = tranTypeCd; }
    public Integer getTranCatCd() { return tranCatCd; }
    public void setTranCatCd(Integer tranCatCd) { this.tranCatCd = tranCatCd; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TranCatBalanceId that = (TranCatBalanceId) o;
        return Objects.equals(acctId, that.acctId)
                && Objects.equals(tranTypeCd, that.tranTypeCd)
                && Objects.equals(tranCatCd, that.tranCatCd);
    }

    @Override
    public int hashCode() {
        return Objects.hash(acctId, tranTypeCd, tranCatCd);
    }
}
