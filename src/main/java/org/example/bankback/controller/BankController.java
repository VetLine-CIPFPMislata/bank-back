package org.example.bankback.controller;

import org.example.bankback.controller.webmodel.request.PagoTarjetaRequest;
import org.example.bankback.controller.webmodel.response.PagoTarjetaResponse;
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

    public BankController(PagoTarjetaService pagoTarjetaService) {
        this.pagoTarjetaService = pagoTarjetaService;
    }

    @PostMapping("/pago_tarjeta")
    public ResponseEntity<PagoTarjetaResponse> pagarConTarjeta(@RequestBody PagoTarjetaRequest request) {
        PagoTarjetaResponse response = pagoTarjetaService.procesarPago(request);
        if (!response.exito()) {
            return ResponseEntity.badRequest().body(response);
        }
        return ResponseEntity.ok(response);
    }
}
