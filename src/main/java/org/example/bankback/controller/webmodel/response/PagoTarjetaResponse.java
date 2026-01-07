package org.example.bankback.controller.webmodel.response;
import java.math.BigDecimal;

public record PagoTarjetaResponse(
        String ibanDestino,
        BigDecimal importe,
        String concepto,
        String mensaje,
        boolean exito
) {
}
