package org.example.bankback.domain.repository;

import java.util.Date;
import java.util.Optional;

public interface BankMovementRepository {
    Optional<BankMovementDto>findById(Long id);
    Optional<BankMovementDto> findByCreditCard(CreditCardDto creditCardDto);
    Optional<BankMovementDto> findByDate(Date date);

}
