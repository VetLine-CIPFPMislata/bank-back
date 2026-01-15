package org.example.bankback.persistence.dao.entity;

import jakarta.persistence.PersistenceException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
class ClientJpaEntityTest {

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void persist_ShouldGenerateId_WhenSaved() {
        // Arrange
        ClientJpaEntity client = new ClientJpaEntity(
                "testuser",
                "hashedPassword",
                "John",
                "Doe",
                "Smith",
                "12345678X"
        );

        // Act
        entityManager.persist(client);
        entityManager.flush();

        // Assert
        assertThat(client.getId()).isNotNull();
        assertThat(client.getId()).isGreaterThan(0L);
    }

    @Test
    void persist_ShouldFail_WhenDniIsDuplicated() {
        // Arrange
        ClientJpaEntity client1 = new ClientJpaEntity(
                "user1", "pass1", "First", "User", "One", "11111111A"
        );
        ClientJpaEntity client2 = new ClientJpaEntity(
                "user2", "pass2", "Second", "User", "Two", "11111111A"
        );

        entityManager.persist(client1);
        entityManager.flush();

        // Act & Assert
        assertThatThrownBy(() -> {
            entityManager.persist(client2);
            entityManager.flush();
        }).isInstanceOf(PersistenceException.class);
    }

    @Test
    void persist_ShouldSaveAllFields_Correctly() {
        // Arrange
        ClientJpaEntity client = new ClientJpaEntity(
                "completeuser", "$2a$10$fullHash", "María", "López", "Martínez", "33333333Z"
        );

        // Act
        entityManager.persist(client);
        entityManager.flush();
        entityManager.clear();

        // Assert
        ClientJpaEntity found = entityManager.find(ClientJpaEntity.class, client.getId());
        assertThat(found).isNotNull();
        assertThat(found.getUsername()).isEqualTo("completeuser");
        assertThat(found.getPassword()).isEqualTo("$2a$10$fullHash");
        assertThat(found.getNombre()).isEqualTo("María");
        assertThat(found.getApellido1()).isEqualTo("López");
        assertThat(found.getApellido2()).isEqualTo("Martínez");
        assertThat(found.getDni()).isEqualTo("33333333Z");
    }

    @Test
    void update_ShouldModifyClient_WhenExists() {
        // Arrange
        ClientJpaEntity client = new ClientJpaEntity(
                "originaluser", "originalpass", "Original", "Name", "Surname", "44444444Y"
        );
        entityManager.persist(client);
        entityManager.flush();

        // Act
        client.setUsername("updateduser");
        client.setNombre("Updated");
        entityManager.merge(client);
        entityManager.flush();
        entityManager.clear();

        // Assert
        ClientJpaEntity found = entityManager.find(ClientJpaEntity.class, client.getId());
        assertThat(found.getUsername()).isEqualTo("updateduser");
        assertThat(found.getNombre()).isEqualTo("Updated");
    }

    @Test
    void delete_ShouldRemoveClient() {
        // Arrange
        ClientJpaEntity client = new ClientJpaEntity(
                "deleteuser", "deletepass", "Delete", "Test", "User", "55555555X"
        );
        entityManager.persist(client);
        entityManager.flush();
        Long clientId = client.getId();

        // Act
        entityManager.remove(client);
        entityManager.flush();
        entityManager.clear();

        // Assert
        ClientJpaEntity found = entityManager.find(ClientJpaEntity.class, clientId);
        assertThat(found).isNull();
    }
}

