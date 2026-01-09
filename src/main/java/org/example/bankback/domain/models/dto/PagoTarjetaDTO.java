package org.example.bankback.domain.models.dto;

import java.math.BigDecimal;

public record PagoTarjetaDTO(
        AutorizacionDTO autorizacion,
        OrigenDTO origen,
        DestinoDTO destino,
        PagoDTO pago
) {
    public record AutorizacionDTO(String login, String api_token) {}
    public record OrigenDTO(String numeroTarjeta, String fechaCaducidad, String cvc, String nombreCompleto) {}
    public record DestinoDTO(String iban) {}
    public record PagoDTO(BigDecimal importe, String concepto) {}
}

