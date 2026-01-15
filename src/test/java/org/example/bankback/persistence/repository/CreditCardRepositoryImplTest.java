package org.example.bankback.persistence.repository;

import org.example.bankback.domain.models.CreditCard;
import org.example.bankback.persistence.dao.CreditCardJpaDao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreditCardRepositoryImplTest {

    @Mock
    private CreditCardJpaDao creditCardJpaDao;

    @InjectMocks
    private CreditCardRepositoryImpl creditCardRepository;

    private CreditCard testCard;

    @BeforeEach
    void setUp() {
        testCard = new CreditCard(
                1L,
                "4532555555555555",
                "2027-12",
                "123",
                "Juan Pérez García"
        );
        testCard.setIdCuentaBancaria(10L);
    }

    @Test
    void findByCardNumber_ShouldReturnCard_WhenExists() {
        // Arrange
        when(creditCardJpaDao.findByCardNumber("4532555555555555"))
                .thenReturn(Optional.of(testCard));

        // Act
        Optional<CreditCard> result = creditCardRepository.findByCardNumber("4532555555555555");

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get().getNumeroTarjeta()).isEqualTo("4532555555555555");
        assertThat(result.get().getNombreCompleto()).isEqualTo("Juan Pérez García");
        verify(creditCardJpaDao).findByCardNumber("4532555555555555");
    }

    @Test
    void findByCardNumber_ShouldReturnEmpty_WhenNotFound() {
        // Arrange
        when(creditCardJpaDao.findByCardNumber("0000000000000000"))
                .thenReturn(Optional.empty());

        // Act
        Optional<CreditCard> result = creditCardRepository.findByCardNumber("0000000000000000");

        // Assert
        assertThat(result).isEmpty();
        verify(creditCardJpaDao).findByCardNumber("0000000000000000");
    }

    @Test
    void findByBankAccountId_ShouldReturnCards_WhenExist() {
        // Arrange
        CreditCard card2 = new CreditCard(
                2L,
                "5425444444444444",
                "2026-06",
                "456",
                "Juan Pérez García"
        );
        card2.setIdCuentaBancaria(10L);

        when(creditCardJpaDao.findByBankAccountId(10L))
                .thenReturn(Arrays.asList(testCard, card2));

        // Act
        List<CreditCard> result = creditCardRepository.findByBankAccountId(10L);

        // Assert
        assertThat(result).hasSize(2);
        assertThat(result).extracting(CreditCard::getNumeroTarjeta)
                .containsExactly("4532555555555555", "5425444444444444");
        verify(creditCardJpaDao).findByBankAccountId(10L);
    }

    @Test
    void findByBankAccountId_ShouldReturnEmptyList_WhenNoCards() {
        // Arrange
        when(creditCardJpaDao.findByBankAccountId(999L)).thenReturn(List.of());

        // Act
        List<CreditCard> result = creditCardRepository.findByBankAccountId(999L);

        // Assert
        assertThat(result).isEmpty();
        verify(creditCardJpaDao).findByBankAccountId(999L);
    }

    @Test
    void findByName_ShouldReturnCard_WhenExists() {
        // Arrange
        when(creditCardJpaDao.findByName("Juan Pérez García"))
                .thenReturn(Optional.of(testCard));

        // Act
        Optional<CreditCard> result = creditCardRepository.findByName("Juan Pérez García");

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get().getNombreCompleto()).isEqualTo("Juan Pérez García");
        verify(creditCardJpaDao).findByName("Juan Pérez García");
    }

    @Test
    void findByName_ShouldReturnEmpty_WhenNotFound() {
        // Arrange
        when(creditCardJpaDao.findByName("Nombre Inexistente"))
                .thenReturn(Optional.empty());

        // Act
        Optional<CreditCard> result = creditCardRepository.findByName("Nombre Inexistente");

        // Assert
        assertThat(result).isEmpty();
        verify(creditCardJpaDao).findByName("Nombre Inexistente");
    }

    @Test
    void findByCardNumber_ShouldDelegateToDao() {
        // Arrange
        when(creditCardJpaDao.findByCardNumber(anyString())).thenReturn(Optional.of(testCard));

        // Act
        creditCardRepository.findByCardNumber("anycard");

        // Assert
        verify(creditCardJpaDao, times(1)).findByCardNumber("anycard");
    }

    @Test
    void findByCardNumber_ShouldReturnCardWithAllFields() {
        // Arrange
        CreditCard completeCard = new CreditCard(
                5L,
                "4916338506082832",
                "2028-03",
                "789",
                "María López Martínez"
        );
        completeCard.setIdCuentaBancaria(20L);
        when(creditCardJpaDao.findByCardNumber("4916338506082832")).thenReturn(Optional.of(completeCard));

        // Act
        Optional<CreditCard> result = creditCardRepository.findByCardNumber("4916338506082832");

        // Assert
        assertThat(result).isPresent();
        CreditCard card = result.get();
        assertThat(card.getId()).isEqualTo(5L);
        assertThat(card.getNumeroTarjeta()).isEqualTo("4916338506082832");
        assertThat(card.getFechaCaducidad()).isEqualTo("2028-03");
        assertThat(card.getCvc()).isEqualTo("789");
        assertThat(card.getNombreCompleto()).isEqualTo("María López Martínez");
        assertThat(card.getIdCuentaBancaria()).isEqualTo(20L);
    }
}
