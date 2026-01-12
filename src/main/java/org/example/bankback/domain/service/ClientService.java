package org.example.bankback.domain.service;

import org.example.bankback.domain.models.Client;


import java.util.Optional;

public interface ClientService {
    Optional<Client> findByUsername(String username);
    Optional<Client> login(String username, String password);
}
