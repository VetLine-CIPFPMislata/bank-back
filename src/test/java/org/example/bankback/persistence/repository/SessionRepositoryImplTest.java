package org.example.bankback.persistence.repository;

import org.example.bankback.domain.models.Session;
import org.example.bankback.persistence.dao.SessionJpaDao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SessionRepositoryImplTest {

    @Mock
    private SessionJpaDao sessionJpaDao;

    @InjectMocks
    private SessionRepositoryImpl sessionRepository;

    private Session testSession;
    private LocalDateTime testLoginDate;

    @BeforeEach
    void setUp() {
        testLoginDate = LocalDateTime.of(2026, 1, 15, 10, 30);
        testSession = new Session(
                1L,
                1L,
                "test-token-12345",
                testLoginDate
        );
    }

    @Test
    void findByToken_ShouldReturnSession_WhenExists() {
        // Arrange
        when(sessionJpaDao.findByToken("test-token-12345"))
                .thenReturn(Optional.of(testSession));

        // Act
        Optional<Session> result = sessionRepository.findByToken("test-token-12345");

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get().getToken()).isEqualTo("test-token-12345");
        assertThat(result.get().getClientId()).isEqualTo(1L);
        assertThat(result.get().getLoginDate()).isEqualTo(testLoginDate);
        verify(sessionJpaDao).findByToken("test-token-12345");
    }

    @Test
    void findByToken_ShouldReturnEmpty_WhenNotFound() {
        // Arrange
        when(sessionJpaDao.findByToken("nonexistent-token"))
                .thenReturn(Optional.empty());

        // Act
        Optional<Session> result = sessionRepository.findByToken("nonexistent-token");

        // Assert
        assertThat(result).isEmpty();
        verify(sessionJpaDao).findByToken("nonexistent-token");
    }

    @Test
    void save_ShouldPersistSession() {
        // Arrange
        when(sessionJpaDao.save(testSession)).thenReturn(testSession);

        // Act
        Session result = sessionRepository.save(testSession);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getToken()).isEqualTo("test-token-12345");
        assertThat(result.getClientId()).isEqualTo(1L);
        verify(sessionJpaDao).save(testSession);
    }

    @Test
    void deleteByToken_ShouldDelegateToDao() {
        // Arrange
        String tokenToDelete = "token-to-delete";

        // Act
        sessionRepository.deleteByToken(tokenToDelete);

        // Assert
        verify(sessionJpaDao, times(1)).deleteByToken(tokenToDelete);
    }

    @Test
    void save_ShouldReturnSavedSession_WithGeneratedId() {
        // Arrange
        LocalDateTime loginDate = LocalDateTime.now();
        Session newSession = new Session(
                null,
                2L,
                "new-token-67890",
                loginDate
        );
        Session savedSession = new Session(
                10L,
                2L,
                "new-token-67890",
                loginDate
        );
        when(sessionJpaDao.save(newSession)).thenReturn(savedSession);

        // Act
        Session result = sessionRepository.save(newSession);

        // Assert
        assertThat(result.getId()).isNotNull();
        assertThat(result.getId()).isEqualTo(10L);
        assertThat(result.getToken()).isEqualTo("new-token-67890");
        verify(sessionJpaDao).save(newSession);
    }

    @Test
    void findByToken_ShouldVerifyDaoIsCalled() {
        // Arrange
        when(sessionJpaDao.findByToken(anyString())).thenReturn(Optional.of(testSession));

        // Act
        sessionRepository.findByToken("any-token");

        // Assert
        verify(sessionJpaDao, times(1)).findByToken("any-token");
    }
}
