package org.example.bankback.domain.service.impl;

import org.example.bankback.domain.exception.ValidationException;
import org.example.bankback.domain.models.dto.PagoTarjetaResponseDTO;
import org.example.bankback.domain.models.dto.PagoTarjetaDTO;
import org.example.bankback.domain.models.BankAccount;
import org.example.bankback.domain.models.Client;
import org.example.bankback.domain.models.CreditCard;
import org.example.bankback.domain.models.BankMovement;
import org.example.bankback.domain.models.TypeBankMovement;
import org.example.bankback.domain.models.OriginBankMovement;
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
        validarPeticionCompleta(request);

        validarDatosPago(request);

        Client tienda = validarAutorizacion(request);

        String ibanNormalizado = normalizarIban(request.destino().iban());
        BankAccount cuentaDestino = validarCuentaDestino(ibanNormalizado, tienda);

        String numeroTarjetaNormalizado = normalizarNumeroTarjeta(request.origen().numeroTarjeta());
        CreditCard tarjeta = validarTarjeta(numeroTarjetaNormalizado, request.origen());

        BankAccount cuentaOrigen = validarCuentaOrigen(tarjeta, request.pago().importe());

        procesarTransferencia(cuentaOrigen, cuentaDestino, request.pago().importe());

        registrarMovimientos(tarjeta, cuentaOrigen, cuentaDestino, request.pago().importe(), request.pago().concepto());

        return crearRespuestaExito(ibanNormalizado, request.pago().importe(), request.pago().concepto());
    }

    private void validarPeticionCompleta(PagoTarjetaDTO request) {
        if (request == null || request.autorizacion() == null || request.origen() == null
                || request.destino() == null || request.pago() == null) {
            throw new ValidationException("Petición incompleta");
        }
    }

    private void validarDatosPago(PagoTarjetaDTO request) {
        if (!esImporteValido(request.pago().importe())) {
            throw new ValidationException("Importe debe ser positivo");
        }
        if (!esConceptoValido(request.pago().concepto())) {
            throw new ValidationException("Concepto debe tener al menos 3 caracteres");
        }
        if (!esIbanValido(request.destino().iban())) {
            throw new ValidationException("IBAN inválido o no empieza por ES");
        }
    }

    private Client validarAutorizacion(PagoTarjetaDTO request) {
        Optional<Client> tiendaOpt = clientService.findByUsername(request.autorizacion().login());
        if (tiendaOpt.isEmpty()) {
            throw new ValidationException("Autorización no válida");
        }
        return tiendaOpt.get();
    }

    private BankAccount validarCuentaDestino(String iban, Client tienda) {
        Optional<BankAccount> cuentaDestinoOpt = bankAccountService.findByIBAN(iban);
        if (cuentaDestinoOpt.isEmpty()) {
            throw new ValidationException("Cuenta destino no encontrada");
        }

        BankAccount cuentaDestino = cuentaDestinoOpt.get();
        if (cuentaDestino.getIdCliente() == null || !cuentaDestino.getIdCliente().equals(tienda.getId())) {
            throw new ValidationException("Cuenta destino no pertenece a la tienda");
        }

        return cuentaDestino;
    }

    private CreditCard validarTarjeta(String numeroTarjeta, PagoTarjetaDTO.OrigenDTO origen) {
        Optional<CreditCard> tarjetaOpt = creditCardService.findByCardNumber(numeroTarjeta);
        if (tarjetaOpt.isEmpty()) {
            throw new ValidationException("Tarjeta no encontrada");
        }

        CreditCard tarjeta = tarjetaOpt.get();
        if (!coincideTarjeta(tarjeta, origen)) {
            throw new ValidationException("Datos de la tarjeta no coinciden");
        }

        if (!tarjetaVigente(tarjeta.getFechaCaducidad())) {
            throw new ValidationException("Tarjeta caducada");
        }

        return tarjeta;
    }

    private BankAccount validarCuentaOrigen(CreditCard tarjeta, BigDecimal importe) {
        if (tarjeta.getIdCuentaBancaria() == null) {
            throw new ValidationException("La tarjeta no tiene cuenta asociada");
        }

        Optional<BankAccount> cuentaOrigenOpt = bankAccountService.findById(tarjeta.getIdCuentaBancaria());
        if (cuentaOrigenOpt.isEmpty()) {
            throw new ValidationException("Cuenta origen no encontrada");
        }

        BankAccount cuentaOrigen = cuentaOrigenOpt.get();
        if (cuentaOrigen.getSaldo() == null || cuentaOrigen.getSaldo().compareTo(importe) < 0) {
            throw new ValidationException("Fondos insuficientes");
        }

        return cuentaOrigen;
    }

    private void procesarTransferencia(BankAccount cuentaOrigen, BankAccount cuentaDestino, BigDecimal importe) {
        BigDecimal nuevoSaldoOrigen = cuentaOrigen.getSaldo().subtract(importe);
        BigDecimal nuevoSaldoDestino = cuentaDestino.getSaldo().add(importe);

        cuentaOrigen.setSaldo(nuevoSaldoOrigen);
        cuentaDestino.setSaldo(nuevoSaldoDestino);

        bankAccountService.save(cuentaOrigen);
        bankAccountService.save(cuentaDestino);
    }


    private void registrarMovimientos(CreditCard tarjeta, BankAccount cuentaOrigen, BankAccount cuentaDestino, BigDecimal importe, String concepto) {
        // Movimiento de débito en cuenta origen (con tarjeta)
        BankMovement movimientoDebe = crearMovimientoPago(tarjeta, cuentaOrigen, importe, concepto);
        bankMovementService.save(movimientoDebe);

        // Movimiento de ingreso en cuenta destino (sin tarjeta, solo cuenta)
        BankMovement movimientoHaber = crearMovimientoIngreso(cuentaDestino, importe, concepto);
        bankMovementService.save(movimientoHaber);
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

    private BankMovement crearMovimientoPago(CreditCard tarjeta, BankAccount cuenta, BigDecimal importe, String concepto) {
        return new BankMovement(
                null,
                DEBE,
                TARJETA,
                tarjeta,
                cuenta,
                new Date(),
                importe,
                concepto
        );
    }

    private BankMovement crearMovimientoIngreso(BankAccount cuentaDestino, BigDecimal importe, String concepto) {
        return new BankMovement(
                null,
                TypeBankMovement.HABER,
                OriginBankMovement.TRANSFERENCIA,
                null, // Sin tarjeta, es un ingreso por transferencia
                cuentaDestino,
                new Date(),
                importe,
                concepto
        );
    }
}
