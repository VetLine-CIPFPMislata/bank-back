package org.example.bankback.domain.repository;

import java.util.Optional;

public interface CreditCardRepository {
    Optional<CreditCardDto> findByCardNumber(String cardNumber);
    Optional<CreditCardDto> findByName(String name);
}
