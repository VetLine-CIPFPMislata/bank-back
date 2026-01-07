package org.example.bankback.domain.service.impl;

import org.example.bankback.controller.webmodel.request.PagoTarjetaRequest;
import org.example.bankback.controller.webmodel.response.PagoTarjetaResponse;
import org.example.bankback.domain.models.Cliente;
import org.example.bankback.domain.models.CuentaBancaria;
import org.example.bankback.domain.models.TarjetaCredito;
import org.example.bankback.domain.service.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.regex.Pattern;

@Service
public class PagoTarjetaServiceImpl implements PagoTarjetaService {
    private final ClientService clientService;
    private final CreditCardService creditCardService;
    private final BankAccountService bankAccountService;

    public PagoTarjetaServiceImpl(ClientService clientService,
                                  CreditCardService creditCardService,
                                  BankAccountService bankAccountService) {
        this.clientService = clientService;
        this.creditCardService = creditCardService;
        this.bankAccountService = bankAccountService;
    }

    @Override
    public PagoTarjetaResponse procesarPago(PagoTarjetaRequest request) {
        if (request == null || request.autorizacion() == null || request.origen() == null
                || request.destino() == null || request.pago() == null) {
            return new PagoTarjetaResponse(null, null, null, "Petición incompleta", false);
        }

        if (!esIbanValido(request.destino().iban())) {
            return new PagoTarjetaResponse(null, null, null, "IBAN inválido o no empieza por ES", false);
        }
        if (!esImporteValido(request.pago().importe())) {
            return new PagoTarjetaResponse(null, null, null, "Importe debe ser positivo", false);
        }
        if (!esConceptoValido(request.pago().concepto())) {
            return new PagoTarjetaResponse(null, null, null, "Concepto debe tener al menos 3 caracteres", false);
        }

        Optional<Cliente> clienteOpt = clientService.findByUsername(request.autorizacion().login())
                .filter(c -> request.autorizacion().api_token().equals(c.getApi_token()));
        if (clienteOpt.isEmpty()) {
            return new PagoTarjetaResponse(null, null, null, "Autorización no válida", false);
        }

        TarjetaCredito tarjeta = creditCardService.findByCardNumber(request.origen().numeroTarjeta())
                .orElse(null);
        if (tarjeta == null) {
            return new PagoTarjetaResponse(null, null, null, "Tarjeta no encontrada", false);
        }
        if (!coincideTarjeta(tarjeta, request.origen())) {
            return new PagoTarjetaResponse(null, null, null, "Datos de tarjeta no coinciden", false);
        }

        String ibanDigits = request.destino().iban().replaceAll("\\s+", "");
        Optional<CuentaBancaria> cuentaDestinoOpt = bankAccountService.findByIBAN(new BigDecimal(ibanDigits.substring(2))); // usa servicios existentes (BigDecimal)
        if (cuentaDestinoOpt.isEmpty()) {
            return new PagoTarjetaResponse(null, null, null, "Cuenta destino no encontrada", false);
        }
        CuentaBancaria cuentaDestino = cuentaDestinoOpt.get();

        if (cuentaDestino.getSaldo() == null || cuentaDestino.getSaldo().compareTo(request.pago().importe()) < 0) {
            return new PagoTarjetaResponse(null, null, null, "Fondos insuficientes", false);
        }

        return new PagoTarjetaResponse(request.destino().iban(), request.pago().importe(), request.pago().concepto(), "Pago aceptado", true);
    }

    private boolean esIbanValido(String iban) {
        if (iban == null) return false;
        String trimmed = iban.replaceAll("\\s+", "");
        return Pattern.matches("ES[0-9]{22}", trimmed);
    }

    private boolean esImporteValido(BigDecimal importe) {
        return importe != null && importe.compareTo(BigDecimal.ZERO) > 0;
    }

    private boolean esConceptoValido(String concepto) {
        return concepto != null && concepto.trim().length() >= 3;
    }

    private boolean coincideTarjeta(TarjetaCredito tarjeta, PagoTarjetaRequest.Origen origen) {
        return tarjeta.getNumeroTarjeta().equals(origen.numeroTarjeta())
                && tarjeta.getFechaCaducidad().equals(origen.fechaCaducidad())
                && tarjeta.getCvc().equals(origen.cvc())
                && tarjeta.getNombreCompleto().equals(origen.nombreCompleto());
    }
}
