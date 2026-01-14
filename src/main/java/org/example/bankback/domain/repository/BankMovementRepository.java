package org.example.bankback.domain.repository;

import org.example.bankback.domain.models.BankMovement;
import org.example.bankback.domain.models.CreditCard;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface BankMovementRepository {
    Optional<BankMovement> findById(Long id);
    Optional<BankMovement> findByCreditCard(CreditCard creditCard);
    Optional<BankMovement> findByDate(Date date);
    BankMovement save(BankMovement bankMovement);
    List<BankMovement> findAllByCreditCardId(Long creditCardId);
    List<BankMovement> findAllByBankAccountId(Long bankAccountId);
}
