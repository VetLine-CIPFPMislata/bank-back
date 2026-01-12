package org.example.bankback.persistence.dao.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.example.bankback.domain.models.Client;
import org.example.bankback.persistence.dao.ClientJpaDao;
import org.example.bankback.persistence.dao.entity.ClientJpaEntity;
import org.example.bankback.persistence.repository.mapper.MapperPersistence;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional
public class ClientJpaDaoImpl implements ClientJpaDao {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<Client> findByUsername(String username) {
        TypedQuery<ClientJpaEntity> query = entityManager.createQuery(
                "SELECT c FROM ClientJpaEntity c WHERE c.username = :username",
                ClientJpaEntity.class
        );
        query.setParameter("username", username);

        return query.getResultList().stream()
                .findFirst()
                .map(MapperPersistence.getInstance()::fromClientJpaEntityToClient);
    }

    @Override
    public Optional<Client> findById(Long id) {
        ClientJpaEntity entity = entityManager.find(ClientJpaEntity.class, id);
        return Optional.ofNullable(entity)
                .map(MapperPersistence.getInstance()::fromClientJpaEntityToClient);
    }
}
