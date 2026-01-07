package org.example.bankback.controller.webmodel.response;

public record CreditCardRespose(
        Long id,
        String numeroTarjeta,
        String fechaCaducidad,
        String nombreCompleto
) {
}
