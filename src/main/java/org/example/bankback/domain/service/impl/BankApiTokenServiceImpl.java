package org.example.bankback.domain.service.impl;

import org.example.bankback.domain.service.BankApiTokenService;

public class BankApiTokenServiceImpl implements BankApiTokenService {

    private static final String VALID_BANK_API_TOKEN = "BANK_SECRET_TOKEN_2024";

    @Override
    public boolean validateApiToken(String apiToken) {
        if (apiToken == null || apiToken.isEmpty()) {
            return false;
        }
        return VALID_BANK_API_TOKEN.equals(apiToken);
    }
}
