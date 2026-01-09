//package org.example.bankback.domain.service.impl;
//
//import org.example.bankback.controller.webmodel.request.PagoTarjetaRequest;
//import org.example.bankback.controller.webmodel.response.PagoTarjetaResponse;
//import org.example.bankback.domain.models.Cliente;
//import org.example.bankback.domain.service.BankAccountService;
//import org.example.bankback.domain.service.ClientService;
//import org.example.bankback.domain.service.CreditCardService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import java.math.BigDecimal;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//public class PagoTarjetaServiceImplTest {
//
//    @Mock
//    private ClientService clientService;
//
//    @Mock
//    private CreditCardService creditCardService;
//
//    @Mock
//    private BankAccountService bankAccountService;
//
//    @InjectMocks
//    private PagoTarjetaServiceImpl pagoTarjetaService;
//
//    private PagoTarjetaRequest validRequest;
//    private Cliente validCliente;
//    private TarjetaCredito validTarjeta;
//    private CuentaBancaria validCuenta;
//
//    @BeforeEach
//    void setUp() {
//        // Cliente válido
//        validCliente = new Cliente(1L, "juan", "password", "Juan", "Garcia", "Garcia", "12345678A", "5f5ca67f-4c02-47cf-8753-a7790f7f5be1");
//
//        // Tarjeta válida
//        validTarjeta = new TarjetaCredito(1L, "4111111111111111", "2027-12", "123", "JUAN GARCIA GARCIA");
//
//        // Cuenta válida con saldo suficiente
//        validCuenta = new CuentaBancaria(1L, "ES6112343456420456325555", new BigDecimal("1000.00"));
//
//        // Request válido
//        validRequest = new PagoTarjetaRequest(
//                new PagoTarjetaRequest.Autorizacion("juan", "5f5ca67f-4c02-47cf-8753-a7790f7f5be1"),
//                new PagoTarjetaRequest.Origen("4111111111111111", "2027-12", "123", "JUAN GARCIA GARCIA"),
//                new PagoTarjetaRequest.Destino("ES61 1234 3456 4204 5632 5555"),
//                new PagoTarjetaRequest.Pago(new BigDecimal("567.67"), "Comprar PC")
//        );
//    }
//
//    // Tests para validación de IBAN
//    @Test
//    void testPagoConIbanInvalido() {
//        PagoTarjetaRequest request = new PagoTarjetaRequest(
//                new PagoTarjetaRequest.Autorizacion("juan", "5f5ca67f-4c02-47cf-8753-a7790f7f5be1"),
//                new PagoTarjetaRequest.Origen("4111111111111111", "2027-12", "123", "JUAN GARCIA GARCIA"),
//                new PagoTarjetaRequest.Destino("FR61 1234 3456 4204 5632 5555"),
//                new PagoTarjetaRequest.Pago(new BigDecimal("567.67"), "Comprar PC")
//        );
//
//        PagoTarjetaResponse response = pagoTarjetaService.procesarPago(request);
//
//        assertFalse(response.exito());
//        assertEquals("IBAN inválido o no empieza por ES", response.mensaje());
//    }
//
//    @Test
//    void testPagoConIbanFormatoInvalido() {
//        PagoTarjetaRequest request = new PagoTarjetaRequest(
//                new PagoTarjetaRequest.Autorizacion("juan", "5f5ca67f-4c02-47cf-8753-a7790f7f5be1"),
//                new PagoTarjetaRequest.Origen("4111111111111111", "2027-12", "123", "JUAN GARCIA GARCIA"),
//                new PagoTarjetaRequest.Destino("ES6112343456"),
//                new PagoTarjetaRequest.Pago(new BigDecimal("567.67"), "Comprar PC")
//        );
//
//        PagoTarjetaResponse response = pagoTarjetaService.procesarPago(request);
//
//        assertFalse(response.exito());
//        assertEquals("IBAN inválido o no empieza por ES", response.mensaje());
//    }
//
//    @Test
//    void testPagoConIbanValido() {
//        when(clientService.findByUsername("juan")).thenReturn(Optional.of(validCliente));
//        when(creditCardService.findByCardNumber("4111111111111111")).thenReturn(Optional.of(validTarjeta));
//        when(bankAccountService.findByIBAN(any())).thenReturn(Optional.of(validCuenta));
//
//        PagoTarjetaResponse response = pagoTarjetaService.procesarPago(validRequest);
//
//        assertTrue(response.exito());
//        assertEquals("Pago aceptado", response.mensaje());
//    }
//
//    // Tests para validación de importe
//    @Test
//    void testPagoConImporteNegativo() {
//        PagoTarjetaRequest request = new PagoTarjetaRequest(
//                new PagoTarjetaRequest.Autorizacion("juan", "5f5ca67f-4c02-47cf-8753-a7790f7f5be1"),
//                new PagoTarjetaRequest.Origen("4111111111111111", "2027-12", "123", "JUAN GARCIA GARCIA"),
//                new PagoTarjetaRequest.Destino("ES61 1234 3456 4204 5632 5555"),
//                new PagoTarjetaRequest.Pago(new BigDecimal("-100.00"), "Comprar PC")
//        );
//
//        PagoTarjetaResponse response = pagoTarjetaService.procesarPago(request);
//
//        assertFalse(response.exito());
//        assertEquals("Importe debe ser positivo", response.mensaje());
//    }
//
//    @Test
//    void testPagoConImporteCero() {
//        PagoTarjetaRequest request = new PagoTarjetaRequest(
//                new PagoTarjetaRequest.Autorizacion("juan", "5f5ca67f-4c02-47cf-8753-a7790f7f5be1"),
//                new PagoTarjetaRequest.Origen("4111111111111111", "2027-12", "123", "JUAN GARCIA GARCIA"),
//                new PagoTarjetaRequest.Destino("ES61 1234 3456 4204 5632 5555"),
//                new PagoTarjetaRequest.Pago(new BigDecimal("0.00"), "Comprar PC")
//        );
//
//        PagoTarjetaResponse response = pagoTarjetaService.procesarPago(request);
//
//        assertFalse(response.exito());
//        assertEquals("Importe debe ser positivo", response.mensaje());
//    }
//
//    @Test
//    void testPagoConImportePositivo() {
//        when(clientService.findByUsername("juan")).thenReturn(Optional.of(validCliente));
//        when(creditCardService.findByCardNumber("4111111111111111")).thenReturn(Optional.of(validTarjeta));
//        when(bankAccountService.findByIBAN(any())).thenReturn(Optional.of(validCuenta));
//
//        PagoTarjetaResponse response = pagoTarjetaService.procesarPago(validRequest);
//
//        assertTrue(response.exito());
//        assertEquals(new BigDecimal("567.67"), response.importe());
//    }
//
//    // Tests para validación de concepto
//    @Test
//    void testPagoConConceptoMenorA3Caracteres() {
//        PagoTarjetaRequest request = new PagoTarjetaRequest(
//                new PagoTarjetaRequest.Autorizacion("juan", "5f5ca67f-4c02-47cf-8753-a7790f7f5be1"),
//                new PagoTarjetaRequest.Origen("4111111111111111", "2027-12", "123", "JUAN GARCIA GARCIA"),
//                new PagoTarjetaRequest.Destino("ES61 1234 3456 4204 5632 5555"),
//                new PagoTarjetaRequest.Pago(new BigDecimal("567.67"), "AB")
//        );
//
//        PagoTarjetaResponse response = pagoTarjetaService.procesarPago(request);
//
//        assertFalse(response.exito());
//        assertEquals("Concepto debe tener al menos 3 caracteres", response.mensaje());
//    }
//
//    @Test
//    void testPagoConConceptoExactamente3Caracteres() {
//        PagoTarjetaRequest request = new PagoTarjetaRequest(
//                new PagoTarjetaRequest.Autorizacion("juan", "5f5ca67f-4c02-47cf-8753-a7790f7f5be1"),
//                new PagoTarjetaRequest.Origen("4111111111111111", "2027-12", "123", "JUAN GARCIA GARCIA"),
//                new PagoTarjetaRequest.Destino("ES61 1234 3456 4204 5632 5555"),
//                new PagoTarjetaRequest.Pago(new BigDecimal("567.67"), "ABC")
//        );
//
//        when(clientService.findByUsername("juan")).thenReturn(Optional.of(validCliente));
//        when(creditCardService.findByCardNumber("4111111111111111")).thenReturn(Optional.of(validTarjeta));
//        when(bankAccountService.findByIBAN(any())).thenReturn(Optional.of(validCuenta));
//
//        PagoTarjetaResponse response = pagoTarjetaService.procesarPago(request);
//
//        assertTrue(response.exito());
//        assertEquals("ABC", response.concepto());
//    }
//
//    // Tests para autenticación
//    @Test
//    void testPagoConAutorizacionNoValida() {
//        when(clientService.findByUsername("juan")).thenReturn(Optional.empty());
//
//        PagoTarjetaResponse response = pagoTarjetaService.procesarPago(validRequest);
//
//        assertFalse(response.exito());
//        assertEquals("Autorización no válida", response.mensaje());
//    }
//
//    @Test
//    void testPagoConApiTokenIncorrecto() {
//        Cliente clienteConTokenIncorrecto = new Cliente(1L, "juan", "password", "Juan", "Garcia", "Garcia", "12345678A", "token-incorrecto");
//
//        when(clientService.findByUsername("juan")).thenReturn(Optional.of(clienteConTokenIncorrecto));
//
//        PagoTarjetaResponse response = pagoTarjetaService.procesarPago(validRequest);
//
//        assertFalse(response.exito());
//        assertEquals("Autorización no válida", response.mensaje());
//    }
//
//    @Test
//    void testPagoConAutorizacionValida() {
//        when(clientService.findByUsername("juan")).thenReturn(Optional.of(validCliente));
//        when(creditCardService.findByCardNumber("4111111111111111")).thenReturn(Optional.of(validTarjeta));
//        when(bankAccountService.findByIBAN(any())).thenReturn(Optional.of(validCuenta));
//
//        PagoTarjetaResponse response = pagoTarjetaService.procesarPago(validRequest);
//
//        assertTrue(response.exito());
//    }
//
//    // Tests para validación de tarjeta
//    @Test
//    void testPagoConTarjetaNoEncontrada() {
//        when(clientService.findByUsername("juan")).thenReturn(Optional.of(validCliente));
//        when(creditCardService.findByCardNumber("4111111111111111")).thenReturn(Optional.empty());
//
//        PagoTarjetaResponse response = pagoTarjetaService.procesarPago(validRequest);
//
//        assertFalse(response.exito());
//        assertEquals("Tarjeta no encontrada", response.mensaje());
//    }
//
//    @Test
//    void testPagoConNumeroDeTarjetaIncorrecto() {
//        when(clientService.findByUsername("juan")).thenReturn(Optional.of(validCliente));
//        when(creditCardService.findByCardNumber(any())).thenReturn(Optional.of(validTarjeta));
//
//        PagoTarjetaRequest request = new PagoTarjetaRequest(
//                new PagoTarjetaRequest.Autorizacion("juan", "5f5ca67f-4c02-47cf-8753-a7790f7f5be1"),
//                new PagoTarjetaRequest.Origen("5555555555555555", "2027-12", "123", "JUAN GARCIA GARCIA"),
//                new PagoTarjetaRequest.Destino("ES61 1234 3456 4204 5632 5555"),
//                new PagoTarjetaRequest.Pago(new BigDecimal("567.67"), "Comprar PC")
//        );
//
//        PagoTarjetaResponse response = pagoTarjetaService.procesarPago(request);
//
//        assertFalse(response.exito());
//        assertEquals("Datos de tarjeta no coinciden", response.mensaje());
//    }
//
//    @Test
//    void testPagoConFechaCaducidadIncorrecta() {
//        when(clientService.findByUsername("juan")).thenReturn(Optional.of(validCliente));
//        when(creditCardService.findByCardNumber("4111111111111111")).thenReturn(Optional.of(validTarjeta));
//
//        PagoTarjetaRequest request = new PagoTarjetaRequest(
//                new PagoTarjetaRequest.Autorizacion("juan", "5f5ca67f-4c02-47cf-8753-a7790f7f5be1"),
//                new PagoTarjetaRequest.Origen("4111111111111111", "2025-01", "123", "JUAN GARCIA GARCIA"),
//                new PagoTarjetaRequest.Destino("ES61 1234 3456 4204 5632 5555"),
//                new PagoTarjetaRequest.Pago(new BigDecimal("567.67"), "Comprar PC")
//        );
//
//        PagoTarjetaResponse response = pagoTarjetaService.procesarPago(request);
//
//        assertFalse(response.exito());
//        assertEquals("Datos de tarjeta no coinciden", response.mensaje());
//    }
//
//    @Test
//    void testPagoConCvcIncorrecto() {
//        when(clientService.findByUsername("juan")).thenReturn(Optional.of(validCliente));
//        when(creditCardService.findByCardNumber("4111111111111111")).thenReturn(Optional.of(validTarjeta));
//
//        PagoTarjetaRequest request = new PagoTarjetaRequest(
//                new PagoTarjetaRequest.Autorizacion("juan", "5f5ca67f-4c02-47cf-8753-a7790f7f5be1"),
//                new PagoTarjetaRequest.Origen("4111111111111111", "2027-12", "456", "JUAN GARCIA GARCIA"),
//                new PagoTarjetaRequest.Destino("ES61 1234 3456 4204 5632 5555"),
//                new PagoTarjetaRequest.Pago(new BigDecimal("567.67"), "Comprar PC")
//        );
//
//        PagoTarjetaResponse response = pagoTarjetaService.procesarPago(request);
//
//        assertFalse(response.exito());
//        assertEquals("Datos de tarjeta no coinciden", response.mensaje());
//    }
//
//    @Test
//    void testPagoConNombreCompleoIncorrecto() {
//        when(clientService.findByUsername("juan")).thenReturn(Optional.of(validCliente));
//        when(creditCardService.findByCardNumber("4111111111111111")).thenReturn(Optional.of(validTarjeta));
//
//        PagoTarjetaRequest request = new PagoTarjetaRequest(
//                new PagoTarjetaRequest.Autorizacion("juan", "5f5ca67f-4c02-47cf-8753-a7790f7f5be1"),
//                new PagoTarjetaRequest.Origen("4111111111111111", "2027-12", "123", "OTRO NOMBRE"),
//                new PagoTarjetaRequest.Destino("ES61 1234 3456 4204 5632 5555"),
//                new PagoTarjetaRequest.Pago(new BigDecimal("567.67"), "Comprar PC")
//        );
//
//        PagoTarjetaResponse response = pagoTarjetaService.procesarPago(request);
//
//        assertFalse(response.exito());
//        assertEquals("Datos de tarjeta no coinciden", response.mensaje());
//    }
//
//    // Tests para fondos suficientes
//    @Test
//    void testPagoConFondosInsuficientes() {
//        CuentaBancaria cuentaConSaldoBajo = new CuentaBancaria(1L, "ES6112343456420456325555", new BigDecimal("100.00"));
//
//        when(clientService.findByUsername("juan")).thenReturn(Optional.of(validCliente));
//        when(creditCardService.findByCardNumber("4111111111111111")).thenReturn(Optional.of(validTarjeta));
//        when(bankAccountService.findByIBAN(any())).thenReturn(Optional.of(cuentaConSaldoBajo));
//
//        PagoTarjetaResponse response = pagoTarjetaService.procesarPago(validRequest);
//
//        assertFalse(response.exito());
//        assertEquals("Fondos insuficientes", response.mensaje());
//    }
//
//    @Test
//    void testPagoConFondosSuficientes() {
//        when(clientService.findByUsername("juan")).thenReturn(Optional.of(validCliente));
//        when(creditCardService.findByCardNumber("4111111111111111")).thenReturn(Optional.of(validTarjeta));
//        when(bankAccountService.findByIBAN(any())).thenReturn(Optional.of(validCuenta));
//
//        PagoTarjetaResponse response = pagoTarjetaService.procesarPago(validRequest);
//
//        assertTrue(response.exito());
//        assertEquals("Pago aceptado", response.mensaje());
//    }
//
//    @Test
//    void testPagoConFondosExactosAlImporte() {
//        CuentaBancaria cuentaConSaldoExacto = new CuentaBancaria(1L, "ES6112343456420456325555", new BigDecimal("567.67"));
//
//        when(clientService.findByUsername("juan")).thenReturn(Optional.of(validCliente));
//        when(creditCardService.findByCardNumber("4111111111111111")).thenReturn(Optional.of(validTarjeta));
//        when(bankAccountService.findByIBAN(any())).thenReturn(Optional.of(cuentaConSaldoExacto));
//
//        PagoTarjetaResponse response = pagoTarjetaService.procesarPago(validRequest);
//
//        assertTrue(response.exito());
//    }
//}
//
