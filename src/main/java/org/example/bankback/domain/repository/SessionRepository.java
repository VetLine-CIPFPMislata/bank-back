package org.example.bankback.domain.repository;

import org.example.bankback.domain.models.Session;

import java.util.Optional;

public interface SessionRepository {
    Optional<Session> findByToken(String token);
    Session save(Session session);
    void deleteByToken(String token);
}

