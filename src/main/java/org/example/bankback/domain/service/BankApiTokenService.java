package org.example.bankback.domain.service;

public interface BankApiTokenService {
    boolean validateApiToken(String apiToken);
}