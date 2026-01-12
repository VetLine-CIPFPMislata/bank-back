package org.example.bankback.persistence.repository;

import org.example.bankback.domain.models.CreditCard;
import org.example.bankback.domain.repository.CreditCardRepository;
import org.example.bankback.persistence.dao.CreditCardJpaDao;


import java.util.List;
import java.util.Optional;


public class CreditCardRepositoryImpl implements CreditCardRepository {

    private final CreditCardJpaDao creditCardJpaDao;

    public CreditCardRepositoryImpl(CreditCardJpaDao creditCardJpaDao) {
        this.creditCardJpaDao = creditCardJpaDao;
    }

    @Override
    public Optional<CreditCard> findByCardNumber(String cardNumber) {
        return creditCardJpaDao.findByCardNumber(cardNumber);
    }

    @Override
    public Optional<CreditCard> findByName(String name) {
        return creditCardJpaDao.findByName(name).stream().findFirst();
    }

    @Override
    public List<CreditCard> findByBankAccountId(Long bankAccountId) {
        return creditCardJpaDao.findByBankAccountId(bankAccountId);
    }
}
