package org.example.bankback.persistence.dao.impl;

import org.example.bankback.domain.models.Session;
import org.example.bankback.persistence.dao.SessionJpaDao;
import org.example.bankback.persistence.dao.entity.ClientJpaEntity;
import org.example.bankback.persistence.dao.entity.SessionJpaEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(SessionJpaDaoImpl.class)
class SessionJpaDaoImplIntegrationTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private SessionJpaDao sessionJpaDao;

    private ClientJpaEntity testClient;

    @BeforeEach
    void setUp() {
        testClient = new ClientJpaEntity(
                "testuser",
                "$2a$10$hash",
                "Test",
                "User",
                "Testing",
                "11111111X"
        );
        entityManager.persist(testClient);
        entityManager.flush();
    }

    @Test
    void findByToken_ShouldReturnSession_WhenExists() {
        // Arrange
        SessionJpaEntity entity = new SessionJpaEntity();
        entity.setClientId(testClient.getId());
        entity.setToken("test-token-12345");
        entity.setLoginDate(LocalDateTime.now());
        entityManager.persist(entity);
        entityManager.flush();

        // Act
        Optional<Session> result = sessionJpaDao.findByToken("test-token-12345");

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get().getToken()).isEqualTo("test-token-12345");
        assertThat(result.get().getClientId()).isEqualTo(testClient.getId());
    }

    @Test
    void findByToken_ShouldReturnEmpty_WhenNotFound() {
        // Act
        Optional<Session> result = sessionJpaDao.findByToken("nonexistent-token");

        // Assert
        assertThat(result).isEmpty();
    }

    @Test
    void save_ShouldPersistNewSession() {
        // Arrange
        Session newSession = new Session(
                null,
                testClient.getId(),
                "new-session-token-67890",
                LocalDateTime.now()
        );

        // Act
        Session savedSession = sessionJpaDao.save(newSession);

        // Assert
        assertThat(savedSession.getId()).isNotNull();
        assertThat(savedSession.getToken()).isEqualTo("new-session-token-67890");
        assertThat(savedSession.getClientId()).isEqualTo(testClient.getId());

        // Verificar que se guardó en BD
        SessionJpaEntity found = entityManager.find(SessionJpaEntity.class, savedSession.getId());
        assertThat(found).isNotNull();
        assertThat(found.getToken()).isEqualTo("new-session-token-67890");
    }


    @Test
    void deleteByToken_ShouldRemoveSession_WhenExists() {
        // Arrange
        SessionJpaEntity entity = new SessionJpaEntity();
        entity.setClientId(testClient.getId());
        entity.setToken("token-to-delete");
        entity.setLoginDate(LocalDateTime.now());
        entityManager.persist(entity);
        entityManager.flush();
        Long sessionId = entity.getId();

        // Act
        sessionJpaDao.deleteByToken("token-to-delete");
        entityManager.flush();
        entityManager.clear();

        // Assert
        SessionJpaEntity found = entityManager.find(SessionJpaEntity.class, sessionId);
        assertThat(found).isNull();
    }

    @Test
    void deleteByToken_ShouldNotThrowException_WhenTokenNotFound() {
        // Act & Assert - no debería lanzar excepción
        sessionJpaDao.deleteByToken("nonexistent-token");
    }

    @Test
    void save_ShouldHandleMultipleSessions_ForSameClient() {
        // Arrange
        Session session1 = new Session(
                null,
                testClient.getId(),
                "token-session-1",
                LocalDateTime.now()
        );
        Session session2 = new Session(
                null,
                testClient.getId(),
                "token-session-2",
                LocalDateTime.now().plusMinutes(5)
        );

        // Act
        Session saved1 = sessionJpaDao.save(session1);
        Session saved2 = sessionJpaDao.save(session2);

        // Assert
        assertThat(saved1.getId()).isNotNull();
        assertThat(saved2.getId()).isNotNull();
        assertThat(saved1.getId()).isNotEqualTo(saved2.getId());
        assertThat(saved1.getClientId()).isEqualTo(testClient.getId());
        assertThat(saved2.getClientId()).isEqualTo(testClient.getId());
    }

    @Test
    void findByToken_ShouldMapAllFields_Correctly() {
        // Arrange
        LocalDateTime loginDate = LocalDateTime.of(2026, 1, 15, 10, 30);
        SessionJpaEntity entity = new SessionJpaEntity();
        entity.setClientId(testClient.getId());
        entity.setToken("complete-token");
        entity.setLoginDate(loginDate);
        entityManager.persist(entity);
        entityManager.flush();

        // Act
        Optional<Session> result = sessionJpaDao.findByToken("complete-token");

        // Assert
        assertThat(result).isPresent();
        Session session = result.get();
        assertThat(session.getId()).isNotNull();
        assertThat(session.getClientId()).isEqualTo(testClient.getId());
        assertThat(session.getToken()).isEqualTo("complete-token");
        assertThat(session.getLoginDate()).isEqualTo(loginDate);
    }
}

