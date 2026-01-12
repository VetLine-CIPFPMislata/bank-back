package org.example.bankback.persistence.repository;

import org.example.bankback.domain.models.BankAccount;
import org.example.bankback.domain.repository.BankAccountRepository;
import org.example.bankback.persistence.dao.BankAccountJpaDao;


import java.util.List;
import java.util.Optional;

public class BankAccountRepositoryImpl implements BankAccountRepository {

    private final BankAccountJpaDao bankAccountJpaDao;

    public BankAccountRepositoryImpl(BankAccountJpaDao bankAccountJpaDao) {
        this.bankAccountJpaDao = bankAccountJpaDao;
    }

    @Override
    public Optional<BankAccount> findById(Long id) {
        return bankAccountJpaDao.findById(id);
    }

    @Override
    public Optional<BankAccount> findByIBAN(String iban) {
        return bankAccountJpaDao.findByIBAN(iban);
    }

    @Override
    public BankAccount save(BankAccount bankAccount) {
        return bankAccountJpaDao.save(bankAccount);
    }

    @Override
    public List<BankAccount> findByClientId(Long clientId) {
        return bankAccountJpaDao.findByClientId(clientId);
    }
}
