package org.example.bankback.domain.service.impl;

import org.example.bankback.domain.models.Client;
import org.example.bankback.domain.repository.ClientRepository;
import org.example.bankback.domain.service.ClientService;
import org.example.bankback.domain.service.PasswordEncryptionService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ClientServiceImpl implements ClientService {
    private final ClientRepository clientRepository;
    private final PasswordEncryptionService passwordEncryptionService;

    public ClientServiceImpl(ClientRepository clientRepository, PasswordEncryptionService passwordEncryptionService) {
        this.clientRepository = clientRepository;
        this.passwordEncryptionService = passwordEncryptionService;
    }

    @Override
    public Optional<Client> findByUsername(String username) {
        return clientRepository.findByUsername(username);
    }

    @Override
    public Optional<Client> login(String username, String password) {
        return clientRepository.findByUsername(username)
                .filter(client -> passwordEncryptionService.matches(password, client.getPassword()));
    }
}
