package org.example.bankback.domain.repository;

import org.example.bankback.domain.models.CreditCard;
import java.util.List;
import java.util.Optional;

public interface CreditCardRepository {
    Optional<CreditCard> findByCardNumber(String cardNumber);
    Optional<CreditCard> findByName(String name);
    List<CreditCard> findByBankAccountId(Long bankAccountId);
}
