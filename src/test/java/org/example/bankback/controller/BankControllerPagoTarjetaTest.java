package org.example.bankback.controller;

import org.example.bankback.controller.mapper.PagoTarjetaMapper;
import org.example.bankback.controller.webmodel.request.AutorizacionRequest;
import org.example.bankback.controller.webmodel.request.DestinoRequest;
import org.example.bankback.controller.webmodel.request.OrigenRequest;
import org.example.bankback.controller.webmodel.request.PagoDetailsRequest;
import org.example.bankback.controller.webmodel.request.PagoTarjetaRequest;
import org.example.bankback.domain.exception.ValidationException;
import org.example.bankback.domain.models.dto.PagoTarjetaResponseDTO;
import org.example.bankback.domain.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class BankControllerPagoTarjetaTest {

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

    private PagoTarjetaRequest validRequest;
    private PagoTarjetaResponseDTO successResponseDTO;

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

        mockMvc = MockMvcBuilders.standaloneSetup(bankController)
                .setControllerAdvice(new TestExceptionHandler())
                .build();

        validRequest = new PagoTarjetaRequest(
                new AutorizacionRequest("juan", "5f5ca67f-4c02-47cf-8753-a7790f7f5be1"),
                new OrigenRequest("4111111111111111", "2027-12", "123", "JUAN GARCIA GARCIA"),
                new DestinoRequest("ES61 1234 3456 4204 5632 5555"),
                new PagoDetailsRequest(new BigDecimal("567.67"), "Comprar PC")
        );

        successResponseDTO = new PagoTarjetaResponseDTO(
                "ES6112343456420456325555",
                new BigDecimal("567.67"),
                "Comprar PC",
                "Pago aceptado",
                true
        );
    }

    @Test
    void testPagoTarjetaConExito() throws Exception {
        
        when(bankApiTokenService.validateApiToken(anyString())).thenReturn(true);
        when(pagoTarjetaService.procesarPago(any())).thenReturn(successResponseDTO);

        
        mockMvc.perform(post("/api/pago_tarjeta")
                        .contentType("application/json")
                        .content(toJson(validRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exito").value(true))
                .andExpect(jsonPath("$.mensaje").value("Pago aceptado"))
                .andExpect(jsonPath("$.importe").value(567.67))
                .andExpect(jsonPath("$.concepto").value("Comprar PC"))
                .andExpect(jsonPath("$.ibanDestino").value("ES6112343456420456325555"));
    }

    @Test
    void testPagoTarjetaConApiTokenInvalido() throws Exception {
        
        when(bankApiTokenService.validateApiToken(anyString())).thenReturn(false);

        
        mockMvc.perform(post("/api/pago_tarjeta")
                        .contentType("application/json")
                        .content(toJson(validRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testPagoTarjetaConIbanInvalido() throws Exception {
        
        PagoTarjetaRequest invalidRequest = new PagoTarjetaRequest(
                new AutorizacionRequest("juan", "5f5ca67f-4c02-47cf-8753-a7790f7f5be1"),
                new OrigenRequest("4111111111111111", "2027-12", "123", "JUAN GARCIA GARCIA"),
                new DestinoRequest("FR61 1234 3456 4204 5632 5555"),
                new PagoDetailsRequest(new BigDecimal("567.67"), "Comprar PC")
        );

        when(bankApiTokenService.validateApiToken(anyString())).thenReturn(true);
        when(pagoTarjetaService.procesarPago(any()))
                .thenThrow(new ValidationException("IBAN invÃ¡lido o no empieza por ES"));

        
        mockMvc.perform(post("/api/pago_tarjeta")
                        .contentType("application/json")
                        .content(toJson(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testPagoTarjetaConImporteNegativo() throws Exception {
        
        PagoTarjetaRequest invalidRequest = new PagoTarjetaRequest(
                new AutorizacionRequest("juan", "5f5ca67f-4c02-47cf-8753-a7790f7f5be1"),
                new OrigenRequest("4111111111111111", "2027-12", "123", "JUAN GARCIA GARCIA"),
                new DestinoRequest("ES61 1234 3456 4204 5632 5555"),
                new PagoDetailsRequest(new BigDecimal("-100.00"), "Comprar PC")
        );

        when(bankApiTokenService.validateApiToken(anyString())).thenReturn(true);
        when(pagoTarjetaService.procesarPago(any()))
                .thenThrow(new ValidationException("Importe debe ser positivo"));

        
        mockMvc.perform(post("/api/pago_tarjeta")
                        .contentType("application/json")
                        .content(toJson(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testPagoTarjetaConConceptoCorto() throws Exception {
        
        PagoTarjetaRequest invalidRequest = new PagoTarjetaRequest(
                new AutorizacionRequest("juan", "5f5ca67f-4c02-47cf-8753-a7790f7f5be1"),
                new OrigenRequest("4111111111111111", "2027-12", "123", "JUAN GARCIA GARCIA"),
                new DestinoRequest("ES61 1234 3456 4204 5632 5555"),
                new PagoDetailsRequest(new BigDecimal("567.67"), "AB")
        );

        when(bankApiTokenService.validateApiToken(anyString())).thenReturn(true);
        when(pagoTarjetaService.procesarPago(any()))
                .thenThrow(new ValidationException("Concepto debe tener al menos 3 caracteres"));

        
        mockMvc.perform(post("/api/pago_tarjeta")
                        .contentType("application/json")
                        .content(toJson(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testPagoTarjetaConAutorizacionInvalida() throws Exception {
        
        when(bankApiTokenService.validateApiToken(anyString())).thenReturn(true);
        when(pagoTarjetaService.procesarPago(any()))
                .thenThrow(new ValidationException("AutorizaciÃ³n no vÃ¡lida"));

        
        mockMvc.perform(post("/api/pago_tarjeta")
                        .contentType("application/json")
                        .content(toJson(validRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testPagoTarjetaConDatosIncoherentes() throws Exception {
        
        when(bankApiTokenService.validateApiToken(anyString())).thenReturn(true);
        when(pagoTarjetaService.procesarPago(any()))
                .thenThrow(new ValidationException("Datos de la tarjeta no coinciden"));

        
        mockMvc.perform(post("/api/pago_tarjeta")
                        .contentType("application/json")
                        .content(toJson(validRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testPagoTarjetaConFondosInsuficientes() throws Exception {
        
        when(bankApiTokenService.validateApiToken(anyString())).thenReturn(true);
        when(pagoTarjetaService.procesarPago(any()))
                .thenThrow(new ValidationException("Fondos insuficientes"));

        
        mockMvc.perform(post("/api/pago_tarjeta")
                        .contentType("application/json")
                        .content(toJson(validRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testPagoTarjetaConTarjetaCaducada() throws Exception {
        
        when(bankApiTokenService.validateApiToken(anyString())).thenReturn(true);
        when(pagoTarjetaService.procesarPago(any()))
                .thenThrow(new ValidationException("Tarjeta caducada"));

        
        mockMvc.perform(post("/api/pago_tarjeta")
                        .contentType("application/json")
                        .content(toJson(validRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testPagoTarjetaConTarjetaNoEncontrada() throws Exception {
        
        when(bankApiTokenService.validateApiToken(anyString())).thenReturn(true);
        when(pagoTarjetaService.procesarPago(any()))
                .thenThrow(new ValidationException("Tarjeta no encontrada"));

        
        mockMvc.perform(post("/api/pago_tarjeta")
                        .contentType("application/json")
                        .content(toJson(validRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testPagoTarjetaConCuentaDestinoNoEncontrada() throws Exception {
        
        when(bankApiTokenService.validateApiToken(anyString())).thenReturn(true);
        when(pagoTarjetaService.procesarPago(any()))
                .thenThrow(new ValidationException("Cuenta destino no encontrada"));

        
        mockMvc.perform(post("/api/pago_tarjeta")
                        .contentType("application/json")
                        .content(toJson(validRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testPagoTarjetaConPeticionIncompleta() throws Exception {
        
        when(bankApiTokenService.validateApiToken(anyString())).thenReturn(true);
        when(pagoTarjetaService.procesarPago(any()))
                .thenThrow(new ValidationException("PeticiÃ³n incompleta"));

        
        mockMvc.perform(post("/api/pago_tarjeta")
                        .contentType("application/json")
                        .content(toJson(validRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testPagoTarjetaConCuentaNoPerteneceTienda() throws Exception {
        
        when(bankApiTokenService.validateApiToken(anyString())).thenReturn(true);
        when(pagoTarjetaService.procesarPago(any()))
                .thenThrow(new ValidationException("Cuenta destino no pertenece a la tienda"));

        
        mockMvc.perform(post("/api/pago_tarjeta")
                        .contentType("application/json")
                        .content(toJson(validRequest)))
                .andExpect(status().isBadRequest());
    }

    private String toJson(PagoTarjetaRequest r) {
        return """
                {
                  "autorizacion": {"login":"%s","api_token":"%s"},
                  "origen": {"numeroTarjeta":"%s","fechaCaducidad":"%s","cvc":"%s","nombreCompleto":"%s"},
                  "destino": {"iban":"%s"},
                  "pago": {"importe": %s, "concepto": "%s"}
                }
                """.formatted(
                r.autorizacion().login(),
                r.autorizacion().api_token(),
                r.origen().numeroTarjeta(),
                r.origen().fechaCaducidad(),
                r.origen().cvc(),
                r.origen().nombreCompleto(),
                r.destino().iban(),
                r.pago().importe().toPlainString(),
                r.pago().concepto()
        );
    }

    @org.springframework.web.bind.annotation.RestControllerAdvice
    static class TestExceptionHandler {
        @org.springframework.web.bind.annotation.ExceptionHandler(ValidationException.class)
        org.springframework.http.ResponseEntity<java.util.Map<String, Object>> handleValidationException(ValidationException ex) {
            java.util.Map<String, Object> response = new java.util.HashMap<>();
            response.put("error", ex.getMessage());
            response.put("status", 400);

            return org.springframework.http.ResponseEntity.status(org.springframework.http.HttpStatus.BAD_REQUEST).body(response);
        }
    }
}


