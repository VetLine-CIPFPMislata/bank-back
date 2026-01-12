package org.example.bankback.domain.repository;

import org.example.bankback.domain.models.Client;
import java.util.Optional;

public interface ClientRepository {
    Optional<Client> findByUsername(String username);
    Optional<Client> findById(Long id);
}