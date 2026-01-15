package org.example.bankback.controller;

import org.example.bankback.controller.mapper.PagoTarjetaMapper;
import org.example.bankback.domain.models.BankAccount;
import org.example.bankback.domain.models.Client;
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
class BankControllerCuentasTest {

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
    private List<BankAccount> cuentas;

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

        
        validClient = new Client(1L, "juan", "password123", "Juan", "GarcÃ­a", "LÃ³pez", "12345678A");

        
        BankAccount cuenta1 = new BankAccount(1L, "ES6112343456420456325555", new BigDecimal("1000.00"));
        cuenta1.setIdCliente(1L);

        BankAccount cuenta2 = new BankAccount(2L, "ES6112343456420456325556", new BigDecimal("500.00"));
        cuenta2.setIdCliente(1L);

        cuentas = Arrays.asList(cuenta1, cuenta2);
    }

    @Test
    void testGetCuentasByCliente_Exitoso() throws Exception {
        
        String validToken = "valid-token-123";
        when(authService.getUserFromToken(validToken)).thenReturn(Optional.of(validClient));
        when(bankAccountService.findByClientId(1L)).thenReturn(cuentas);

        
        mockMvc.perform(get("/api/clientes/1/cuentas")
                        .header("Authorization", "Bearer " + validToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].iban").value("ES6112343456420456325555"))
                .andExpect(jsonPath("$[0].saldo").value(1000.00))
                .andExpect(jsonPath("$[1].iban").value("ES6112343456420456325556"))
                .andExpect(jsonPath("$[1].saldo").value(500.00));
    }

    @Test
    void testGetCuentasByCliente_SinAuthorizationHeader() throws Exception {
        
        mockMvc.perform(get("/api/clientes/1/cuentas"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testGetCuentasByCliente_AuthorizationHeaderSinBearer() throws Exception {
        
        mockMvc.perform(get("/api/clientes/1/cuentas")
                        .header("Authorization", "invalid-format"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testGetCuentasByCliente_TokenInvalido() throws Exception {
        
        String invalidToken = "invalid-token";
        when(authService.getUserFromToken(invalidToken)).thenReturn(Optional.empty());

        
        mockMvc.perform(get("/api/clientes/1/cuentas")
                        .header("Authorization", "Bearer " + invalidToken))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testGetCuentasByCliente_UsuarioIntentaAccederACuentasDeOtroCliente() throws Exception {
        
        String validToken = "valid-token-123";
        when(authService.getUserFromToken(validToken)).thenReturn(Optional.of(validClient));

        
        mockMvc.perform(get("/api/clientes/2/cuentas")
                        .header("Authorization", "Bearer " + validToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void testGetCuentasByCliente_ClienteSinCuentas() throws Exception {
        
        String validToken = "valid-token-123";
        when(authService.getUserFromToken(validToken)).thenReturn(Optional.of(validClient));
        when(bankAccountService.findByClientId(1L)).thenReturn(List.of());

        
        mockMvc.perform(get("/api/clientes/1/cuentas")
                        .header("Authorization", "Bearer " + validToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void testGetCuentasByCliente_ConMultiplesCuentas() throws Exception {
        
        String validToken = "valid-token-123";
        BankAccount cuenta3 = new BankAccount(3L, "ES6112343456420456325557", new BigDecimal("2500.00"));
        cuenta3.setIdCliente(1L);

        List<BankAccount> muchasCuentas = Arrays.asList(
                cuentas.get(0),
                cuentas.get(1),
                cuenta3
        );

        when(authService.getUserFromToken(validToken)).thenReturn(Optional.of(validClient));
        when(bankAccountService.findByClientId(1L)).thenReturn(muchasCuentas);

        
        mockMvc.perform(get("/api/clientes/1/cuentas")
                        .header("Authorization", "Bearer " + validToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(3));
    }
}


