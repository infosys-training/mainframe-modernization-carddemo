package com.carddemo.authorization.dto;

public class AuthorizationResponse {

    private Long authorizationId;
    private String status;
    private String authorizationCode;
    private String declineReason;
    private boolean fraudDetected;

    public AuthorizationResponse() {}

    public AuthorizationResponse(Long authorizationId, String status, String authorizationCode,
                                 String declineReason, boolean fraudDetected) {
        this.authorizationId = authorizationId;
        this.status = status;
        this.authorizationCode = authorizationCode;
        this.declineReason = declineReason;
        this.fraudDetected = fraudDetected;
    }

    public static AuthorizationResponse approved(Long id, String code) {
        return new AuthorizationResponse(id, "APPROVED", code, null, false);
    }

    public static AuthorizationResponse declined(Long id, String reason) {
        return new AuthorizationResponse(id, "DECLINED", null, reason, false);
    }

    public static AuthorizationResponse fraudAlert(Long id, String reason) {
        return new AuthorizationResponse(id, "DECLINED", null, reason, true);
    }

    public Long getAuthorizationId() { return authorizationId; }
    public void setAuthorizationId(Long authorizationId) { this.authorizationId = authorizationId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getAuthorizationCode() { return authorizationCode; }
    public void setAuthorizationCode(String authorizationCode) { this.authorizationCode = authorizationCode; }
    public String getDeclineReason() { return declineReason; }
    public void setDeclineReason(String declineReason) { this.declineReason = declineReason; }
    public boolean isFraudDetected() { return fraudDetected; }
    public void setFraudDetected(boolean fraudDetected) { this.fraudDetected = fraudDetected; }
}
