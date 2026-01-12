package org.example.bankback.domain.repository;


import org.example.bankback.domain.models.BankAccount;
import java.util.List;
import java.util.Optional;

public interface BankAccountRepository {
    Optional<BankAccount> findById(Long id);
    Optional<BankAccount> findByIBAN(String iban);
    BankAccount save(BankAccount bankAccount);
    List<BankAccount> findByClientId(Long clientId);
}
