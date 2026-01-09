package org.example.bankback.domain.models.dto;

import java.math.BigDecimal;

public record PagoTarjetaResponseDTO(
        String ibanDestino,
        BigDecimal importe,
        String concepto,
        String mensaje,
        boolean exito
) {
}

