package org.example.bankback.controller;

import org.example.bankback.controller.mapper.PagoTarjetaMapper;
import org.example.bankback.domain.models.BankAccount;
import org.example.bankback.domain.models.BankMovement;
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
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.example.bankback.domain.models.OriginBankMovement.TARJETA;
import static org.example.bankback.domain.models.OriginBankMovement.TRANSFERENCIA;
import static org.example.bankback.domain.models.TypeBankMovement.DEBE;
import static org.example.bankback.domain.models.TypeBankMovement.HABER;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class BankControllerMovimientosTest {

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
    private CreditCard tarjeta;
    private List<BankMovement> movimientos;

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

        
        validClient = new Client(1L, "juan", "password123", "Juan", "García", "López", "12345678A");

        
        cuentaDelCliente = new BankAccount(1L, "ES6112343456420456325555", new BigDecimal("1000.00"));
        cuentaDelCliente.setIdCliente(1L);

        
        tarjeta = new CreditCard(1L, "4111111111111111", "2027-12", "123", "JUAN GARCIA");
        tarjeta.setIdCuentaBancaria(1L);

        
        BankMovement movimiento1 = new BankMovement(
                1L,
                DEBE,
                TARJETA,
                tarjeta,
                cuentaDelCliente,
                new Date(),
                new BigDecimal("50.00"),
                "Compra en tienda"
        );

        BankMovement movimiento2 = new BankMovement(
                2L,
                HABER,
                TRANSFERENCIA,
                null,
                cuentaDelCliente,
                new Date(),
                new BigDecimal("200.00"),
                "Ingreso nómina"
        );

        BankMovement movimiento3 = new BankMovement(
                3L,
                DEBE,
                TARJETA,
                tarjeta,
                cuentaDelCliente,
                new Date(),
                new BigDecimal("30.50"),
                "Compra online"
        );

        movimientos = Arrays.asList(movimiento1, movimiento2, movimiento3);
    }

    @Test
    void testGetMovimientosByCuenta_Exitoso() throws Exception {
        
        String validToken = "valid-token-123";
        when(authService.getUserFromToken(validToken)).thenReturn(Optional.of(validClient));
        when(bankAccountService.findById(1L)).thenReturn(Optional.of(cuentaDelCliente));
        when(bankMovementService.findAllByBankAccountId(1L)).thenReturn(movimientos);

        
        mockMvc.perform(get("/api/cuentas/1/movimientos")
                        .header("Authorization", "Bearer " + validToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0].tipoMovimientoBancario").value("DEBE"))
                .andExpect(jsonPath("$[0].origenMovimientoBancario").value("TARJETA"))
                .andExpect(jsonPath("$[0].importe").value(50.00))
                .andExpect(jsonPath("$[0].concepto").value("Compra en tienda"))
                .andExpect(jsonPath("$[1].tipoMovimientoBancario").value("HABER"))
                .andExpect(jsonPath("$[1].origenMovimientoBancario").value("TRANSFERENCIA"))
                .andExpect(jsonPath("$[1].importe").value(200.00))
                .andExpect(jsonPath("$[2].importe").value(30.50));
    }

    @Test
    void testGetMovimientosByCuenta_SinAuthorizationHeader() throws Exception {
        
        mockMvc.perform(get("/api/cuentas/1/movimientos"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testGetMovimientosByCuenta_TokenInvalido() throws Exception {
        
        String invalidToken = "invalid-token";
        when(authService.getUserFromToken(invalidToken)).thenReturn(Optional.empty());

        
        mockMvc.perform(get("/api/cuentas/1/movimientos")
                        .header("Authorization", "Bearer " + invalidToken))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testGetMovimientosByCuenta_CuentaNoExiste() throws Exception {
        
        String validToken = "valid-token-123";
        when(authService.getUserFromToken(validToken)).thenReturn(Optional.of(validClient));
        when(bankAccountService.findById(999L)).thenReturn(Optional.empty());

        
        mockMvc.perform(get("/api/cuentas/999/movimientos")
                        .header("Authorization", "Bearer " + validToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetMovimientosByCuenta_CuentaNoPertenecAlUsuario() throws Exception {
        
        String validToken = "valid-token-123";
        BankAccount cuentaOtroCliente = new BankAccount(2L, "ES6112343456420456325556", new BigDecimal("2000.00"));
        cuentaOtroCliente.setIdCliente(2L); 

        when(authService.getUserFromToken(validToken)).thenReturn(Optional.of(validClient));
        when(bankAccountService.findById(2L)).thenReturn(Optional.of(cuentaOtroCliente));

        
        mockMvc.perform(get("/api/cuentas/2/movimientos")
                        .header("Authorization", "Bearer " + validToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void testGetMovimientosByCuenta_CuentaSinMovimientos() throws Exception {
        
        String validToken = "valid-token-123";
        when(authService.getUserFromToken(validToken)).thenReturn(Optional.of(validClient));
        when(bankAccountService.findById(1L)).thenReturn(Optional.of(cuentaDelCliente));
        when(bankMovementService.findAllByBankAccountId(1L)).thenReturn(List.of());

        
        mockMvc.perform(get("/api/cuentas/1/movimientos")
                        .header("Authorization", "Bearer " + validToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void testGetMovimientosByCuenta_ConMuchosMovimientos() throws Exception {
        
        String validToken = "valid-token-123";
        BankMovement movimiento4 = new BankMovement(
                4L,
                DEBE,
                TRANSFERENCIA,
                null,
                cuentaDelCliente,
                new Date(),
                new BigDecimal("100.00"),
                "Pago recibo luz"
        );

        BankMovement movimiento5 = new BankMovement(
                5L,
                HABER,
                TRANSFERENCIA,
                null,
                cuentaDelCliente,
                new Date(),
                new BigDecimal("500.00"),
                "Devolución"
        );

        List<BankMovement> muchosMovimientos = Arrays.asList(
                movimientos.get(0),
                movimientos.get(1),
                movimientos.get(2),
                movimiento4,
                movimiento5
        );

        when(authService.getUserFromToken(validToken)).thenReturn(Optional.of(validClient));
        when(bankAccountService.findById(1L)).thenReturn(Optional.of(cuentaDelCliente));
        when(bankMovementService.findAllByBankAccountId(1L)).thenReturn(muchosMovimientos);

        
        mockMvc.perform(get("/api/cuentas/1/movimientos")
                        .header("Authorization", "Bearer " + validToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(5));
    }

    @Test
    void testGetMovimientosByCuenta_AuthorizationHeaderSinBearer() throws Exception {
        
        mockMvc.perform(get("/api/cuentas/1/movimientos")
                        .header("Authorization", "invalid-format"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testGetMovimientosByCuenta_SoloMovimientosDebe() throws Exception {
        
        String validToken = "valid-token-123";
        List<BankMovement> soloDebes = Arrays.asList(movimientos.get(0), movimientos.get(2));

        when(authService.getUserFromToken(validToken)).thenReturn(Optional.of(validClient));
        when(bankAccountService.findById(1L)).thenReturn(Optional.of(cuentaDelCliente));
        when(bankMovementService.findAllByBankAccountId(1L)).thenReturn(soloDebes);

        
        mockMvc.perform(get("/api/cuentas/1/movimientos")
                        .header("Authorization", "Bearer " + validToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].tipoMovimientoBancario").value("DEBE"))
                .andExpect(jsonPath("$[1].tipoMovimientoBancario").value("DEBE"));
    }
}
