package org.example.bankback.domain.service.impl;

import org.example.bankback.domain.models.BankAccount;
import org.example.bankback.domain.models.BankMovement;
import org.example.bankback.domain.models.CreditCard;
import org.example.bankback.domain.repository.BankMovementRepository;
import org.example.bankback.domain.service.BankMovementService;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
public class BankMovementServiceImpl implements BankMovementService {
    private final BankMovementRepository bankMovementRepository;

    public BankMovementServiceImpl(BankMovementRepository bankMovementRepository) {
        this.bankMovementRepository = bankMovementRepository;
    }

    @Override
    public Optional<BankMovement> findById(Long id) {
        return bankMovementRepository.findById(id);
    }

    @Override
    public Optional<BankMovement> findByCreditCard(CreditCard tarjetaCredito) {
        return bankMovementRepository.findByCreditCard(tarjetaCredito);
    }

    @Override
    public Optional<BankMovement> findByDate(Date date) {
        return bankMovementRepository.findByDate(date);
    }

    @Override
    public BankMovement save(BankMovement bankMovement) {
        return bankMovementRepository.save(bankMovement);
    }
}
