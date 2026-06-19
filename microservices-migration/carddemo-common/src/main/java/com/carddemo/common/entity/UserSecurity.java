package com.carddemo.common.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "user_security")
public class UserSecurity {

    @Id
    @Column(name = "usr_id", length = 8, nullable = false)
    private String userId;

    @Column(name = "usr_fname", length = 20)
    private String firstName;

    @Column(name = "usr_lname", length = 20)
    private String lastName;

    @Column(name = "usr_pwd", length = 255)
    private String password;

    @Column(name = "usr_type", length = 1)
    private String userType;

    public UserSecurity() {}

    public UserSecurity(String userId) {
        this.userId = userId;
    }

    public boolean isAdmin() {
        return "A".equals(userType);
    }

    public boolean isRegularUser() {
        return "U".equals(userType);
    }

    public String getFullName() {
        StringBuilder sb = new StringBuilder();
        if (firstName != null) sb.append(firstName.trim());
        if (lastName != null) sb.append(" ").append(lastName.trim());
        return sb.toString().trim();
    }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getUserType() { return userType; }
    public void setUserType(String userType) { this.userType = userType; }
}
