package org.example.bankback.domain.service;

import org.example.bankback.domain.models.CuentaBancaria;

import java.math.BigDecimal;
import java.util.Optional;

public interface BankAccountService {
    Optional<CuentaBancaria> findById(Long id);
    Optional<CuentaBancaria> findByIBAN(BigDecimal iban);
}
