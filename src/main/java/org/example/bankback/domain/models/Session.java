package org.example.bankback.domain.models;

import java.time.LocalDateTime;

public class Session {
    private Long id;
    private Long clientId;
    private String token;
    private LocalDateTime loginDate;

    public Session() {}

    public Session(Long id, Long clientId, String token, LocalDateTime loginDate) {
        this.id = id;
        this.clientId = clientId;
        this.token = token;
        this.loginDate = loginDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public LocalDateTime getLoginDate() {
        return loginDate;
    }

    public void setLoginDate(LocalDateTime loginDate) {
        this.loginDate = loginDate;
    }
}

