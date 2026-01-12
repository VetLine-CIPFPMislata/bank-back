package org.example.bankback.domain.service;

import org.example.bankback.domain.models.Client;

import java.util.Optional;

public interface AuthService {
    String createTokenFromUser(Client client);
    Optional<Client> getUserFromToken(String token);
    void deleteToken(String token);
}

