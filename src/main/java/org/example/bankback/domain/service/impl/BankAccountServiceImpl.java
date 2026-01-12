package org.example.bankback.domain.service.impl;

import org.example.bankback.domain.models.BankAccount;
import org.example.bankback.domain.repository.BankAccountRepository;
import org.example.bankback.domain.service.BankAccountService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BankAccountServiceImpl implements BankAccountService {
    private final BankAccountRepository bankAccountRepository;

    public BankAccountServiceImpl(BankAccountRepository bankAccountRepository) {
        this.bankAccountRepository = bankAccountRepository;
    }

    @Override
    public Optional<BankAccount> findById(Long id) {
        return bankAccountRepository.findById(id);
    }

    @Override
    public Optional<BankAccount> findByIBAN(String iban) {
        return bankAccountRepository.findByIBAN(iban);
    }

    @Override
    public BankAccount save(BankAccount bankAccount) {
        return bankAccountRepository.save(bankAccount);
    }

    @Override
    public List<BankAccount> findByClientId(Long clientId) {
        return bankAccountRepository.findByClientId(clientId);
    }
}
