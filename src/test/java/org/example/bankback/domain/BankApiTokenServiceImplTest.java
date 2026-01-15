package org.example.bankback.domain;

import org.example.bankback.domain.service.impl.BankApiTokenServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BankApiTokenServiceImplTest {

    private BankApiTokenServiceImpl bankApiTokenService;

    @BeforeEach
    void setUp() {
        bankApiTokenService = new BankApiTokenServiceImpl();
    }

    @Test
    void testValidateApiToken_ConTokenValido_DebeRetornarTrue() {
        
        String validToken = "BANK_SECRET_TOKEN_2024";

        
        boolean result = bankApiTokenService.validateApiToken(validToken);

        
        assertTrue(result);
    }

    @Test
    void testValidateApiToken_ConTokenInvalido_DebeRetornarFalse() {
        
        String invalidToken = "TOKEN_INVALIDO";

        
        boolean result = bankApiTokenService.validateApiToken(invalidToken);

        
        assertFalse(result);
    }

    @Test
    void testValidateApiToken_ConTokenNull_DebeRetornarFalse() {
        
        boolean result = bankApiTokenService.validateApiToken(null);

        
        assertFalse(result);
    }

    @Test
    void testValidateApiToken_ConTokenVacio_DebeRetornarFalse() {
        
        String emptyToken = "";

        
        boolean result = bankApiTokenService.validateApiToken(emptyToken);

        
        assertFalse(result);
    }

    @Test
    void testValidateApiToken_ConTokenConEspacios_DebeRetornarFalse() {
        
        String tokenWithSpaces = "BANK_SECRET_TOKEN_2024 ";

        
        boolean result = bankApiTokenService.validateApiToken(tokenWithSpaces);

        
        assertFalse(result);
    }

    @Test
    void testValidateApiToken_ConTokenEnMinusculas_DebeRetornarFalse() {
        
        String lowercaseToken = "bank_secret_token_2024";

        
        boolean result = bankApiTokenService.validateApiToken(lowercaseToken);

        
        assertFalse(result);
    }

    @Test
    void testValidateApiToken_VerificarSensibilidadACaseSensitive() {
        
        String mixedCaseToken = "Bank_Secret_Token_2024";

        
        boolean result = bankApiTokenService.validateApiToken(mixedCaseToken);

        
        assertFalse(result, "El token debe ser case-sensitive");
    }
}

