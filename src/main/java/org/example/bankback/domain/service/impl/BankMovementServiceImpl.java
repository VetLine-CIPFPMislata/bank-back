package org.example.bankback.domain.service.impl;

import org.example.bankback.domain.models.MovimientoBancario;
import org.example.bankback.domain.models.TarjetaCredito;
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
    public Optional<MovimientoBancario> findById(Long id) {
        return bankMovementRepository.findById(id);
    }

    @Override
    public Optional<MovimientoBancario> findByCreditCard(TarjetaCredito tarjetaCredito) {
        return bankMovementRepository.findByCreditCard(tarjetaCredito);
    }

    @Override
    public Optional<MovimientoBancario> findByDate(Date date) {
        return bankMovementRepository.findByDate(date);
    }
}
