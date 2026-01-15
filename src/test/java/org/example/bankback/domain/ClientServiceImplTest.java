package org.example.bankback.domain;

import org.example.bankback.domain.models.Client;
import org.example.bankback.domain.repository.ClientRepository;
import org.example.bankback.domain.service.PasswordEncryptionService;
import org.example.bankback.domain.service.impl.ClientServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClientServiceImplTest {

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private PasswordEncryptionService passwordEncryptionService;

    @InjectMocks
    private ClientServiceImpl clientService;

    private Client testClient;

    @BeforeEach
    void setUp() {
        testClient = new Client(1L, "juan", "$2a$10$hashedPassword", "Juan", "Garcia", "Garcia", "12345678A");
    }

    @Test
    void testFindByUsername_ConUsuarioExistente_DebeRetornarCliente() {
        
        when(clientRepository.findByUsername("juan")).thenReturn(Optional.of(testClient));

        
        Optional<Client> result = clientService.findByUsername("juan");

        
        assertTrue(result.isPresent());
        assertEquals("juan", result.get().getUsername());
        verify(clientRepository).findByUsername("juan");
    }

    @Test
    void testFindByUsername_ConUsuarioNoExistente_DebeRetornarVacio() {
        
        when(clientRepository.findByUsername("noexiste")).thenReturn(Optional.empty());

        
        Optional<Client> result = clientService.findByUsername("noexiste");

        
        assertFalse(result.isPresent());
        verify(clientRepository).findByUsername("noexiste");
    }

    @Test
    void testLogin_ConCredencialesCorrectas_DebeRetornarCliente() {
        
        String username = "juan";
        String plainPassword = "password123";

        when(clientRepository.findByUsername(username)).thenReturn(Optional.of(testClient));
        when(passwordEncryptionService.matches(plainPassword, testClient.getPassword())).thenReturn(true);

        
        Optional<Client> result = clientService.login(username, plainPassword);

        
        assertTrue(result.isPresent());
        assertEquals(username, result.get().getUsername());
        verify(clientRepository).findByUsername(username);
        verify(passwordEncryptionService).matches(plainPassword, testClient.getPassword());
    }

    @Test
    void testLogin_ConPasswordIncorrecta_DebeRetornarVacio() {
        
        String username = "juan";
        String wrongPassword = "wrongPassword";

        when(clientRepository.findByUsername(username)).thenReturn(Optional.of(testClient));
        when(passwordEncryptionService.matches(wrongPassword, testClient.getPassword())).thenReturn(false);

        
        Optional<Client> result = clientService.login(username, wrongPassword);

        
        assertFalse(result.isPresent());
        verify(clientRepository).findByUsername(username);
        verify(passwordEncryptionService).matches(wrongPassword, testClient.getPassword());
    }

    @Test
    void testLogin_ConUsuarioNoExistente_DebeRetornarVacio() {
        
        String username = "noexiste";
        String password = "password123";

        when(clientRepository.findByUsername(username)).thenReturn(Optional.empty());

        
        Optional<Client> result = clientService.login(username, password);

        
        assertFalse(result.isPresent());
        verify(clientRepository).findByUsername(username);
        verify(passwordEncryptionService, never()).matches(anyString(), anyString());
    }

    @Test
    void testLogin_ConUsernameNull_DebeRetornarVacio() {
        
        when(clientRepository.findByUsername(null)).thenReturn(Optional.empty());

        
        Optional<Client> result = clientService.login(null, "password");

        
        assertFalse(result.isPresent());
        verify(clientRepository).findByUsername(null);
    }

    @Test
    void testLogin_ConPasswordNull_DebeRetornarVacio() {
        
        String username = "juan";

        when(clientRepository.findByUsername(username)).thenReturn(Optional.of(testClient));
        when(passwordEncryptionService.matches(null, testClient.getPassword())).thenReturn(false);

        
        Optional<Client> result = clientService.login(username, null);

        
        assertFalse(result.isPresent());
        verify(clientRepository).findByUsername(username);
    }
}

