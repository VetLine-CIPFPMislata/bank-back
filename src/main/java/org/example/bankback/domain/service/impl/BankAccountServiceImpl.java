package org.example.bankback.domain.service.impl;

import org.example.bankback.domain.models.CuentaBancaria;
import org.example.bankback.domain.repository.BankAccountRepository;
import org.example.bankback.domain.service.BankAccountService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class BankAccountServiceImpl implements BankAccountService {
    private final BankAccountRepository bankAccountRepository;

    public BankAccountServiceImpl(BankAccountRepository bankAccountRepository) {
        this.bankAccountRepository = bankAccountRepository;
    }

    @Override
    public Optional<CuentaBancaria> findById(Long id) {
        return bankAccountRepository.findById(id);
    }

    @Override
    public Optional<CuentaBancaria> findByIBAN(BigDecimal iban) {
        return bankAccountRepository.findByIBAN(iban);
    }
}
