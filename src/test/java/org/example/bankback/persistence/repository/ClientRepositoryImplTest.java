package org.example.bankback.persistence.repository;

import org.example.bankback.domain.models.Client;
import org.example.bankback.persistence.dao.ClientJpaDao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClientRepositoryImplTest {

    @Mock
    private ClientJpaDao clientJpaDao;

    @InjectMocks
    private ClientRepositoryImpl clientRepository;

    private Client testClient;

    @BeforeEach
    void setUp() {
        testClient = new Client(
                1L,
                "jperez",
                "$2a$10$hashedPassword",
                "Juan",
                "Pérez",
                "García",
                "12345678A"
        );
    }

    @Test
    void findByUsername_ShouldReturnClient_WhenExists() {
        // Arrange
        when(clientJpaDao.findByUsername("jperez")).thenReturn(Optional.of(testClient));

        // Act
        Optional<Client> result = clientRepository.findByUsername("jperez");

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get().getUsername()).isEqualTo("jperez");
        assertThat(result.get().getId()).isEqualTo(1L);
        assertThat(result.get().getDni()).isEqualTo("12345678A");
        verify(clientJpaDao, times(1)).findByUsername("jperez");
    }

    @Test
    void findByUsername_ShouldReturnEmpty_WhenNotFound() {
        // Arrange
        when(clientJpaDao.findByUsername("noexiste")).thenReturn(Optional.empty());

        // Act
        Optional<Client> result = clientRepository.findByUsername("noexiste");

        // Assert
        assertThat(result).isEmpty();
        verify(clientJpaDao).findByUsername("noexiste");
    }

    @Test
    void findById_ShouldReturnClient_WhenExists() {
        // Arrange
        when(clientJpaDao.findById(1L)).thenReturn(Optional.of(testClient));

        // Act
        Optional<Client> result = clientRepository.findById(1L);

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(1L);
        assertThat(result.get().getUsername()).isEqualTo("jperez");
        verify(clientJpaDao).findById(1L);
    }

    @Test
    void findById_ShouldReturnEmpty_WhenNotFound() {
        // Arrange
        when(clientJpaDao.findById(999L)).thenReturn(Optional.empty());

        // Act
        Optional<Client> result = clientRepository.findById(999L);

        // Assert
        assertThat(result).isEmpty();
        verify(clientJpaDao).findById(999L);
    }

    @Test
    void findByUsername_ShouldDelegateToDao() {
        // Arrange
        when(clientJpaDao.findByUsername(anyString())).thenReturn(Optional.of(testClient));

        // Act
        clientRepository.findByUsername("anyuser");

        // Assert
        verify(clientJpaDao, times(1)).findByUsername("anyuser");
    }

    @Test
    void findByUsername_ShouldReturnClientWithAllFields() {
        // Arrange
        Client completeClient = new Client(
                5L,
                "mlopez",
                "$2a$10$anotherHash",
                "María",
                "López",
                "Martínez",
                "87654321B"
        );
        when(clientJpaDao.findByUsername("mlopez")).thenReturn(Optional.of(completeClient));

        // Act
        Optional<Client> result = clientRepository.findByUsername("mlopez");

        // Assert
        assertThat(result).isPresent();
        Client client = result.get();
        assertThat(client.getId()).isEqualTo(5L);
        assertThat(client.getUsername()).isEqualTo("mlopez");
        assertThat(client.getPassword()).isEqualTo("$2a$10$anotherHash");
        assertThat(client.getNombre()).isEqualTo("María");
        assertThat(client.getApellido1()).isEqualTo("López");
        assertThat(client.getApellido2()).isEqualTo("Martínez");
        assertThat(client.getDni()).isEqualTo("87654321B");
    }
}
