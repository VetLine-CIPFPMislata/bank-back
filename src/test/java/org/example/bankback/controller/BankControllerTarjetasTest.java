package org.example.bankback.controller;

import org.example.bankback.controller.mapper.PagoTarjetaMapper;
import org.example.bankback.domain.models.BankAccount;
import org.example.bankback.domain.models.Client;
import org.example.bankback.domain.models.CreditCard;
import org.example.bankback.domain.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class BankControllerTarjetasTest {

    private MockMvc mockMvc;

    @Mock
    private PagoTarjetaService pagoTarjetaService;

    @Mock
    private BankApiTokenService bankApiTokenService;

    @Mock
    private BankAccountService bankAccountService;

    @Mock
    private CreditCardService creditCardService;

    @Mock
    private BankMovementService bankMovementService;

    @Mock
    private AuthService authService;

    private BankController bankController;
    private Client validClient;
    private BankAccount cuentaDelCliente;
    private List<CreditCard> tarjetas;

    @BeforeEach
    void setUp() {
        PagoTarjetaMapper mapper = new PagoTarjetaMapper();
        bankController = new BankController(
                pagoTarjetaService,
                mapper,
                bankAccountService,
                creditCardService,
                bankMovementService,
                bankApiTokenService,
                authService
        );

        mockMvc = MockMvcBuilders.standaloneSetup(bankController).build();

        // Cliente válido
        validClient = new Client(1L, "juan", "password123", "Juan", "García", "López", "12345678A");

        // Cuenta bancaria del cliente
        cuentaDelCliente = new BankAccount(1L, "ES6112343456420456325555", new BigDecimal("1000.00"));
        cuentaDelCliente.setIdCliente(1L);

        // Tarjetas de la cuenta
        CreditCard tarjeta1 = new CreditCard(1L, "4111111111111111", "2027-12", "123", "JUAN GARCIA");
        tarjeta1.setIdCuentaBancaria(1L);

        CreditCard tarjeta2 = new CreditCard(2L, "4222222222222222", "2028-06", "456", "JUAN GARCIA");
        tarjeta2.setIdCuentaBancaria(1L);

        tarjetas = Arrays.asList(tarjeta1, tarjeta2);
    }

    @Test
    void testGetTarjetasByCuenta_Exitoso() throws Exception {
        // Given
        String validToken = "valid-token-123";
        when(authService.getUserFromToken(validToken)).thenReturn(Optional.of(validClient));
        when(bankAccountService.findById(1L)).thenReturn(Optional.of(cuentaDelCliente));
        when(creditCardService.findByBankAccountId(1L)).thenReturn(tarjetas);

        // When/Then
        mockMvc.perform(get("/api/cuentas/1/tarjetas")
                        .header("Authorization", "Bearer " + validToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].numeroTarjeta").value("4111111111111111"))
                .andExpect(jsonPath("$[0].fechaCaducidad").value("2027-12"))
                .andExpect(jsonPath("$[1].numeroTarjeta").value("4222222222222222"))
                .andExpect(jsonPath("$[1].fechaCaducidad").value("2028-06"));
    }

    @Test
    void testGetTarjetasByCuenta_SinAuthorizationHeader() throws Exception {
        // When/Then
        mockMvc.perform(get("/api/cuentas/1/tarjetas"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testGetTarjetasByCuenta_TokenInvalido() throws Exception {
        // Given
        String invalidToken = "invalid-token";
        when(authService.getUserFromToken(invalidToken)).thenReturn(Optional.empty());

        // When/Then
        mockMvc.perform(get("/api/cuentas/1/tarjetas")
                        .header("Authorization", "Bearer " + invalidToken))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testGetTarjetasByCuenta_CuentaNoExiste() throws Exception {
        // Given
        String validToken = "valid-token-123";
        when(authService.getUserFromToken(validToken)).thenReturn(Optional.of(validClient));
        when(bankAccountService.findById(999L)).thenReturn(Optional.empty());

        // When/Then
        mockMvc.perform(get("/api/cuentas/999/tarjetas")
                        .header("Authorization", "Bearer " + validToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetTarjetasByCuenta_CuentaNoPertenecAlUsuario() throws Exception {
        // Given - Cuenta pertenece a otro cliente
        String validToken = "valid-token-123";
        BankAccount cuentaOtroCliente = new BankAccount(2L, "ES6112343456420456325556", new BigDecimal("2000.00"));
        cuentaOtroCliente.setIdCliente(2L); // Cliente diferente

        when(authService.getUserFromToken(validToken)).thenReturn(Optional.of(validClient));
        when(bankAccountService.findById(2L)).thenReturn(Optional.of(cuentaOtroCliente));

        // When/Then
        mockMvc.perform(get("/api/cuentas/2/tarjetas")
                        .header("Authorization", "Bearer " + validToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void testGetTarjetasByCuenta_CuentaSinTarjetas() throws Exception {
        // Given
        String validToken = "valid-token-123";
        when(authService.getUserFromToken(validToken)).thenReturn(Optional.of(validClient));
        when(bankAccountService.findById(1L)).thenReturn(Optional.of(cuentaDelCliente));
        when(creditCardService.findByBankAccountId(1L)).thenReturn(List.of());

        // When/Then
        mockMvc.perform(get("/api/cuentas/1/tarjetas")
                        .header("Authorization", "Bearer " + validToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void testGetTarjetasByCuenta_ConMultiplesTarjetas() throws Exception {
        // Given
        String validToken = "valid-token-123";
        CreditCard tarjeta3 = new CreditCard(3L, "4333333333333333", "2029-03", "789", "JUAN GARCIA");
        tarjeta3.setIdCuentaBancaria(1L);

        List<CreditCard> muchasTarjetas = Arrays.asList(
                tarjetas.get(0),
                tarjetas.get(1),
                tarjeta3
        );

        when(authService.getUserFromToken(validToken)).thenReturn(Optional.of(validClient));
        when(bankAccountService.findById(1L)).thenReturn(Optional.of(cuentaDelCliente));
        when(creditCardService.findByBankAccountId(1L)).thenReturn(muchasTarjetas);

        // When/Then
        mockMvc.perform(get("/api/cuentas/1/tarjetas")
                        .header("Authorization", "Bearer " + validToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(3));
    }

    @Test
    void testGetTarjetasByCuenta_AuthorizationHeaderVacio() throws Exception {
        // When/Then
        mockMvc.perform(get("/api/cuentas/1/tarjetas")
                        .header("Authorization", ""))
                .andExpect(status().isUnauthorized());
    }
}

