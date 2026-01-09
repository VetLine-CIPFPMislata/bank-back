package org.example.bankback.controller;

import org.example.bankback.controller.mapper.PagoTarjetaMapper;
import org.example.bankback.controller.webmodel.request.PagoTarjetaRequest;
import org.example.bankback.controller.webmodel.response.PagoTarjetaResponse;
import org.example.bankback.domain.models.dto.PagoTarjetaDTO;
import org.example.bankback.domain.models.dto.PagoTarjetaResponseDTO;
import org.example.bankback.domain.service.PagoTarjetaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class BankController {

    private final PagoTarjetaService pagoTarjetaService;
    private final PagoTarjetaMapper mapper;

    public BankController(PagoTarjetaService pagoTarjetaService, PagoTarjetaMapper mapper) {
        this.pagoTarjetaService = pagoTarjetaService;
        this.mapper = mapper;
    }

    @PostMapping("/pago_tarjeta")
    public ResponseEntity<PagoTarjetaResponse> pagarConTarjeta(@RequestBody PagoTarjetaRequest request) {
        PagoTarjetaDTO dto = mapper.toDTO(request);
        PagoTarjetaResponseDTO responseDTO = pagoTarjetaService.procesarPago(dto);
        PagoTarjetaResponse response = mapper.toResponse(responseDTO);

        if (!response.exito()) {
            return ResponseEntity.badRequest().body(response);
        }
        return ResponseEntity.ok(response);
    }
}
