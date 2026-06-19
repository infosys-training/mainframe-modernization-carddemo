package com.carddemo.common.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "transaction_types")
public class TransactionType {

    @Id
    @Column(name = "tran_type", length = 2, nullable = false)
    private String typeCode;

    @Column(name = "tran_type_desc", length = 50)
    private String description;

    public TransactionType() {}

    public TransactionType(String typeCode, String description) {
        this.typeCode = typeCode;
        this.description = description;
    }

    public String getTypeCode() { return typeCode; }
    public void setTypeCode(String typeCode) { this.typeCode = typeCode; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
