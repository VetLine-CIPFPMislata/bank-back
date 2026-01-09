package org.example.bankback.controller.webmodel.response;


import org.example.bankback.domain.models.OriginBankMovement;
import org.example.bankback.domain.models.TypeBankMovement;

import java.math.BigDecimal;
import java.util.Date;

public record BankMovementResponse(
        Long id,
        TypeBankMovement tipoMovimiento,
        OriginBankMovement origen,
        Long tarjetaCreditoId,
        Date fechaMovimiento,
        BigDecimal importe,
        String concepto
) {
}
