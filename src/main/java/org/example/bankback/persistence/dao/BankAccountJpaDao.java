package org.example.bankback.persistence.dao;

import org.example.bankback.domain.models.BankAccount;

import java.util.Optional;

public interface BankAccountJpaDao {
    Optional<BankAccount> findById(Long id);
    Optional<BankAccount> findByIBAN(String iban);
    BankAccount save(BankAccount bankAccount);
}
