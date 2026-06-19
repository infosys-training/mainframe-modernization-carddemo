package com.carddemo.userauth.dto;

public class LoginResponse {

    private String userId;
    private String firstName;
    private String lastName;
    private String userType;
    private boolean authenticated;

    public LoginResponse() {}

    public LoginResponse(String userId, String firstName, String lastName, String userType, boolean authenticated) {
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.userType = userType;
        this.authenticated = authenticated;
    }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public String getUserType() { return userType; }
    public void setUserType(String userType) { this.userType = userType; }
    public boolean isAuthenticated() { return authenticated; }
    public void setAuthenticated(boolean authenticated) { this.authenticated = authenticated; }
}
