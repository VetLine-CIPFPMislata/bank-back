package org.example.bankback.domain.service;

import org.example.bankback.domain.models.MovimientoBancario;
import org.example.bankback.domain.models.TarjetaCredito;

import java.util.Date;
import java.util.Optional;

public interface BankMovementService {
    Optional<MovimientoBancario> findById(Long id);
    Optional<MovimientoBancario> findByCreditCard(TarjetaCredito tarjetaCredito);
    Optional<MovimientoBancario> findByDate(Date date);
}
