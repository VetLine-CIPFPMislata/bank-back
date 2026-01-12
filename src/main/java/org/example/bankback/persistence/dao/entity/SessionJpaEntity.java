package org.example.bankback.persistence.dao.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "sessions")
public class SessionJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_session")
    private Long id;

    @Column(name = "id_client", nullable = false)
    private Long clientId;

    @Column(nullable = false, unique = true, length = 500)
    private String token;

    @Column(name = "login_date", nullable = false)
    private LocalDateTime loginDate;

    public SessionJpaEntity() {}

    public SessionJpaEntity(Long id, Long clientId, String token, LocalDateTime loginDate) {
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

