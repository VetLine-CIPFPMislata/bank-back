package org.example.bankback.persistence.dao.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class SessionJpaEntityTest {

    @Autowired
    private TestEntityManager entityManager;

    private ClientJpaEntity testClient;

    @BeforeEach
    void setUp() {
        testClient = new ClientJpaEntity(
                "testuser", "$2a$10$hash", "Test", "User", "Testing", "11111111X"
        );
        entityManager.persist(testClient);
        entityManager.flush();
    }

    @Test
    void persist_ShouldGenerateId_AndSaveSession() {
        // Arrange
        SessionJpaEntity session = new SessionJpaEntity();
        session.setClientId(testClient.getId());
        session.setToken("test-token-12345");
        session.setLoginDate(LocalDateTime.now());

        // Act
        entityManager.persist(session);
        entityManager.flush();

        // Assert
        assertThat(session.getId()).isNotNull();
        assertThat(session.getId()).isGreaterThan(0L);
        assertThat(session.getClientId()).isEqualTo(testClient.getId());
        assertThat(session.getToken()).isEqualTo("test-token-12345");
    }

    @Test
    void findById_ShouldReturnSession() {
        // Arrange
        SessionJpaEntity session = new SessionJpaEntity();
        session.setClientId(testClient.getId());
        session.setToken("unique-token-67890");
        session.setLoginDate(LocalDateTime.now());
        entityManager.persist(session);
        entityManager.flush();
        entityManager.clear();

        // Act
        SessionJpaEntity found = entityManager.find(SessionJpaEntity.class, session.getId());

        // Assert
        assertThat(found).isNotNull();
        assertThat(found.getToken()).isEqualTo("unique-token-67890");
        assertThat(found.getClientId()).isEqualTo(testClient.getId());
    }

    @Test
    void persist_ShouldAllowMultipleSessions_ForSameClient() {
        // Arrange
        SessionJpaEntity session1 = new SessionJpaEntity();
        session1.setClientId(testClient.getId());
        session1.setToken("token-session-1");
        session1.setLoginDate(LocalDateTime.now());

        SessionJpaEntity session2 = new SessionJpaEntity();
        session2.setClientId(testClient.getId());
        session2.setToken("token-session-2");
        session2.setLoginDate(LocalDateTime.now().plusMinutes(5));

        // Act
        entityManager.persist(session1);
        entityManager.persist(session2);
        entityManager.flush();

        // Assert
        assertThat(session1.getId()).isNotNull();
        assertThat(session2.getId()).isNotNull();
        assertThat(session1.getId()).isNotEqualTo(session2.getId());
    }

    @Test
    void delete_ShouldRemoveSession() {
        // Arrange
        SessionJpaEntity session = new SessionJpaEntity();
        session.setClientId(testClient.getId());
        session.setToken("token-to-delete");
        session.setLoginDate(LocalDateTime.now());
        entityManager.persist(session);
        entityManager.flush();
        Long sessionId = session.getId();

        // Act
        entityManager.remove(session);
        entityManager.flush();
        entityManager.clear();

        // Assert
        SessionJpaEntity found = entityManager.find(SessionJpaEntity.class, sessionId);
        assertThat(found).isNull();
    }

    @Test
    void update_ShouldModifySession() {
        // Arrange
        SessionJpaEntity session = new SessionJpaEntity();
        session.setClientId(testClient.getId());
        session.setToken("original-token");
        session.setLoginDate(LocalDateTime.now());
        entityManager.persist(session);
        entityManager.flush();

        // Act
        session.setToken("updated-token");
        entityManager.merge(session);
        entityManager.flush();
        entityManager.clear();

        // Assert
        SessionJpaEntity found = entityManager.find(SessionJpaEntity.class, session.getId());
        assertThat(found.getToken()).isEqualTo("updated-token");
    }

    @Test
    void persist_ShouldSaveDateTime_Correctly() {
        // Arrange
        LocalDateTime specificDate = LocalDateTime.of(2026, 1, 15, 10, 30, 45);
        SessionJpaEntity session = new SessionJpaEntity();
        session.setClientId(testClient.getId());
        session.setToken("datetime-test-token");
        session.setLoginDate(specificDate);

        // Act
        entityManager.persist(session);
        entityManager.flush();
        entityManager.clear();

        // Assert
        SessionJpaEntity found = entityManager.find(SessionJpaEntity.class, session.getId());
        assertThat(found.getLoginDate()).isEqualTo(specificDate);
    }

    @Test
    void persist_ShouldSaveAllFields_Correctly() {
        // Arrange
        LocalDateTime loginDate = LocalDateTime.of(2026, 1, 15, 12, 0);
        SessionJpaEntity session = new SessionJpaEntity();
        session.setClientId(testClient.getId());
        session.setToken("complete-test-token");
        session.setLoginDate(loginDate);

        // Act
        entityManager.persist(session);
        entityManager.flush();
        entityManager.clear();

        // Assert
        SessionJpaEntity found = entityManager.find(SessionJpaEntity.class, session.getId());
        assertThat(found).isNotNull();
        assertThat(found.getClientId()).isEqualTo(testClient.getId());
        assertThat(found.getToken()).isEqualTo("complete-test-token");
        assertThat(found.getLoginDate()).isEqualTo(loginDate);
    }
}

