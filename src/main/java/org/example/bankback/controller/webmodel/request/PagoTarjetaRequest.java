package org.example.bankback.controller.webmodel.request;

public record PagoTarjetaRequest(
        AutorizacionRequest autorizacion,
        OrigenRequest origen,
        DestinoRequest destino,
        PagoDetailsRequest pago
) {
}
