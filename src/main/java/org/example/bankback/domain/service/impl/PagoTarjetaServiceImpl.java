package org.example.bankback.domain.service.impl;

import org.example.bankback.domain.models.dto.PagoTarjetaResponseDTO;
import org.example.bankback.domain.models.dto.PagoTarjetaDTO;
import org.example.bankback.domain.models.BankAccount;
import org.example.bankback.domain.models.Client;
import org.example.bankback.domain.models.CreditCard;
import org.example.bankback.domain.models.BankMovement;
import org.example.bankback.domain.service.*;


import java.math.BigDecimal;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.Optional;
import java.util.regex.Pattern;

import static org.example.bankback.domain.models.OriginBankMovement.TARJETA;
import static org.example.bankback.domain.models.TypeBankMovement.DEBE;


public class PagoTarjetaServiceImpl implements PagoTarjetaService {
    private final ClientService clientService;
    private final CreditCardService creditCardService;
    private final BankAccountService bankAccountService;
    private final BankMovementService bankMovementService;

    public PagoTarjetaServiceImpl(ClientService clientService,
                                  CreditCardService creditCardService,
                                  BankAccountService bankAccountService,
                                  BankMovementService bankMovementService) {
        this.clientService = clientService;
        this.creditCardService = creditCardService;
        this.bankAccountService = bankAccountService;
        this.bankMovementService = bankMovementService;
    }

    @Override
    public PagoTarjetaResponseDTO procesarPago(PagoTarjetaDTO request) {

        PagoTarjetaResponseDTO validacionPeticion = validarPeticionCompleta(request);
        if (validacionPeticion != null) return validacionPeticion;


        PagoTarjetaResponseDTO validacionDatos = validarDatosPago(request);
        if (validacionDatos != null) return validacionDatos;


        Optional<Client> tiendaOpt = validarAutorizacion(request);
        if (tiendaOpt.isEmpty()) {
            return crearRespuestaError("Autorización no válida");
        }
        Client tienda = tiendaOpt.get();


        String ibanNormalizado = normalizarIban(request.destino().iban());
        Optional<BankAccount> cuentaDestinoOpt = validarCuentaDestino(ibanNormalizado, tienda);
        if (cuentaDestinoOpt.isEmpty()) {
            return crearRespuestaError("Cuenta destino no encontrada o no pertenece a la tienda");
        }
        BankAccount cuentaDestino = cuentaDestinoOpt.get();


        String numeroTarjetaNormalizado = normalizarNumeroTarjeta(request.origen().numeroTarjeta());
        Optional<CreditCard> tarjetaOpt = validarTarjeta(numeroTarjetaNormalizado, request.origen());
        if (tarjetaOpt.isEmpty()) {
            return crearRespuestaError("Tarjeta no válida");
        }
        CreditCard tarjeta = tarjetaOpt.get();


        Optional<BankAccount> cuentaOrigenOpt = validarCuentaOrigen(tarjeta, request.pago().importe());
        if (cuentaOrigenOpt.isEmpty()) {
            return crearRespuestaError("Cuenta origen no encontrada o fondos insuficientes");
        }
        BankAccount cuentaOrigen = cuentaOrigenOpt.get();


        procesarTransferencia(cuentaOrigen, cuentaDestino, request.pago().importe());


        registrarMovimiento(tarjeta, request.pago().importe(), request.pago().concepto());

        return crearRespuestaExito(ibanNormalizado, request.pago().importe(), request.pago().concepto());
    }


    private PagoTarjetaResponseDTO validarPeticionCompleta(PagoTarjetaDTO request) {
        if (request == null || request.autorizacion() == null || request.origen() == null
                || request.destino() == null || request.pago() == null) {
            return crearRespuestaError("Petición incompleta");
        }
        return null;
    }


    private PagoTarjetaResponseDTO validarDatosPago(PagoTarjetaDTO request) {
        if (!esImporteValido(request.pago().importe())) {
            return crearRespuestaError("Importe debe ser positivo");
        }
        if (!esConceptoValido(request.pago().concepto())) {
            return crearRespuestaError("Concepto debe tener al menos 3 caracteres");
        }
        if (!esIbanValido(request.destino().iban())) {
            return crearRespuestaError("IBAN inválido o no empieza por ES");
        }
        return null;
    }


    private Optional<Client> validarAutorizacion(PagoTarjetaDTO request) {
        Optional<Client> tiendaOpt = clientService.findByUsername(request.autorizacion().login());
        if (tiendaOpt.isEmpty()) {
            return Optional.empty();
        }

        Client tienda = tiendaOpt.get();
        if (!request.autorizacion().api_token().equals(tienda.getApi_token())) {
            return Optional.empty();
        }

        return tiendaOpt;
    }


