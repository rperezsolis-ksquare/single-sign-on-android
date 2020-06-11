package com.rafaelperez.ssolauncher.domain;

public class ValidateAuthCodeRequest {
    private String clientId;
    private String clientSecret;
    private String authCode;
    private Long id;

    public ValidateAuthCodeRequest(String clientId, String clientSecret, String authCode, Long id) {
        this.authCode = authCode;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.id = id;
    }

    public String getAuthCode() {
        return authCode;
    }

    public void setAuthCode(String authCode) {
        this.authCode = authCode;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
