package com.carddemo.common.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "customers")
public class Customer {

    @Id
    @Column(name = "cust_id", length = 9, nullable = false)
    private String customerId;

    @Column(name = "cust_first_name", length = 25)
    private String firstName;

    @Column(name = "cust_middle_name", length = 25)
    private String middleName;

    @Column(name = "cust_last_name", length = 25)
    private String lastName;

    @Column(name = "cust_addr_line_1", length = 50)
    private String addressLine1;

    @Column(name = "cust_addr_line_2", length = 50)
    private String addressLine2;

    @Column(name = "cust_addr_line_3", length = 50)
    private String addressLine3;

    @Column(name = "cust_addr_state_cd", length = 2)
    private String stateCode;

    @Column(name = "cust_addr_country_cd", length = 3)
    private String countryCode;

    @Column(name = "cust_addr_zip", length = 10)
    private String zip;

    @Column(name = "cust_phone_num_1", length = 15)
    private String phoneNumber1;

    @Column(name = "cust_phone_num_2", length = 15)
    private String phoneNumber2;

    @Column(name = "cust_ssn", length = 9)
    private String ssn;

    @Column(name = "cust_govt_issued_id", length = 20)
    private String govtIssuedId;

    @Column(name = "cust_dob", length = 10)
    private String dateOfBirth;

    @Column(name = "cust_eft_account_id", length = 10)
    private String eftAccountId;

    @Column(name = "cust_pri_card_holder_ind", length = 1)
    private String primaryCardHolderIndicator;

    @Column(name = "cust_fico_credit_score")
    private Integer ficoCreditScore;

    public Customer() {}

    public Customer(String customerId) {
        this.customerId = customerId;
    }

    public String getFullName() {
        StringBuilder sb = new StringBuilder();
        if (firstName != null) sb.append(firstName.trim());
        if (middleName != null && !middleName.isBlank()) sb.append(" ").append(middleName.trim());
        if (lastName != null) sb.append(" ").append(lastName.trim());
        return sb.toString().trim();
    }

    public boolean isPrimaryCardHolder() {
        return "Y".equals(primaryCardHolderIndicator);
    }

    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getMiddleName() { return middleName; }
    public void setMiddleName(String middleName) { this.middleName = middleName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public String getAddressLine1() { return addressLine1; }
    public void setAddressLine1(String addressLine1) { this.addressLine1 = addressLine1; }
    public String getAddressLine2() { return addressLine2; }
    public void setAddressLine2(String addressLine2) { this.addressLine2 = addressLine2; }
    public String getAddressLine3() { return addressLine3; }
    public void setAddressLine3(String addressLine3) { this.addressLine3 = addressLine3; }
    public String getStateCode() { return stateCode; }
    public void setStateCode(String stateCode) { this.stateCode = stateCode; }
    public String getCountryCode() { return countryCode; }
    public void setCountryCode(String countryCode) { this.countryCode = countryCode; }
    public String getZip() { return zip; }
    public void setZip(String zip) { this.zip = zip; }
    public String getPhoneNumber1() { return phoneNumber1; }
    public void setPhoneNumber1(String phoneNumber1) { this.phoneNumber1 = phoneNumber1; }
    public String getPhoneNumber2() { return phoneNumber2; }
    public void setPhoneNumber2(String phoneNumber2) { this.phoneNumber2 = phoneNumber2; }
    public String getSsn() { return ssn; }
    public void setSsn(String ssn) { this.ssn = ssn; }
    public String getGovtIssuedId() { return govtIssuedId; }
    public void setGovtIssuedId(String govtIssuedId) { this.govtIssuedId = govtIssuedId; }
    public String getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(String dateOfBirth) { this.dateOfBirth = dateOfBirth; }
    public String getEftAccountId() { return eftAccountId; }
    public void setEftAccountId(String eftAccountId) { this.eftAccountId = eftAccountId; }
    public String getPrimaryCardHolderIndicator() { return primaryCardHolderIndicator; }
    public void setPrimaryCardHolderIndicator(String primaryCardHolderIndicator) { this.primaryCardHolderIndicator = primaryCardHolderIndicator; }
    public Integer getFicoCreditScore() { return ficoCreditScore; }
    public void setFicoCreditScore(Integer ficoCreditScore) { this.ficoCreditScore = ficoCreditScore; }
}
