package org.example.bankback.persistence.dao.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.example.bankback.domain.models.BankMovement;
import org.example.bankback.domain.models.CreditCard;
import org.example.bankback.persistence.dao.BankMovementJpaDao;
import org.example.bankback.persistence.dao.entity.BankMovementJpaEntity;
import org.example.bankback.persistence.repository.mapper.MapperPersistence;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Optional;

@Repository
@Transactional
public class BankMovementJpaDaoImpl implements BankMovementJpaDao {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<BankMovement> findById(Long id) {
        BankMovementJpaEntity entity = entityManager.find(BankMovementJpaEntity.class, id);
        return Optional.ofNullable(entity)
                .map(MapperPersistence.getInstance()::fromBankMovementJpaEntityToBankMovement);
    }

    @Override
    public Optional<BankMovement> findByCreditCard(CreditCard tarjetaCredito) {
        TypedQuery<BankMovementJpaEntity> query = entityManager.createQuery(
                "SELECT b FROM BankMovementJpaEntity b WHERE b.tarjetaCreditoOrigen.numeroTarjeta = :cardNumber",
                BankMovementJpaEntity.class
        );
        query.setParameter("cardNumber", String.valueOf(tarjetaCredito.getNumeroTarjeta()));

        return query.getResultList().stream()
                .findFirst()
                .map(MapperPersistence.getInstance()::fromBankMovementJpaEntityToBankMovement);
    }

    @Override
    public Optional<BankMovement> findByDate(Date date) {
        TypedQuery<BankMovementJpaEntity> query = entityManager.createQuery(
                "SELECT b FROM BankMovementJpaEntity b WHERE b.fechaMovimiento = :date",
                BankMovementJpaEntity.class
        );
        query.setParameter("date", date);

        return query.getResultList().stream()
                .findFirst()
                .map(MapperPersistence.getInstance()::fromBankMovementJpaEntityToBankMovement);
    }

    @Override
    public BankMovement save(BankMovement bankMovement) {
        BankMovementJpaEntity entity = MapperPersistence.getInstance()
                .fromBankMovementToBankMovementJpaEntity(bankMovement);

        if (entity.getId() == null) {
            entityManager.persist(entity);
        } else {
            entity = entityManager.merge(entity);
        }

        return MapperPersistence.getInstance().fromBankMovementJpaEntityToBankMovement(entity);
    }
}
