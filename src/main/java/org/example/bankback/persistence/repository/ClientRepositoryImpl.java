package org.example.bankback.persistence.repository;

import org.example.bankback.domain.models.Client;
import org.example.bankback.domain.repository.ClientRepository;
import org.example.bankback.persistence.dao.ClientJpaDao;


import java.util.Optional;

public class ClientRepositoryImpl implements ClientRepository {

    private final ClientJpaDao clientJpaDao;

    public ClientRepositoryImpl(ClientJpaDao clientJpaDao) {
        this.clientJpaDao = clientJpaDao;
    }

    @Override
    public Optional<Client> findByUsername(String username) {
        return clientJpaDao.findByUsername(username);
    }

    @Override
    public Optional<Client> findByApiToken(String apiToken) {
        return clientJpaDao.findByApiToken(apiToken);
    }
}
