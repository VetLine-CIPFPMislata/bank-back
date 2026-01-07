package org.example.bankback.domain.repository;

import org.example.bankback.domain.models.Cliente;
import java.util.Optional;

public interface ClientRepository {
    Optional<Cliente> findByUsername(String username);
    Optional<Cliente> findByApiToken(String apiToken);
}