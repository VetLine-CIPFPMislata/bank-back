package org.example.bankback.persistence.dao.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.example.bankback.domain.models.CreditCard;
import org.example.bankback.persistence.dao.CreditCardJpaDao;
import org.example.bankback.persistence.dao.entity.CreditCardJpaEntity;
import org.example.bankback.persistence.repository.mapper.MapperPersistence;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional
public class CreditCardJpaDaoImpl implements CreditCardJpaDao {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<CreditCard> findByCardNumber(String cardNumber) {
        TypedQuery<CreditCardJpaEntity> query = entityManager.createQuery(
                "SELECT c FROM CreditCardJpaEntity c WHERE c.numeroTarjeta = :cardNumber",
                CreditCardJpaEntity.class
        );
        query.setParameter("cardNumber", cardNumber);

        return query.getResultList().stream()
                .findFirst()
                .map(MapperPersistence.getInstance()::fromCreditCardJpaEntityToCreditCard);
    }

    @Override
    public Optional<CreditCard> findByName(String name) {
        TypedQuery<CreditCardJpaEntity> query = entityManager.createQuery(
                "SELECT c FROM CreditCardJpaEntity c WHERE c.nombreCompleto = :name",
                CreditCardJpaEntity.class
        );
        query.setParameter("name", name);

        return query.getResultList().stream()
                .findFirst()
                .map(MapperPersistence.getInstance()::fromCreditCardJpaEntityToCreditCard);
    }
}

