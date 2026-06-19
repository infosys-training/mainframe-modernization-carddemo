package com.carddemo.common.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "transaction_categories")
@IdClass(TransactionCategoryId.class)
public class TransactionCategory {

    @Id
    @Column(name = "tran_type_cd", length = 2, nullable = false)
    private String typeCode;

    @Id
    @Column(name = "tran_cat_cd", nullable = false)
    private Integer categoryCode;

    @Column(name = "tran_cat_type_desc", length = 50)
    private String description;

    public TransactionCategory() {}

    public TransactionCategory(String typeCode, Integer categoryCode, String description) {
        this.typeCode = typeCode;
        this.categoryCode = categoryCode;
        this.description = description;
    }

    public String getTypeCode() { return typeCode; }
    public void setTypeCode(String typeCode) { this.typeCode = typeCode; }
    public Integer getCategoryCode() { return categoryCode; }
    public void setCategoryCode(Integer categoryCode) { this.categoryCode = categoryCode; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