    private Optional<BankAccount> validarCuentaDestino(String iban, Client tienda) {
        Optional<BankAccount> cuentaDestinoOpt = bankAccountService.findByIBAN(iban);
        if (cuentaDestinoOpt.isEmpty()) {
            return Optional.empty();
        }

        BankAccount cuentaDestino = cuentaDestinoOpt.get();
        if (cuentaDestino.getIdCliente() == null || !cuentaDestino.getIdCliente().equals(tienda.getId())) {
            return Optional.empty();
        }

        return cuentaDestinoOpt;
    }


    private Optional<CreditCard> validarTarjeta(String numeroTarjeta, PagoTarjetaDTO.OrigenDTO origen) {
        Optional<CreditCard> tarjetaOpt = creditCardService.findByCardNumber(numeroTarjeta);
        if (tarjetaOpt.isEmpty()) {
            return Optional.empty();
        }

        CreditCard tarjeta = tarjetaOpt.get();
        if (!coincideTarjeta(tarjeta, origen)) {
            return Optional.empty();
        }

        if (!tarjetaVigente(tarjeta.getFechaCaducidad())) {
            return Optional.empty();
        }

        return tarjetaOpt;
    }


    private Optional<BankAccount> validarCuentaOrigen(CreditCard tarjeta, BigDecimal importe) {
        if (tarjeta.getIdCuentaBancaria() == null) {
            return Optional.empty();
        }

        Optional<BankAccount> cuentaOrigenOpt = bankAccountService.findById(tarjeta.getIdCuentaBancaria());
        if (cuentaOrigenOpt.isEmpty()) {
            return Optional.empty();
        }

        BankAccount cuentaOrigen = cuentaOrigenOpt.get();
        if (cuentaOrigen.getSaldo() == null || cuentaOrigen.getSaldo().compareTo(importe) < 0) {
            return Optional.empty();
        }

        return cuentaOrigenOpt;
    }

    private void procesarTransferencia(BankAccount cuentaOrigen, BankAccount cuentaDestino, BigDecimal importe) {
        BigDecimal nuevoSaldoOrigen = cuentaOrigen.getSaldo().subtract(importe);
        BigDecimal nuevoSaldoDestino = cuentaDestino.getSaldo().add(importe);

        cuentaOrigen.setSaldo(nuevoSaldoOrigen);
        cuentaDestino.setSaldo(nuevoSaldoDestino);

        bankAccountService.save(cuentaOrigen);
        bankAccountService.save(cuentaDestino);
    }


    private void registrarMovimiento(CreditCard tarjeta, BigDecimal importe, String concepto) {
        BankMovement movimiento = crearMovimientoPago(tarjeta, importe, concepto);
        bankMovementService.save(movimiento);
    }


    private PagoTarjetaResponseDTO crearRespuestaError(String mensaje) {
        return new PagoTarjetaResponseDTO(null, null, null, mensaje, false);
    }

    private PagoTarjetaResponseDTO crearRespuestaExito(String iban, BigDecimal importe, String concepto) {
        return new PagoTarjetaResponseDTO(iban, importe, concepto, "Pago aceptado", true);
    }

    private boolean esIbanValido(String iban) {
        if (iban == null) return false;
        String normalizado = iban.replaceAll("\\s+", "");
        return Pattern.matches("ES[0-9]{22}", normalizado);
    }

    private boolean esImporteValido(BigDecimal importe) {
        return importe != null && importe.compareTo(BigDecimal.ZERO) > 0;
    }

    private boolean esConceptoValido(String concepto) {
        return concepto != null && concepto.trim().length() >= 3;
    }

    private String normalizarIban(String iban) {
        return iban.replaceAll("\\s+", "");
    }

    private String normalizarNumeroTarjeta(String numeroTarjeta) {
        return numeroTarjeta.replaceAll("[\\s-]", "");
    }

    private boolean coincideTarjeta(CreditCard tarjeta, PagoTarjetaDTO.OrigenDTO origen) {
        String numeroTarjetaNormalizado = normalizarNumeroTarjeta(origen.numeroTarjeta());
        String numeroTarjetaBDNormalizado = normalizarNumeroTarjeta(tarjeta.getNumeroTarjeta());

        return numeroTarjetaBDNormalizado.equals(numeroTarjetaNormalizado)
                && tarjeta.getFechaCaducidad().equals(origen.fechaCaducidad())
                && tarjeta.getCvc().equals(origen.cvc())
                && tarjeta.getNombreCompleto().equalsIgnoreCase(origen.nombreCompleto());
    }

    private boolean tarjetaVigente(String fechaCaducidad) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
            YearMonth fechaCaducidadParsed = YearMonth.parse(fechaCaducidad, formatter);
            YearMonth ahora = YearMonth.now();

            return !fechaCaducidadParsed.isBefore(ahora);
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    private BankMovement crearMovimientoPago(CreditCard tarjeta, BigDecimal importe, String concepto) {
        BankMovement movimiento = new BankMovement(
                null,
                DEBE,
                TARJETA,
                tarjeta,
                new Date(),
                importe,
                concepto
        );
        return movimiento;
    }
}
