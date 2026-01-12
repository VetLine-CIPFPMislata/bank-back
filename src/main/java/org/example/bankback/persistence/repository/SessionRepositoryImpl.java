package org.example.bankback.persistence.repository;

import org.example.bankback.domain.models.Session;
import org.example.bankback.domain.repository.SessionRepository;
import org.example.bankback.persistence.dao.SessionJpaDao;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class SessionRepositoryImpl implements SessionRepository {

    private final SessionJpaDao sessionJpaDao;

    public SessionRepositoryImpl(SessionJpaDao sessionJpaDao) {
        this.sessionJpaDao = sessionJpaDao;
    }

    @Override
    public Optional<Session> findByToken(String token) {
        return sessionJpaDao.findByToken(token);
    }

    @Override
    public Session save(Session session) {
        return sessionJpaDao.save(session);
    }

    @Override
    public void deleteByToken(String token) {
        sessionJpaDao.deleteByToken(token);
    }
}

