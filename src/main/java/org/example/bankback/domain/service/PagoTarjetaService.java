package org.example.bankback.domain.service;

import org.example.bankback.domain.models.dto.PagoTarjetaDTO;
import org.example.bankback.domain.models.dto.PagoTarjetaResponseDTO;

public interface PagoTarjetaService {
    PagoTarjetaResponseDTO procesarPago(PagoTarjetaDTO request);
}
