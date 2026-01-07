package org.example.bankback.domain.service;

import org.example.bankback.controller.webmodel.request.PagoTarjetaRequest;
import org.example.bankback.controller.webmodel.response.PagoTarjetaResponse;

public interface PagoTarjetaService {
    PagoTarjetaResponse procesarPago(PagoTarjetaRequest request);
}

