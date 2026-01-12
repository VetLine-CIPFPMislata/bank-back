package org.example.bankback.controller.webmodel.response;

public record LoginResponse(
    boolean exito,
    String mensaje,
    ClientData cliente
) {
    public record ClientData(
        Long id,
        String username,
        String nombre,
        String apellido1,
        String apellido2,
        String dni
    ) {}
}
