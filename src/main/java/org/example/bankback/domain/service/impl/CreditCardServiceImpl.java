package org.example.bankback.domain.service.impl;

import org.example.bankback.domain.models.TarjetaCredito;
import org.example.bankback.domain.repository.CreditCardRepository;
import org.example.bankback.domain.service.CreditCardService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CreditCardServiceImpl implements CreditCardService {

    private final CreditCardRepository creditCardRepository;

    public CreditCardServiceImpl(CreditCardRepository creditCardRepository) {
        this.creditCardRepository = creditCardRepository;
    }

    @Override
    public Optional<TarjetaCredito> findByCardNumber(String cardNumber) {
        return creditCardRepository.findByCardNumber(cardNumber);
    }

    @Override
    public Optional<TarjetaCredito> findByName(String name) {
        return creditCardRepository.findByName(name);
    }
}
