package org.example.bankback.controller.mapper;

import org.example.bankback.controller.webmodel.request.PagoTarjetaRequest;
import org.example.bankback.controller.webmodel.response.PagoTarjetaResponse;
import org.example.bankback.domain.models.dto.PagoTarjetaDTO;
import org.example.bankback.domain.models.dto.PagoTarjetaResponseDTO;


public class PagoTarjetaMapper {

    public PagoTarjetaDTO toDTO(PagoTarjetaRequest request) {
        if (request == null) return null;

        return new PagoTarjetaDTO(
            mapAutorizacion(request),
            mapOrigen(request),
            mapDestino(request),
            mapPago(request)
        );
    }

    public PagoTarjetaResponse toResponse(PagoTarjetaResponseDTO dto) {
        if (dto == null) return null;

        return new PagoTarjetaResponse(
            dto.ibanDestino(),
            dto.importe(),
            dto.concepto(),
            dto.mensaje(),
            dto.exito()
        );
    }

    private PagoTarjetaDTO.AutorizacionDTO mapAutorizacion(PagoTarjetaRequest request) {
        if (request.autorizacion() == null) return null;

        return new PagoTarjetaDTO.AutorizacionDTO(
            request.autorizacion().login(),
            request.autorizacion().api_token()
        );
    }

    private PagoTarjetaDTO.OrigenDTO mapOrigen(PagoTarjetaRequest request) {
        if (request.origen() == null) return null;

        return new PagoTarjetaDTO.OrigenDTO(
            request.origen().numeroTarjeta(),
            request.origen().fechaCaducidad(),
            request.origen().cvc(),
            request.origen().nombreCompleto()
        );
    }

    private PagoTarjetaDTO.DestinoDTO mapDestino(PagoTarjetaRequest request) {
        if (request.destino() == null) return null;

        return new PagoTarjetaDTO.DestinoDTO(
            request.destino().iban()
        );
    }

    private PagoTarjetaDTO.PagoDTO mapPago(PagoTarjetaRequest request) {
        if (request.pago() == null) return null;

        return new PagoTarjetaDTO.PagoDTO(
            request.pago().importe(),
            request.pago().concepto()
        );
    }
}
