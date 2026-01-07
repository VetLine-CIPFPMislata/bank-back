package org.example.bankback.domain.repository;


import java.math.BigDecimal;
import java.util.Optional;

public interface BankAccountRepository {
    Optional<BankAccountDto> findById(Long id);
    Optional<BankAccountDto> findByIBAN(BigDecimal iban);
}
