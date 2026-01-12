package org.example.bankback.persistence.dao.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.example.bankback.domain.models.BankAccount;
import org.example.bankback.persistence.dao.BankAccountJpaDao;
import org.example.bankback.persistence.dao.entity.BankAccountJpaEntity;
import org.example.bankback.persistence.repository.mapper.MapperPersistence;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Transactional
public class BankAccountJpaDaoImpl implements BankAccountJpaDao {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<BankAccount> findById(Long id) {
        BankAccountJpaEntity entity = entityManager.find(BankAccountJpaEntity.class, id);
        return Optional.ofNullable(entity)
                .map(MapperPersistence.getInstance()::fromBankAccountJpaEntityToBankAccount);
    }

    @Override
    public Optional<BankAccount> findByIBAN(String iban) {
        TypedQuery<BankAccountJpaEntity> query = entityManager.createQuery(
                "SELECT b FROM BankAccountJpaEntity b WHERE b.iban = :iban",
                BankAccountJpaEntity.class
        );
        query.setParameter("iban", iban);

        return query.getResultList().stream()
                .findFirst()
                .map(MapperPersistence.getInstance()::fromBankAccountJpaEntityToBankAccount);
    }

    @Override
    public BankAccount save(BankAccount bankAccount) {
        BankAccountJpaEntity entity = MapperPersistence.getInstance()
                .fromBankAccountToBankAccountJpaEntity(bankAccount);

        if (entity.getId() == null) {
            entityManager.persist(entity);
        } else {
            entity = entityManager.merge(entity);
        }

        return MapperPersistence.getInstance().fromBankAccountJpaEntityToBankAccount(entity);
    }

    @Override
    public List<BankAccount> findByClientId(Long clientId) {
        TypedQuery<BankAccountJpaEntity> query = entityManager.createQuery(
                "SELECT b FROM BankAccountJpaEntity b WHERE b.idCliente = :clientId",
                BankAccountJpaEntity.class
        );
        query.setParameter("clientId", clientId);

        return query.getResultList().stream()
                .map(MapperPersistence.getInstance()::fromBankAccountJpaEntityToBankAccount)
                .collect(Collectors.toList());
    }
}
