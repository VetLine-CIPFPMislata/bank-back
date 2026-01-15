package org.example.bankback.domain;

import org.example.bankback.domain.models.Client;
import org.example.bankback.domain.models.Session;
import org.example.bankback.domain.repository.ClientRepository;
import org.example.bankback.domain.repository.SessionRepository;
import org.example.bankback.domain.service.impl.AuthServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private SessionRepository sessionRepository;

    @Mock
    private ClientRepository clientRepository;

    @InjectMocks
    private AuthServiceImpl authService;

    private Client testClient;
    private Session testSession;

    @BeforeEach
    void setUp() {
        testClient = new Client(1L, "juan", "hashedPassword", "Juan", "Garcia", "Garcia", "12345678A");
        testSession = new Session(1L, 1L, "test-token-uuid", null);
    }

    @Test
    void testCreateTokenFromUser_DebeCrearTokenYGuardarSesion() {
  
        when(sessionRepository.save(any(Session.class))).thenReturn(testSession);

        String token = authService.createTokenFromUser(testClient);

        assertNotNull(token);
        assertFalse(token.isEmpty());

        ArgumentCaptor<Session> sessionCaptor = ArgumentCaptor.forClass(Session.class);
        verify(sessionRepository).save(sessionCaptor.capture());

        Session savedSession = sessionCaptor.getValue();
        assertEquals(testClient.getId(), savedSession.getClientId());
        assertEquals(token, savedSession.getToken());
    }

    @Test
    void testGetUserFromToken_ConTokenValido_DebeRetornarCliente() {
        
        when(sessionRepository.findByToken("test-token-uuid")).thenReturn(Optional.of(testSession));
        when(clientRepository.findById(1L)).thenReturn(Optional.of(testClient));

        
        Optional<Client> result = authService.getUserFromToken("test-token-uuid");

        
        assertTrue(result.isPresent());
        assertEquals(testClient.getId(), result.get().getId());
        assertEquals(testClient.getUsername(), result.get().getUsername());
        verify(sessionRepository).findByToken("test-token-uuid");
        verify(clientRepository).findById(1L);
    }

    @Test
    void testGetUserFromToken_ConTokenInvalido_DebeRetornarVacio() {
        
        when(sessionRepository.findByToken("token-invalido")).thenReturn(Optional.empty());

        
        Optional<Client> result = authService.getUserFromToken("token-invalido");

        
        assertFalse(result.isPresent());
        verify(sessionRepository).findByToken("token-invalido");
        verify(clientRepository, never()).findById(any());
    }

    @Test
    void testGetUserFromToken_ConSesionValidaPeroClienteNoExiste_DebeRetornarVacio() {
        
        when(sessionRepository.findByToken("test-token-uuid")).thenReturn(Optional.of(testSession));
        when(clientRepository.findById(1L)).thenReturn(Optional.empty());

        
        Optional<Client> result = authService.getUserFromToken("test-token-uuid");

        
        assertFalse(result.isPresent());
        verify(sessionRepository).findByToken("test-token-uuid");
        verify(clientRepository).findById(1L);
    }

    @Test
    void testDeleteToken_DebeLlamarAlRepositorio() {
        
        String tokenToDelete = "token-to-delete";
        doNothing().when(sessionRepository).deleteByToken(tokenToDelete);

        
        authService.deleteToken(tokenToDelete);

        
        verify(sessionRepository).deleteByToken(tokenToDelete);
    }

    @Test
    void testDeleteToken_ConTokenNull_NoDeberiaLanzarExcepcion() {
        
        doNothing().when(sessionRepository).deleteByToken(null);


        assertDoesNotThrow(() -> authService.deleteToken(null));
        verify(sessionRepository).deleteByToken(null);
    }
}


