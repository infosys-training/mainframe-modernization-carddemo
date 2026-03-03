package com.cardemo.dto;

import java.util.List;

/**
 * Login response DTO - replaces COSGN00C COMMAREA output after successful signon.
 * After login, the original COBOL set CDEMO-USER-ID, CDEMO-USER-TYPE in CARDDEMO-COMMAREA
 * and transferred control to either COADM01C (admin) or COMEN01C (regular user).
 */
public class LoginResponse {

    private String userId;
    private String userType;
    private String firstName;
    private String lastName;
    private String message;
    private List<MenuOption> menuOptions;

    public LoginResponse() {
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<MenuOption> getMenuOptions() {
        return menuOptions;
    }

    public void setMenuOptions(List<MenuOption> menuOptions) {
        this.menuOptions = menuOptions;
    }

    /**
     * Represents a menu option from the original COBOL menu screen.
     * Migrated from COMEN02Y.cpy (CARDDEMO-MAIN-MENU-OPTIONS) and
     * COADM02Y.cpy (CARDDEMO-ADMIN-MENU-OPTIONS).
     */
    public static class MenuOption {
        private int number;
        private String name;
        private String endpoint;

        public MenuOption() {
        }

        public MenuOption(int number, String name, String endpoint) {
            this.number = number;
            this.name = name;
            this.endpoint = endpoint;
        }

        public int getNumber() {
            return number;
        }

        public void setNumber(int number) {
            this.number = number;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getEndpoint() {
            return endpoint;
        }

        public void setEndpoint(String endpoint) {
            this.endpoint = endpoint;
        }
    }
}
