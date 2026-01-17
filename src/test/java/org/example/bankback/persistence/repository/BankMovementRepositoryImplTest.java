package org.example.bankback.persistence.repository;

import org.example.bankback.domain.models.BankMovement;
import org.example.bankback.domain.models.TypeBankMovement;
import org.example.bankback.domain.models.OriginBankMovement;
import org.example.bankback.persistence.dao.BankMovementJpaDao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BankMovementRepositoryImplTest {

    @Mock
    private BankMovementJpaDao bankMovementJpaDao;

    @InjectMocks
    private BankMovementRepositoryImpl bankMovementRepository;

    private BankMovement testMovement;

    @BeforeEach
    void setUp() {
        testMovement = new BankMovement(
                1L,
                TypeBankMovement.DEBE,
                OriginBankMovement.TARJETA,
                null,
                null,
                new Date(),
                new BigDecimal("49.99"),
                "Compra tienda online"
        );
    }

    @Test
    void findAllByBankAccountId_ShouldReturnMovements_WhenExist() {
        // Arrange
        BankMovement movement2 = new BankMovement(
                2L,
                TypeBankMovement.HABER,
                OriginBankMovement.TRANSFERENCIA,
                null,
                null,
                new Date(),
                new BigDecimal("1200.00"),
                "Ingreso salario"
        );

        when(bankMovementJpaDao.findAllByBankAccountId(1L))
                .thenReturn(Arrays.asList(testMovement, movement2));

        // Act
        List<BankMovement> result = bankMovementRepository.findAllByBankAccountId(1L);

        // Assert
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getConcepto()).isEqualTo("Compra tienda online");
        assertThat(result.get(1).getConcepto()).isEqualTo("Ingreso salario");
        verify(bankMovementJpaDao).findAllByBankAccountId(1L);
    }

    @Test
    void findAllByBankAccountId_ShouldReturnAllMovements_NotOnlyCard() {
        // Arrange
        BankMovement cardMovement = new BankMovement(
                1L, TypeBankMovement.DEBE, OriginBankMovement.TARJETA, null, null,
                new Date(), new BigDecimal("50.00"), "Pago tarjeta"
        );
        BankMovement transferMovement = new BankMovement(
                2L, TypeBankMovement.HABER, OriginBankMovement.TRANSFERENCIA, null, null,
                new Date(), new BigDecimal("100.00"), "Transferencia"
        );
        BankMovement domiciliacionMovement = new BankMovement(
                3L, TypeBankMovement.DEBE, OriginBankMovement.DOMICILIACION, null, null,
                new Date(), new BigDecimal("30.00"), "Recibo luz"
        );

        when(bankMovementJpaDao.findAllByBankAccountId(1L))
                .thenReturn(Arrays.asList(cardMovement, transferMovement, domiciliacionMovement));

        // Act
        List<BankMovement> result = bankMovementRepository.findAllByBankAccountId(1L);

        // Assert
        assertThat(result).hasSize(3);
        assertThat(result).extracting(BankMovement::getOrigenMovimientoBancario)
                .containsExactly(
                        OriginBankMovement.TARJETA,
                        OriginBankMovement.TRANSFERENCIA,
                        OriginBankMovement.DOMICILIACION
                );
        verify(bankMovementJpaDao).findAllByBankAccountId(1L);
    }

    @Test
    void findAllByBankAccountId_ShouldReturnEmptyList_WhenNoMovements() {
        // Arrange
        when(bankMovementJpaDao.findAllByBankAccountId(999L)).thenReturn(List.of());

        // Act
        List<BankMovement> result = bankMovementRepository.findAllByBankAccountId(999L);

        // Assert
        assertThat(result).isEmpty();
        verify(bankMovementJpaDao).findAllByBankAccountId(999L);
    }

    @Test
    void findAllByCreditCardId_ShouldReturnOnlyCardMovements() {
        // Arrange
        BankMovement cardMovement1 = new BankMovement(
                1L, TypeBankMovement.DEBE, OriginBankMovement.TARJETA, null, null,
                new Date(), new BigDecimal("25.00"), "Compra 1"
        );
        BankMovement cardMovement2 = new BankMovement(
                2L, TypeBankMovement.DEBE, OriginBankMovement.TARJETA, null, null,
                new Date(), new BigDecimal("35.00"), "Compra 2"
        );

        when(bankMovementJpaDao.findAllByCreditCardId(10L))
                .thenReturn(Arrays.asList(cardMovement1, cardMovement2));

        // Act
        List<BankMovement> result = bankMovementRepository.findAllByCreditCardId(10L);

        // Assert
        assertThat(result).hasSize(2);
        assertThat(result).allMatch(m -> m.getOrigenMovimientoBancario() == OriginBankMovement.TARJETA);
        verify(bankMovementJpaDao).findAllByCreditCardId(10L);
    }

    @Test
    void findAllByCreditCardId_ShouldReturnEmptyList_WhenNoCardMovements() {
        // Arrange
        when(bankMovementJpaDao.findAllByCreditCardId(999L)).thenReturn(List.of());

        // Act
        List<BankMovement> result = bankMovementRepository.findAllByCreditCardId(999L);

        // Assert
        assertThat(result).isEmpty();
        verify(bankMovementJpaDao).findAllByCreditCardId(999L);
    }

    @Test
    void save_ShouldPersistMovement_WithCorrectOrigin() {
        // Arrange
        when(bankMovementJpaDao.save(testMovement)).thenReturn(testMovement);

        // Act
        BankMovement result = bankMovementRepository.save(testMovement);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getOrigenMovimientoBancario()).isEqualTo(OriginBankMovement.TARJETA);
        assertThat(result.getImporte()).isEqualByComparingTo("49.99");
        verify(bankMovementJpaDao).save(testMovement);
    }

    @Test
    void save_ShouldDelegateToDao() {
        // Arrange
        when(bankMovementJpaDao.save(any(BankMovement.class))).thenReturn(testMovement);

        // Act
        bankMovementRepository.save(testMovement);

        // Assert
        verify(bankMovementJpaDao, times(1)).save(testMovement);
    }

    @Test
    void findById_ShouldReturnMovement_WhenExists() {
        // Arrange
        when(bankMovementJpaDao.findById(1L)).thenReturn(Optional.of(testMovement));

        // Act
        Optional<BankMovement> result = bankMovementRepository.findById(1L);

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(1L);
        assertThat(result.get().getConcepto()).isEqualTo("Compra tienda online");
        verify(bankMovementJpaDao).findById(1L);
    }

    @Test
    void findById_ShouldReturnEmpty_WhenNotFound() {
        // Arrange
        when(bankMovementJpaDao.findById(999L)).thenReturn(Optional.empty());

        // Act
        Optional<BankMovement> result = bankMovementRepository.findById(999L);

        // Assert
        assertThat(result).isEmpty();
        verify(bankMovementJpaDao).findById(999L);
    }
}

