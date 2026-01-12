package org.example.bankback.domain.service.impl;

import org.example.bankback.domain.models.Client;
import org.example.bankback.domain.models.Session;
import org.example.bankback.domain.repository.ClientRepository;
import org.example.bankback.domain.repository.SessionRepository;
import org.example.bankback.domain.service.AuthService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class AuthServiceImpl implements AuthService {

    private final SessionRepository sessionRepository;
    private final ClientRepository clientRepository;

    public AuthServiceImpl(SessionRepository sessionRepository, ClientRepository clientRepository) {
        this.sessionRepository = sessionRepository;
        this.clientRepository = clientRepository;
    }

    @Override
    public String createTokenFromUser(Client client) {
        String token = UUID.randomUUID().toString();


        Session session = new Session(
                null,
                client.getId(),
                token,
                LocalDateTime.now()
        );


        sessionRepository.save(session);

        return token;
    }

    @Override
    public Optional<Client> getUserFromToken(String token) {
        return sessionRepository.findByToken(token)
                .flatMap(session -> clientRepository.findById(session.getClientId()));
    }

    @Override
    public void deleteToken(String token) {
        sessionRepository.deleteByToken(token);
    }
}
