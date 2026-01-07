package org.example.bankback.domain.service;

import org.example.bankback.domain.models.Cliente;

import java.util.Optional;

public interface ClientService {
    Optional<Cliente> findByUsername(String username);
}
