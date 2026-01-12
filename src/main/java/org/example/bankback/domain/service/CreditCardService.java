package org.example.bankback.domain.service;

import org.example.bankback.domain.models.CreditCard;

import java.util.List;
import java.util.Optional;

public interface CreditCardService {
    Optional<CreditCard> findByCardNumber(String cardNumber);
    Optional<CreditCard> findByName(String name);
    List<CreditCard> findByBankAccountId(Long bankAccountId);
}
