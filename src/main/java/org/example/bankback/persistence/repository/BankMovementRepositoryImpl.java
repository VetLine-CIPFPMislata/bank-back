package org.example.bankback.persistence.repository;

import org.example.bankback.domain.models.BankMovement;
import org.example.bankback.domain.models.CreditCard;
import org.example.bankback.domain.repository.BankMovementRepository;
import org.example.bankback.persistence.dao.BankMovementJpaDao;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Optional;

public class BankMovementRepositoryImpl implements BankMovementRepository {

    private final BankMovementJpaDao bankMovementJpaDao;

    public BankMovementRepositoryImpl(BankMovementJpaDao bankMovementJpaDao) {
        this.bankMovementJpaDao = bankMovementJpaDao;
    }

    @Override
    public Optional<BankMovement> findById(Long id) {
        return bankMovementJpaDao.findById(id);
    }

    @Override
    public Optional<BankMovement> findByCreditCard(CreditCard creditCard) {
        return bankMovementJpaDao.findByCreditCard(creditCard);
    }

    @Override
    public Optional<BankMovement> findByDate(Date date) {
        return bankMovementJpaDao.findByDate(date);
    }

    @Override
    public BankMovement save(BankMovement bankMovement) {
        return bankMovementJpaDao.save(bankMovement);
    }
}
