package com.cardemo.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * User entity - migrated from COBOL copybook CSUSR01Y.cpy (SEC-USER-DATA, RECLN 80).
 * Used for authentication and authorization in the CardDemo system.
 *
 * Original COBOL fields:
 *   SEC-USR-ID      PIC X(08)
 *   SEC-USR-FNAME   PIC X(20)
 *   SEC-USR-LNAME   PIC X(20)
 *   SEC-USR-PWD     PIC X(08)
 *   SEC-USR-TYPE    PIC X(01)   ('A' = Admin, 'U' = User)
 */
@Entity
@Table(name = "card_demo_users")
public class CardDemoUser {

    @Id
    @Column(name = "user_id", length = 8)
    private String userId;

    @Column(name = "first_name", length = 20)
    private String firstName;

    @Column(name = "last_name", length = 20)
    private String lastName;

    @Column(name = "password", length = 8)
    private String password;

    @Column(name = "user_type", length = 1)
    private String userType;

    public CardDemoUser() {
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public boolean isAdmin() {
        return "A".equals(userType);
    }
}
