package org.example.bankback.controller.webmodel.response;

import org.example.bankback.domain.models.OrigenMovimientoBancario;
import org.example.bankback.domain.models.TipoMovimientoBancario;

import java.math.BigDecimal;
import java.util.Date;

public record BankMovementResponse(
        Long id,
        TipoMovimientoBancario tipoMovimiento,
        OrigenMovimientoBancario origen,
        Long tarjetaCreditoId,
        Date fechaMovimiento,
        BigDecimal importe,
        String concepto
) {
}
