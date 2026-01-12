package org.example.bankback.persistence.dao.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.example.bankback.domain.models.Session;
import org.example.bankback.persistence.dao.SessionJpaDao;
import org.example.bankback.persistence.dao.entity.SessionJpaEntity;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@Transactional
public class SessionJpaDaoImpl implements SessionJpaDao {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<Session> findByToken(String token) {
        TypedQuery<SessionJpaEntity> query = entityManager.createQuery(
                "SELECT s FROM SessionJpaEntity s WHERE s.token = :token",
                SessionJpaEntity.class
        );
        query.setParameter("token", token);

        try {
            SessionJpaEntity jpaEntity = query.getSingleResult();
            return Optional.of(mapToDomain(jpaEntity));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public Session save(Session session) {
        SessionJpaEntity jpaEntity = mapToJpa(session);

        if (jpaEntity.getId() == null) {
            entityManager.persist(jpaEntity);
        } else {
            jpaEntity = entityManager.merge(jpaEntity);
        }

        return mapToDomain(jpaEntity);
    }

    @Override
    public void deleteByToken(String token) {
        TypedQuery<SessionJpaEntity> query = entityManager.createQuery(
                "SELECT s FROM SessionJpaEntity s WHERE s.token = :token",
                SessionJpaEntity.class
        );
        query.setParameter("token", token);

        try {
            SessionJpaEntity jpaEntity = query.getSingleResult();
            entityManager.remove(jpaEntity);
        } catch (Exception e) {
            // Token no encontrado, no hacer nada
        }
    }

    private Session mapToDomain(SessionJpaEntity jpaEntity) {
        return new Session(
                jpaEntity.getId(),
                jpaEntity.getClientId(),
                jpaEntity.getToken(),
                jpaEntity.getLoginDate()
        );
    }

    private SessionJpaEntity mapToJpa(Session session) {
        return new SessionJpaEntity(
                session.getId(),
                session.getClientId(),
                session.getToken(),
                session.getLoginDate()
        );
    }
}

