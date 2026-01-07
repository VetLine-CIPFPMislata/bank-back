package org.example.bankback.controller.webmodel.request;

import java.math.BigDecimal;

public record PagoTarjetaRequest(
        Autorizacion autorizacion,
        Origen origen,
        Destino destino,
        Pago pago
) {
    public record Autorizacion(String login, String api_token) {}
    public record Origen(String numeroTarjeta, String fechaCaducidad, String cvc, String nombreCompleto) {}
    public record Destino(String iban) {}
    public record Pago(BigDecimal importe, String concepto) {}
}

