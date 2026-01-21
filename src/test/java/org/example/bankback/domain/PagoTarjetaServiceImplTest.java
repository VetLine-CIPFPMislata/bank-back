package org.example.bankback.domain;

import org.example.bankback.domain.exception.ValidationException;
import org.example.bankback.domain.models.BankAccount;
import org.example.bankback.domain.models.BankMovement;
import org.example.bankback.domain.models.Client;
import org.example.bankback.domain.models.CreditCard;
import org.example.bankback.domain.models.dto.PagoTarjetaDTO;
import org.example.bankback.domain.models.dto.PagoTarjetaResponseDTO;
import org.example.bankback.domain.service.BankAccountService;
import org.example.bankback.domain.service.BankMovementService;
import org.example.bankback.domain.service.ClientService;
import org.example.bankback.domain.service.CreditCardService;
import org.example.bankback.domain.service.impl.PagoTarjetaServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PagoTarjetaServiceImplTest {

    @Mock
    private ClientService clientService;

    @Mock
    private CreditCardService creditCardService;

    @Mock
    private BankAccountService bankAccountService;

    @Mock
    private BankMovementService bankMovementService;

    @InjectMocks
    private PagoTarjetaServiceImpl pagoTarjetaService;

    private PagoTarjetaDTO validRequest;
    private Client validClient;
    private CreditCard validCreditCard;
    private BankAccount validCuentaOrigen;
    private BankAccount validCuentaDestino;

    @BeforeEach
    void setUp() {
        validClient = new Client(1L, "juan", "password", "Juan", "Garcia", "Garcia", "12345678A");


        validCreditCard = new CreditCard(1L, "4111111111111111", "2027-12", "123", "JUAN GARCIA GARCIA");
        validCreditCard.setIdCuentaBancaria(2L);
        
        validCuentaOrigen = new BankAccount(2L, "ES9876543210987654321098", new BigDecimal("1000.00"));
        validCuentaOrigen.setIdCliente(2L);


        validCuentaDestino = new BankAccount(3L, "ES6112343456420456325555", new BigDecimal("500.00"));
        validCuentaDestino.setIdCliente(1L);


        validRequest = new PagoTarjetaDTO(
                new PagoTarjetaDTO.AutorizacionDTO("juan", "BANK_SECRET_TOKEN_2024"),
                new PagoTarjetaDTO.OrigenDTO("4111111111111111", "2027-12", "123", "JUAN GARCIA GARCIA"),
                new PagoTarjetaDTO.DestinoDTO("ES6112343456420456325555"),
                new PagoTarjetaDTO.PagoDTO(new BigDecimal("567.67"), "Comprar PC")
        );
    }


    @Test
    void testPagoConIbanQueNoEmpiezaPorES_DebeLanzarExcepcion() {
        
        PagoTarjetaDTO request = new PagoTarjetaDTO(
                validRequest.autorizacion(),
                validRequest.origen(),
                new PagoTarjetaDTO.DestinoDTO("FR6112343456420456325555"),
                validRequest.pago()
        );

        
        ValidationException exception = assertThrows(ValidationException.class,
                () -> pagoTarjetaService.procesarPago(request));
        assertEquals("IBAN inválido o no empieza por ES", exception.getMessage());
    }

    @Test
    void testPagoConIbanFormatoInvalido_DebeLanzarExcepcion() {
        
        PagoTarjetaDTO request = new PagoTarjetaDTO(
                validRequest.autorizacion(),
                validRequest.origen(),
                new PagoTarjetaDTO.DestinoDTO("ES123"),
                validRequest.pago()
        );

        
        ValidationException exception = assertThrows(ValidationException.class,
                () -> pagoTarjetaService.procesarPago(request));
        assertEquals("IBAN inválido o no empieza por ES", exception.getMessage());
    }

    @Test
    void testPagoConIbanConEspacios_DebeNormalizarYProcesar() {
        
        PagoTarjetaDTO request = new PagoTarjetaDTO(
                validRequest.autorizacion(),
                validRequest.origen(),
                new PagoTarjetaDTO.DestinoDTO("ES61 1234 3456 4204 5632 5555"),
                validRequest.pago()
        );

        when(clientService.findByUsername("juan")).thenReturn(Optional.of(validClient));
        when(bankAccountService.findByIBAN("ES6112343456420456325555")).thenReturn(Optional.of(validCuentaDestino));
        when(creditCardService.findByCardNumber("4111111111111111")).thenReturn(Optional.of(validCreditCard));
        when(bankAccountService.findById(2L)).thenReturn(Optional.of(validCuentaOrigen));
        when(bankAccountService.save(any(BankAccount.class))).thenAnswer(i -> i.getArgument(0));
        when(bankMovementService.save(any(BankMovement.class))).thenAnswer(i -> i.getArgument(0));

        
        PagoTarjetaResponseDTO response = pagoTarjetaService.procesarPago(request);

        
        assertTrue(response.exito());
        assertEquals("ES6112343456420456325555", response.ibanDestino());
    }

    
    @Test
    void testPagoConImporteNegativo_DebeLanzarExcepcion() {
        
        PagoTarjetaDTO request = new PagoTarjetaDTO(
                validRequest.autorizacion(),
                validRequest.origen(),
                validRequest.destino(),
                new PagoTarjetaDTO.PagoDTO(new BigDecimal("-100.00"), "Comprar PC")
        );

        
        ValidationException exception = assertThrows(ValidationException.class,
                () -> pagoTarjetaService.procesarPago(request));
        assertEquals("Importe debe ser positivo", exception.getMessage());
    }

    @Test
    void testPagoConImporteCero_DebeLanzarExcepcion() {
        
        PagoTarjetaDTO request = new PagoTarjetaDTO(
                validRequest.autorizacion(),
                validRequest.origen(),
                validRequest.destino(),
                new PagoTarjetaDTO.PagoDTO(BigDecimal.ZERO, "Comprar PC")
        );

        
        ValidationException exception = assertThrows(ValidationException.class,
                () -> pagoTarjetaService.procesarPago(request));
        assertEquals("Importe debe ser positivo", exception.getMessage());
    }


    @Test
    void testPagoConConceptoMenorA3Caracteres_DebeLanzarExcepcion() {
        
        PagoTarjetaDTO request = new PagoTarjetaDTO(
                validRequest.autorizacion(),
                validRequest.origen(),
                validRequest.destino(),
                new PagoTarjetaDTO.PagoDTO(new BigDecimal("100.00"), "AB")
        );

        
        ValidationException exception = assertThrows(ValidationException.class,
                () -> pagoTarjetaService.procesarPago(request));
        assertEquals("Concepto debe tener al menos 3 caracteres", exception.getMessage());
    }

    @Test
    void testPagoConConceptoExactamente3Caracteres_DebeProcesar() {
        
        PagoTarjetaDTO request = new PagoTarjetaDTO(
                validRequest.autorizacion(),
                validRequest.origen(),
                validRequest.destino(),
                new PagoTarjetaDTO.PagoDTO(new BigDecimal("100.00"), "ABC")
        );

        when(clientService.findByUsername("juan")).thenReturn(Optional.of(validClient));
        when(bankAccountService.findByIBAN(anyString())).thenReturn(Optional.of(validCuentaDestino));
        when(creditCardService.findByCardNumber(anyString())).thenReturn(Optional.of(validCreditCard));
        when(bankAccountService.findById(2L)).thenReturn(Optional.of(validCuentaOrigen));
        when(bankAccountService.save(any(BankAccount.class))).thenAnswer(i -> i.getArgument(0));
        when(bankMovementService.save(any(BankMovement.class))).thenAnswer(i -> i.getArgument(0));

        
        PagoTarjetaResponseDTO response = pagoTarjetaService.procesarPago(request);

        
        assertTrue(response.exito());
        assertEquals("ABC", response.concepto());
    }

    

    @Test
    void testPagoConUsuarioNoExistente_DebeLanzarExcepcion() {
        
        when(clientService.findByUsername("juan")).thenReturn(Optional.empty());

        
        ValidationException exception = assertThrows(ValidationException.class,
                () -> pagoTarjetaService.procesarPago(validRequest));
        assertEquals("Autorización no válida", exception.getMessage());
    }


    @Test
    void testPagoConCuentaDestinoNoExistente_DebeLanzarExcepcion() {
        
        when(clientService.findByUsername("juan")).thenReturn(Optional.of(validClient));
        when(bankAccountService.findByIBAN(anyString())).thenReturn(Optional.empty());

        
        ValidationException exception = assertThrows(ValidationException.class,
                () -> pagoTarjetaService.procesarPago(validRequest));
        assertEquals("Cuenta destino no encontrada", exception.getMessage());
    }

    @Test
    void testPagoConCuentaDestinoQueNoEsDelUsuario_DebeLanzarExcepcion() {
        
        BankAccount cuentaDeOtro = new BankAccount(4L, "ES6112343456420456325555", new BigDecimal("500.00"));
        cuentaDeOtro.setIdCliente(999L); 

        when(clientService.findByUsername("juan")).thenReturn(Optional.of(validClient));
        when(bankAccountService.findByIBAN(anyString())).thenReturn(Optional.of(cuentaDeOtro));

        
        ValidationException exception = assertThrows(ValidationException.class,
                () -> pagoTarjetaService.procesarPago(validRequest));
        assertEquals("Cuenta destino no pertenece a la tienda", exception.getMessage());
    }


    @Test
    void testPagoConTarjetaNoEncontrada_DebeLanzarExcepcion() {
        
        when(clientService.findByUsername("juan")).thenReturn(Optional.of(validClient));
        when(bankAccountService.findByIBAN(anyString())).thenReturn(Optional.of(validCuentaDestino));
        when(creditCardService.findByCardNumber(anyString())).thenReturn(Optional.empty());

        
        ValidationException exception = assertThrows(ValidationException.class,
                () -> pagoTarjetaService.procesarPago(validRequest));
        assertEquals("Tarjeta no encontrada", exception.getMessage());
    }

    @Test
    void testPagoConFechaCaducidadIncorrecta_DebeLanzarExcepcion() {
        
        PagoTarjetaDTO request = new PagoTarjetaDTO(
                validRequest.autorizacion(),
                new PagoTarjetaDTO.OrigenDTO("4111111111111111", "2025-01", "123", "JUAN GARCIA GARCIA"),
                validRequest.destino(),
                validRequest.pago()
        );

        when(clientService.findByUsername("juan")).thenReturn(Optional.of(validClient));
        when(bankAccountService.findByIBAN(anyString())).thenReturn(Optional.of(validCuentaDestino));
        when(creditCardService.findByCardNumber(anyString())).thenReturn(Optional.of(validCreditCard));

        
        ValidationException exception = assertThrows(ValidationException.class,
                () -> pagoTarjetaService.procesarPago(request));
        assertEquals("Datos de la tarjeta no coinciden", exception.getMessage());
    }

    @Test
    void testPagoConCvcIncorrecto_DebeLanzarExcepcion() {
        
        PagoTarjetaDTO request = new PagoTarjetaDTO(
                validRequest.autorizacion(),
                new PagoTarjetaDTO.OrigenDTO("4111111111111111", "2027-12", "999", "JUAN GARCIA GARCIA"),
                validRequest.destino(),
                validRequest.pago()
        );

        when(clientService.findByUsername("juan")).thenReturn(Optional.of(validClient));
        when(bankAccountService.findByIBAN(anyString())).thenReturn(Optional.of(validCuentaDestino));
        when(creditCardService.findByCardNumber(anyString())).thenReturn(Optional.of(validCreditCard));

        
        ValidationException exception = assertThrows(ValidationException.class,
                () -> pagoTarjetaService.procesarPago(request));
        assertEquals("Datos de la tarjeta no coinciden", exception.getMessage());
    }

    @Test
    void testPagoConNombreCompletoIncorrecto_DebeLanzarExcepcion() {
        
        PagoTarjetaDTO request = new PagoTarjetaDTO(
                validRequest.autorizacion(),
                new PagoTarjetaDTO.OrigenDTO("4111111111111111", "2027-12", "123", "OTRO NOMBRE"),
                validRequest.destino(),
                validRequest.pago()
        );

        when(clientService.findByUsername("juan")).thenReturn(Optional.of(validClient));
        when(bankAccountService.findByIBAN(anyString())).thenReturn(Optional.of(validCuentaDestino));
        when(creditCardService.findByCardNumber(anyString())).thenReturn(Optional.of(validCreditCard));

        
        ValidationException exception = assertThrows(ValidationException.class,
                () -> pagoTarjetaService.procesarPago(request));
        assertEquals("Datos de la tarjeta no coinciden", exception.getMessage());
    }

    @Test
    void testPagoConTarjetaCaducada_DebeLanzarExcepcion() {
        
        CreditCard tarjetaCaducada = new CreditCard(1L, "4111111111111111", "2020-12", "123", "JUAN GARCIA GARCIA");
        tarjetaCaducada.setIdCuentaBancaria(2L);

        PagoTarjetaDTO request = new PagoTarjetaDTO(
                validRequest.autorizacion(),
                new PagoTarjetaDTO.OrigenDTO("4111111111111111", "2020-12", "123", "JUAN GARCIA GARCIA"),
                validRequest.destino(),
                validRequest.pago()
        );

        when(clientService.findByUsername("juan")).thenReturn(Optional.of(validClient));
        when(bankAccountService.findByIBAN(anyString())).thenReturn(Optional.of(validCuentaDestino));
        when(creditCardService.findByCardNumber(anyString())).thenReturn(Optional.of(tarjetaCaducada));


        ValidationException exception = assertThrows(ValidationException.class,
                () -> pagoTarjetaService.procesarPago(request));
        assertEquals("Tarjeta caducada", exception.getMessage());
    }



    @Test
    void testPagoConFondosInsuficientes_DebeLanzarExcepcion() {
        
        BankAccount cuentaSinFondos = new BankAccount(2L, "ES9876543210987654321098", new BigDecimal("10.00"));
        cuentaSinFondos.setIdCliente(2L);

        when(clientService.findByUsername("juan")).thenReturn(Optional.of(validClient));
        when(bankAccountService.findByIBAN(anyString())).thenReturn(Optional.of(validCuentaDestino));
        when(creditCardService.findByCardNumber(anyString())).thenReturn(Optional.of(validCreditCard));
        when(bankAccountService.findById(2L)).thenReturn(Optional.of(cuentaSinFondos));

        
        ValidationException exception = assertThrows(ValidationException.class,
                () -> pagoTarjetaService.procesarPago(validRequest));
        assertEquals("Fondos insuficientes", exception.getMessage());
    }

    @Test
    void testPagoConSaldoExacto_DebeProcesar() {
        
        BigDecimal importe = new BigDecimal("1000.00");
        BankAccount cuentaSaldoExacto = new BankAccount(2L, "ES9876543210987654321098", importe);
        cuentaSaldoExacto.setIdCliente(2L);

        PagoTarjetaDTO request = new PagoTarjetaDTO(
                validRequest.autorizacion(),
                validRequest.origen(),
                validRequest.destino(),
                new PagoTarjetaDTO.PagoDTO(importe, "Comprar PC")
        );

        when(clientService.findByUsername("juan")).thenReturn(Optional.of(validClient));
        when(bankAccountService.findByIBAN(anyString())).thenReturn(Optional.of(validCuentaDestino));
        when(creditCardService.findByCardNumber(anyString())).thenReturn(Optional.of(validCreditCard));
        when(bankAccountService.findById(2L)).thenReturn(Optional.of(cuentaSaldoExacto));
        when(bankAccountService.save(any(BankAccount.class))).thenAnswer(i -> i.getArgument(0));
        when(bankMovementService.save(any(BankMovement.class))).thenAnswer(i -> i.getArgument(0));

        
        PagoTarjetaResponseDTO response = pagoTarjetaService.procesarPago(request);

        
        assertTrue(response.exito());
    }


    @Test
    void testPagoExitoso_DebeActualizarSaldos() {
        
        when(clientService.findByUsername("juan")).thenReturn(Optional.of(validClient));
        when(bankAccountService.findByIBAN(anyString())).thenReturn(Optional.of(validCuentaDestino));
        when(creditCardService.findByCardNumber(anyString())).thenReturn(Optional.of(validCreditCard));
        when(bankAccountService.findById(2L)).thenReturn(Optional.of(validCuentaOrigen));
        when(bankAccountService.save(any(BankAccount.class))).thenAnswer(i -> i.getArgument(0));
        when(bankMovementService.save(any(BankMovement.class))).thenAnswer(i -> i.getArgument(0));

        
        PagoTarjetaResponseDTO response = pagoTarjetaService.procesarPago(validRequest);

        
        assertTrue(response.exito());
        assertEquals("Pago aceptado", response.mensaje());
        assertEquals("ES6112343456420456325555", response.ibanDestino());
        assertEquals(new BigDecimal("567.67"), response.importe());
        assertEquals("Comprar PC", response.concepto());

        verify(bankAccountService, times(2)).save(any(BankAccount.class));

        verify(bankMovementService, times(2)).save(any(BankMovement.class));
    }

    @Test
    void testPagoExitoso_DebeRegistrarMovimiento() {
        
        when(clientService.findByUsername("juan")).thenReturn(Optional.of(validClient));
        when(bankAccountService.findByIBAN(anyString())).thenReturn(Optional.of(validCuentaDestino));
        when(creditCardService.findByCardNumber(anyString())).thenReturn(Optional.of(validCreditCard));
        when(bankAccountService.findById(2L)).thenReturn(Optional.of(validCuentaOrigen));
        when(bankAccountService.save(any(BankAccount.class))).thenAnswer(i -> i.getArgument(0));
        when(bankMovementService.save(any(BankMovement.class))).thenAnswer(i -> i.getArgument(0));

        
        pagoTarjetaService.procesarPago(validRequest);

        
        ArgumentCaptor<BankMovement> movementCaptor = ArgumentCaptor.forClass(BankMovement.class);
        verify(bankMovementService, times(2)).save(movementCaptor.capture());

        var savedMovements = movementCaptor.getAllValues();
        assertEquals(2, savedMovements.size());


        BankMovement debeMovement = savedMovements.get(0);
        assertNotNull(debeMovement);
        assertEquals(new BigDecimal("567.67"), debeMovement.getImporte());
        assertEquals("Comprar PC", debeMovement.getConcepto());
        assertEquals(validCreditCard, debeMovement.getTarjetaCreditoOrigen());


        BankMovement haberMovement = savedMovements.get(1);
        assertNotNull(haberMovement);
        assertEquals(new BigDecimal("567.67"), haberMovement.getImporte());
        assertEquals("Comprar PC", haberMovement.getConcepto());
    }

    @Test
    void testPagoConNumeroTarjetaConEspacios_DebeNormalizarYProcesar() {
        
        PagoTarjetaDTO request = new PagoTarjetaDTO(
                validRequest.autorizacion(),
                new PagoTarjetaDTO.OrigenDTO("4111 1111 1111 1111", "2027-12", "123", "JUAN GARCIA GARCIA"),
                validRequest.destino(),
                validRequest.pago()
        );

        when(clientService.findByUsername("juan")).thenReturn(Optional.of(validClient));
        when(bankAccountService.findByIBAN(anyString())).thenReturn(Optional.of(validCuentaDestino));
        when(creditCardService.findByCardNumber("4111111111111111")).thenReturn(Optional.of(validCreditCard));
        when(bankAccountService.findById(2L)).thenReturn(Optional.of(validCuentaOrigen));
        when(bankAccountService.save(any(BankAccount.class))).thenAnswer(i -> i.getArgument(0));
        when(bankMovementService.save(any(BankMovement.class))).thenAnswer(i -> i.getArgument(0));

        
        PagoTarjetaResponseDTO response = pagoTarjetaService.procesarPago(request);

        
        assertTrue(response.exito());
        verify(creditCardService).findByCardNumber("4111111111111111");
    }


    @Test
    void testPagoConPeticionNull_DebeLanzarExcepcion() {
        ValidationException exception = assertThrows(ValidationException.class,
                () -> pagoTarjetaService.procesarPago(null));
        assertEquals("Petición incompleta", exception.getMessage());
    }
}

