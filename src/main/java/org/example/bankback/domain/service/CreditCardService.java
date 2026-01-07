package org.example.bankback.domain.service;

import org.example.bankback.domain.models.TarjetaCredito;

import java.util.Optional;

public interface CreditCardService {
    Optional<TarjetaCredito> findByCardNumber(String cardNumber);
    Optional<TarjetaCredito> findByName(String name);
}
