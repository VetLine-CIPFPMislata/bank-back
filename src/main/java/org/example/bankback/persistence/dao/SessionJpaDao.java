package org.example.bankback.persistence.dao;

import org.example.bankback.domain.models.Session;

import java.util.Optional;

public interface SessionJpaDao {
    Optional<Session> findByToken(String token);
    Session save(Session session);
    void deleteByToken(String token);
}

