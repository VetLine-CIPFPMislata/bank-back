package org.example.bankback.persistence.dao.impl;

import org.example.bankback.domain.models.Client;
import org.example.bankback.persistence.dao.ClientJpaDao;
import org.example.bankback.persistence.dao.entity.ClientJpaEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(ClientJpaDaoImpl.class)
class ClientJpaDaoImplIntegrationTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ClientJpaDao clientJpaDao;

    @Test
    void findByUsername_ShouldReturnClient_WhenExists() {
        // Arrange
        ClientJpaEntity entity = new ClientJpaEntity(
                "testuser",
                "$2a$10$hashedPassword",
                "John",
                "Doe",
                "Smith",
                "11111111X"
        );
        entityManager.persist(entity);
        entityManager.flush();

        // Act
        Optional<Client> result = clientJpaDao.findByUsername("testuser");

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get().getUsername()).isEqualTo("testuser");
        assertThat(result.get().getNombre()).isEqualTo("John");
        assertThat(result.get().getApellido1()).isEqualTo("Doe");
        assertThat(result.get().getDni()).isEqualTo("11111111X");
    }

    @Test
    void findByUsername_ShouldReturnEmpty_WhenNotFound() {
        // Act
        Optional<Client> result = clientJpaDao.findByUsername("nonexistent");

        // Assert
        assertThat(result).isEmpty();
    }

    @Test
    void findById_ShouldReturnClient_WhenExists() {
        // Arrange
        ClientJpaEntity entity = new ClientJpaEntity(
                "anotheruser",
                "$2a$10$hash",
                "Jane",
                "Smith",
                "Johnson",
                "22222222Y"
        );
        entityManager.persist(entity);
        entityManager.flush();
        Long savedId = entity.getId();

        // Act
        Optional<Client> result = clientJpaDao.findById(savedId);

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(savedId);
        assertThat(result.get().getUsername()).isEqualTo("anotheruser");
        assertThat(result.get().getDni()).isEqualTo("22222222Y");
    }

    @Test
    void findById_ShouldReturnEmpty_WhenNotFound() {
        // Act
        Optional<Client> result = clientJpaDao.findById(999999L);

        // Assert
        assertThat(result).isEmpty();
    }

    @Test
    void findByUsername_ShouldMapAllFieldsCorrectly() {
        // Arrange
        ClientJpaEntity entity = new ClientJpaEntity(
                "completeuser",
                "$2a$10$fullHash",
                "María",
                "López",
                "Martínez",
                "33333333Z"
        );
        entityManager.persist(entity);
        entityManager.flush();

        // Act
        Optional<Client> result = clientJpaDao.findByUsername("completeuser");

        // Assert
        assertThat(result).isPresent();
        Client client = result.get();
        assertThat(client.getId()).isNotNull();
        assertThat(client.getUsername()).isEqualTo("completeuser");
        assertThat(client.getPassword()).isEqualTo("$2a$10$fullHash");
        assertThat(client.getNombre()).isEqualTo("María");
        assertThat(client.getApellido1()).isEqualTo("López");
        assertThat(client.getApellido2()).isEqualTo("Martínez");
        assertThat(client.getDni()).isEqualTo("33333333Z");
    }

    @Test
    void findByUsername_ShouldBeCaseSensitive() {
        // Arrange
        ClientJpaEntity entity = new ClientJpaEntity(
                "CaseSensitiveUser",
                "$2a$10$hash",
                "Test",
                "User",
                "Case",
                "44444444A"
        );
        entityManager.persist(entity);
        entityManager.flush();

        // Act
        Optional<Client> resultLowercase = clientJpaDao.findByUsername("casesensitiveuser");
        Optional<Client> resultCorrect = clientJpaDao.findByUsername("CaseSensitiveUser");

        // Assert
        assertThat(resultLowercase).isEmpty();
        assertThat(resultCorrect).isPresent();
    }
}

