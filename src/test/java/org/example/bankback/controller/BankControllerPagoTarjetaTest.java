//package org.example.bankback.controller;
//
//import org.example.bankback.controller.webmodel.request.PagoTarjetaRequest;
//import org.example.bankback.controller.webmodel.response.PagoTarjetaResponse;
//import org.example.bankback.domain.service.PagoTarjetaService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.http.MediaType;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.setup.MockMvcBuilders;
//
//import java.math.BigDecimal;
//
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.when;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//@ExtendWith(MockitoExtension.class)
//class BankControllerPagoTarjetaTest {
//
//    private MockMvc mockMvc;
//
//    @Mock
//    private PagoTarjetaService pagoTarjetaService;
//
//    @InjectMocks
//    private BankController bankController;
//
//    private PagoTarjetaRequest validRequest;
//    private PagoTarjetaResponse successResponse;
//    private PagoTarjetaResponse failResponse;
//
//    @BeforeEach
//    void setUp() {
//        mockMvc = MockMvcBuilders.standaloneSetup(bankController).build();
//
//        validRequest = new PagoTarjetaRequest(
//                new PagoTarjetaRequest.Autorizacion("juan", "5f5ca67f-4c02-47cf-8753-a7790f7f5be1"),
//                new PagoTarjetaRequest.Origen("4111111111111111", "2027-12", "123", "JUAN GARCIA GARCIA"),
//                new PagoTarjetaRequest.Destino("ES61 1234 3456 4204 5632 5555"),
//                new PagoTarjetaRequest.Pago(new BigDecimal("567.67"), "Comprar PC")
//        );
//
//        successResponse = new PagoTarjetaResponse(
//                "ES61 1234 3456 4204 5632 5555",
//                new BigDecimal("567.67"),
//                "Comprar PC",
//                "Pago aceptado",
//                true
//        );
//
//        failResponse = new PagoTarjetaResponse(
//                null,
//                null,
//                null,
//                "IBAN inválido o no empieza por ES",
//                false
//        );
//    }
//
//    @Test
//    void testPagoTarjetaConExit() throws Exception {
//        when(pagoTarjetaService.procesarPago(any())).thenReturn(successResponse);
//
//        mockMvc.perform(post("/api/pago_tarjeta")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(toJson(validRequest)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.exito").value(true))
//                .andExpect(jsonPath("$.mensaje").value("Pago aceptado"))
//                .andExpect(jsonPath("$.importe").value(567.67))
//                .andExpect(jsonPath("$.concepto").value("Comprar PC"))
//                .andExpect(jsonPath("$.ibanDestino").value("ES61 1234 3456 4204 5632 5555"));
//    }
//
//    @Test
//    void testPagoTarjetaConFallo() throws Exception {
//        when(pagoTarjetaService.procesarPago(any())).thenReturn(failResponse);
//
//        mockMvc.perform(post("/api/pago_tarjeta")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(toJson(validRequest)))
//                .andExpect(status().isBadRequest())
//                .andExpect(jsonPath("$.exito").value(false))
//                .andExpect(jsonPath("$.mensaje").value("IBAN inválido o no empieza por ES"));
//    }
//
//    @Test
//    void testPagoTarjetaConIbanInvalido() throws Exception {
//        PagoTarjetaRequest invalidRequest = new PagoTarjetaRequest(
//                new PagoTarjetaRequest.Autorizacion("juan", "5f5ca67f-4c02-47cf-8753-a7790f7f5be1"),
//                new PagoTarjetaRequest.Origen("4111111111111111", "2027-12", "123", "JUAN GARCIA GARCIA"),
//                new PagoTarjetaRequest.Destino("FR61 1234 3456 4204 5632 5555"),
//                new PagoTarjetaRequest.Pago(new BigDecimal("567.67"), "Comprar PC")
//        );
//
//        PagoTarjetaResponse response = new PagoTarjetaResponse(null, null, null, "IBAN inválido o no empieza por ES", false);
//        when(pagoTarjetaService.procesarPago(any())).thenReturn(response);
//
//        mockMvc.perform(post("/api/pago_tarjeta")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(toJson(invalidRequest)))
//                .andExpect(status().isBadRequest())
//                .andExpect(jsonPath("$.exito").value(false));
//    }
//
//    @Test
//    void testPagoTarjetaConImporteNegativo() throws Exception {
//        PagoTarjetaRequest invalidRequest = new PagoTarjetaRequest(
//                new PagoTarjetaRequest.Autorizacion("juan", "5f5ca67f-4c02-47cf-8753-a7790f7f5be1"),
//                new PagoTarjetaRequest.Origen("4111111111111111", "2027-12", "123", "JUAN GARCIA GARCIA"),
//                new PagoTarjetaRequest.Destino("ES61 1234 3456 4204 5632 5555"),
//                new PagoTarjetaRequest.Pago(new BigDecimal("-100.00"), "Comprar PC")
//        );
//
//        PagoTarjetaResponse response = new PagoTarjetaResponse(null, null, null, "Importe debe ser positivo", false);
//        when(pagoTarjetaService.procesarPago(any())).thenReturn(response);
//
//        mockMvc.perform(post("/api/pago_tarjeta")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(toJson(invalidRequest)))
//                .andExpect(status().isBadRequest())
//                .andExpect(jsonPath("$.exito").value(false))
//                .andExpect(jsonPath("$.mensaje").value("Importe debe ser positivo"));
//    }
//
//    @Test
//    void testPagoTarjetaConConceptoCorto() throws Exception {
//        PagoTarjetaRequest invalidRequest = new PagoTarjetaRequest(
//                new PagoTarjetaRequest.Autorizacion("juan", "5f5ca67f-4c02-47cf-8753-a7790f7f5be1"),
//                new PagoTarjetaRequest.Origen("4111111111111111", "2027-12", "123", "JUAN GARCIA GARCIA"),
//                new PagoTarjetaRequest.Destino("ES61 1234 3456 4204 5632 5555"),
//                new PagoTarjetaRequest.Pago(new BigDecimal("567.67"), "AB")
//        );
//
//        PagoTarjetaResponse response = new PagoTarjetaResponse(null, null, null, "Concepto debe tener al menos 3 caracteres", false);
//        when(pagoTarjetaService.procesarPago(any())).thenReturn(response);
//
//        mockMvc.perform(post("/api/pago_tarjeta")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(toJson(invalidRequest)))
//                .andExpect(status().isBadRequest())
//                .andExpect(jsonPath("$.exito").value(false))
//                .andExpect(jsonPath("$.mensaje").value("Concepto debe tener al menos 3 caracteres"));
//    }
//
//    @Test
//    void testPagoTarjetaConAutorizacionInvalida() throws Exception {
//        PagoTarjetaResponse response = new PagoTarjetaResponse(null, null, null, "Autorización no válida", false);
//        when(pagoTarjetaService.procesarPago(any())).thenReturn(response);
//
//        mockMvc.perform(post("/api/pago_tarjeta")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(toJson(validRequest)))
//                .andExpect(status().isBadRequest())
//                .andExpect(jsonPath("$.exito").value(false))
//                .andExpect(jsonPath("$.mensaje").value("Autorización no válida"));
//    }
//
//    @Test
//    void testPagoTarjetaConDatosIncoherentes() throws Exception {
//        PagoTarjetaResponse response = new PagoTarjetaResponse(null, null, null, "Datos de tarjeta no coinciden", false);
//        when(pagoTarjetaService.procesarPago(any())).thenReturn(response);
//
//        mockMvc.perform(post("/api/pago_tarjeta")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(toJson(validRequest)))
//                .andExpect(status().isBadRequest())
//                .andExpect(jsonPath("$.exito").value(false))
//                .andExpect(jsonPath("$.mensaje").value("Datos de tarjeta no coinciden"));
//    }
//
//    @Test
//    void testPagoTarjetaConFondosInsuficientes() throws Exception {
//        PagoTarjetaResponse response = new PagoTarjetaResponse(null, null, null, "Fondos insuficientes", false);
//        when(pagoTarjetaService.procesarPago(any())).thenReturn(response);
//
//        mockMvc.perform(post("/api/pago_tarjeta")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(toJson(validRequest)))
//                .andExpect(status().isBadRequest())
//                .andExpect(jsonPath("$.exito").value(false))
//                .andExpect(jsonPath("$.mensaje").value("Fondos insuficientes"));
//    }
//
//    private String toJson(PagoTarjetaRequest r) {
//        return """
//                {
//                  \"autorizacion\": {\"login\":\"%s\",\"api_token\":\"%s\"},
//                  \"origen\": {\"numeroTarjeta\":\"%s\",\"fechaCaducidad\":\"%s\",\"cvc\":\"%s\",\"nombreCompleto\":\"%s\"},
//                  \"destino\": {\"iban\":\"%s\"},
//                  \"pago\": {\"importe\": %s, \"concepto\": \"%s\"}
//                }
//                """.formatted(
//                r.autorizacion().login(),
//                r.autorizacion().api_token(),
//                r.origen().numeroTarjeta(),
//                r.origen().fechaCaducidad(),
//                r.origen().cvc(),
//                r.origen().nombreCompleto(),
//                r.destino().iban(),
//                r.pago().importe().toPlainString(),
//                r.pago().concepto()
//        );
//    }
//}
