package org.example.bankback.domain.service.impl;

import org.example.bankback.domain.models.Client;
import org.example.bankback.domain.repository.ClientRepository;
import org.example.bankback.domain.service.ClientService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ClientServiceImpl implements ClientService {
    private final ClientRepository clientRepository;

    public ClientServiceImpl(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    @Override
    public Optional<Client> findByUsername(String username) {
        return clientRepository.findByUsername(username);
    }
}
