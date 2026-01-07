package org.example.bankback.controller.webmodel.response;

public record ClientResponse(
        Long id,
        String username,
        String nombre,
        String apellido1,
        String apellido2,
        String dni
) {
}
