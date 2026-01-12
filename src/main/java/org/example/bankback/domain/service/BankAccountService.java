package org.example.bankback.domain.service;

import org.example.bankback.domain.models.BankAccount;

import java.util.List;
import java.util.Optional;

public interface BankAccountService {
    Optional<BankAccount> findById(Long id);
    Optional<BankAccount> findByIBAN(String iban);
    BankAccount save(BankAccount bankAccount);
    List<BankAccount> findByClientId(Long clientId);
}
