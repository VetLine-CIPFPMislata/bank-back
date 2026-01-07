package org.example.bankback.controller.webmodel.response;

import java.math.BigDecimal;

public record BankAccountResponse(
        Long id,
        String iban,
        BigDecimal saldo
) {
}
