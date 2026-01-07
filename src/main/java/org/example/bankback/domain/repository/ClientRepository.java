package org.example.bankback.domain.repository;

import java.util.Optional;

public interface ClientRepository {
    Optional<ClientDto> findByUsername(String username);
}