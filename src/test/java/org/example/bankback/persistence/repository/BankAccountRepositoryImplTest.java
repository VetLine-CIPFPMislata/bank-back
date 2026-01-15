package org.example.bankback.persistence.repository;

import org.example.bankback.domain.models.BankAccount;
import org.example.bankback.persistence.dao.BankAccountJpaDao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BankAccountRepositoryImplTest {

    @Mock
    private BankAccountJpaDao bankAccountJpaDao;

    @InjectMocks
    private BankAccountRepositoryImpl bankAccountRepository;

    private BankAccount testAccount;

    @BeforeEach
    void setUp() {
        testAccount = new BankAccount(
                1L,
                "ES9121000418450200051332",
                new BigDecimal("5000.00")
        );
        testAccount.setIdCliente(1L);
    }

    @Test
    void findByIBAN_ShouldReturnAccount_WhenExists() {
        // Arrange
        when(bankAccountJpaDao.findByIBAN("ES9121000418450200051332"))
                .thenReturn(Optional.of(testAccount));

        // Act
        Optional<BankAccount> result = bankAccountRepository.findByIBAN("ES9121000418450200051332");

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get().getIban()).isEqualTo("ES9121000418450200051332");
        assertThat(result.get().getSaldo()).isEqualByComparingTo("5000.00");
        verify(bankAccountJpaDao).findByIBAN("ES9121000418450200051332");
    }

    @Test
    void findByIBAN_ShouldReturnEmpty_WhenNotFound() {
        // Arrange
        when(bankAccountJpaDao.findByIBAN("ES0000000000000000000000"))
                .thenReturn(Optional.empty());

        // Act
        Optional<BankAccount> result = bankAccountRepository.findByIBAN("ES0000000000000000000000");

        // Assert
        assertThat(result).isEmpty();
        verify(bankAccountJpaDao).findByIBAN("ES0000000000000000000000");
    }

    @Test
    void findByClientId_ShouldReturnAccounts_WhenExist() {
        // Arrange
        BankAccount account2 = new BankAccount(
                2L,
                "ES7620770024003801234567",
                new BigDecimal("12500.50")
        );
        account2.setIdCliente(1L);

        when(bankAccountJpaDao.findByClientId(1L))
                .thenReturn(Arrays.asList(testAccount, account2));

        // Act
        List<BankAccount> result = bankAccountRepository.findByClientId(1L);

        // Assert
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getIban()).isEqualTo("ES9121000418450200051332");
        assertThat(result.get(1).getIban()).isEqualTo("ES7620770024003801234567");
        verify(bankAccountJpaDao).findByClientId(1L);
    }

    @Test
    void findByClientId_ShouldReturnEmptyList_WhenNoAccounts() {
        // Arrange
        when(bankAccountJpaDao.findByClientId(999L)).thenReturn(List.of());

        // Act
        List<BankAccount> result = bankAccountRepository.findByClientId(999L);

        // Assert
        assertThat(result).isEmpty();
        verify(bankAccountJpaDao).findByClientId(999L);
    }

    @Test
    void save_ShouldDelegateToDao() {
        // Arrange
        when(bankAccountJpaDao.save(testAccount)).thenReturn(testAccount);

        // Act
        BankAccount result = bankAccountRepository.save(testAccount);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getIban()).isEqualTo("ES9121000418450200051332");
        verify(bankAccountJpaDao).save(testAccount);
    }

    @Test
    void findById_ShouldReturnAccount_WhenExists() {
        // Arrange
        when(bankAccountJpaDao.findById(1L)).thenReturn(Optional.of(testAccount));

        // Act
        Optional<BankAccount> result = bankAccountRepository.findById(1L);

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(1L);
        verify(bankAccountJpaDao).findById(1L);
    }

    @Test
    void findById_ShouldReturnEmpty_WhenNotFound() {
        // Arrange
        when(bankAccountJpaDao.findById(999L)).thenReturn(Optional.empty());

        // Act
        Optional<BankAccount> result = bankAccountRepository.findById(999L);

        // Assert
        assertThat(result).isEmpty();
        verify(bankAccountJpaDao).findById(999L);
    }

    @Test
    void save_ShouldReturnSavedAccount_WithUpdatedBalance() {
        // Arrange
        BankAccount updatedAccount = new BankAccount(
                1L,
                "ES9121000418450200051332",
                new BigDecimal("6000.00")
        );
        updatedAccount.setIdCliente(1L);
        when(bankAccountJpaDao.save(any(BankAccount.class))).thenReturn(updatedAccount);

        // Act
        BankAccount result = bankAccountRepository.save(testAccount);

        // Assert
        assertThat(result.getSaldo()).isEqualByComparingTo("6000.00");
        verify(bankAccountJpaDao, times(1)).save(testAccount);
    }
}

