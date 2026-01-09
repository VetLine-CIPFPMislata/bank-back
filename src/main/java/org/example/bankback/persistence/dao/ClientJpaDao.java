package org.example.bankback.persistence.dao;

import org.example.bankback.domain.models.Client;

import java.util.Optional;

public interface ClientJpaDao {
    Optional<Client> findByUsername(String username);
    Optional<Client> findByApiToken(String apiToken);
}
