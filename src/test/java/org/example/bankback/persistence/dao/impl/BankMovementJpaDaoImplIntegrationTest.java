package org.example.bankback.persistence.dao.impl;

import org.example.bankback.domain.models.BankAccount;
import org.example.bankback.domain.models.BankMovement;
import org.example.bankback.domain.models.TypeBankMovement;
import org.example.bankback.domain.models.OriginBankMovement;
import org.example.bankback.persistence.dao.BankMovementJpaDao;
import org.example.bankback.persistence.dao.entity.BankAccountJpaEntity;
import org.example.bankback.persistence.dao.entity.BankMovementJpaEntity;
import org.example.bankback.persistence.dao.entity.ClientJpaEntity;
import org.example.bankback.persistence.dao.entity.CreditCardJpaEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(BankMovementJpaDaoImpl.class)
class BankMovementJpaDaoImplIntegrationTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private BankMovementJpaDao bankMovementJpaDao;

    private ClientJpaEntity testClient;
    private BankAccountJpaEntity testAccount;
    private CreditCardJpaEntity testCard;

    @BeforeEach
    void setUp() {
        // Crear cliente
        testClient = new ClientJpaEntity(
                "testuser",
                "$2a$10$hash",
                "Test",
                "User",
                "Testing",
                "11111111X"
        );
        entityManager.persist(testClient);

        // Crear cuenta bancaria
        testAccount = new BankAccountJpaEntity();
        testAccount.setIBAN("ES1111111111111111111111");
        testAccount.setSaldo(new BigDecimal("5000.00"));
        testAccount.setIdCliente(testClient.getId());
        entityManager.persist(testAccount);

        // Crear tarjeta
        testCard = new CreditCardJpaEntity();
        testCard.setNumeroTarjeta("4532777777777777");
        testCard.setFechaCaducidad("2027-12");
        testCard.setCvc("123");
        testCard.setNombreCompleto("Test User");
        testCard.setIdCuentaBancaria(testAccount.getId());
        entityManager.persist(testCard);

        entityManager.flush();
    }

    @Test
    void findAllByBankAccountId_ShouldReturnAllMovements_NotOnlyCard() {
        // Arrange - crear movimientos de diferentes orígenes
        BankMovementJpaEntity cardMovement = createMovement(
                TypeBankMovement.DEBE, OriginBankMovement.TARJETA, testCard, new BigDecimal("50.00"), "Pago tarjeta"
        );
        BankMovementJpaEntity transferMovement = createMovement(
                TypeBankMovement.HABER, OriginBankMovement.TRANSFERENCIA, null, new BigDecimal("100.00"), "Transferencia"
        );
        BankMovementJpaEntity domiciliacionMovement = createMovement(
                TypeBankMovement.DEBE, OriginBankMovement.DOMICILIACION, null, new BigDecimal("30.00"), "Recibo luz"
        );

        entityManager.persist(cardMovement);
        entityManager.persist(transferMovement);
        entityManager.persist(domiciliacionMovement);
        entityManager.flush();

        // Act
        List<BankMovement> result = bankMovementJpaDao.findAllByBankAccountId(testAccount.getId());

        // Assert
        assertThat(result).hasSize(3);
        assertThat(result).extracting(BankMovement::getOrigenMovimientoBancario)
                .containsExactlyInAnyOrder(
                        OriginBankMovement.TARJETA,
                        OriginBankMovement.TRANSFERENCIA,
                        OriginBankMovement.DOMICILIACION
                );
        assertThat(result).extracting(BankMovement::getConcepto)
                .containsExactlyInAnyOrder("Pago tarjeta", "Transferencia", "Recibo luz");
    }

    @Test
    void findAllByCreditCardId_ShouldReturnOnlyCardMovements() {
        // Arrange
        BankMovementJpaEntity cardMovement1 = createMovement(
                TypeBankMovement.DEBE, OriginBankMovement.TARJETA, testCard, new BigDecimal("25.00"), "Compra 1"
        );
        BankMovementJpaEntity cardMovement2 = createMovement(
                TypeBankMovement.DEBE, OriginBankMovement.TARJETA, testCard, new BigDecimal("35.00"), "Compra 2"
        );
        BankMovementJpaEntity transferMovement = createMovement(
                TypeBankMovement.HABER, OriginBankMovement.TRANSFERENCIA, null, new BigDecimal("100.00"), "No tarjeta"
        );

        entityManager.persist(cardMovement1);
        entityManager.persist(cardMovement2);
        entityManager.persist(transferMovement);
        entityManager.flush();

        // Act
        List<BankMovement> result = bankMovementJpaDao.findAllByCreditCardId(testCard.getId());

        // Assert
        assertThat(result).hasSize(2);
        assertThat(result).allMatch(m -> m.getOrigenMovimientoBancario() == OriginBankMovement.TARJETA);
        assertThat(result).extracting(BankMovement::getConcepto)
                .containsExactlyInAnyOrder("Compra 1", "Compra 2");
    }

    @Test
    void save_ShouldPersistMovement_WithCorrectOrigin() {
        // Arrange
        BankAccount testBankAccount = new BankAccount(
                testAccount.getId(),
                testAccount.getIBAN(),
                testAccount.getSaldo()
        );

        BankMovement newMovement = new BankMovement(
                null,
                TypeBankMovement.DEBE,
                OriginBankMovement.DOMICILIACION,
                null,
                testBankAccount,
                new Date(),
                new BigDecimal("60.00"),
                "Recibo agua"
        );

        // Act
        BankMovement savedMovement = bankMovementJpaDao.save(newMovement);

        // Assert
        assertThat(savedMovement.getId()).isNotNull();
        assertThat(savedMovement.getOrigenMovimientoBancario()).isEqualTo(OriginBankMovement.DOMICILIACION);
        assertThat(savedMovement.getImporte()).isEqualByComparingTo("60.00");

        // Verificar en BD
        BankMovementJpaEntity found = entityManager.find(BankMovementJpaEntity.class, savedMovement.getId());
        assertThat(found).isNotNull();
        assertThat(found.getOrigenMovimientoBancario()).isEqualTo(OriginBankMovement.DOMICILIACION);
    }

    @Test
    void findAllByBankAccountId_ShouldHandleDifferentMovementTypes() {
        // Arrange
        BankMovementJpaEntity debeMovement = createMovement(
                TypeBankMovement.DEBE, OriginBankMovement.TARJETA, testCard, new BigDecimal("50.00"), "Gasto"
        );
        BankMovementJpaEntity haberMovement = createMovement(
                TypeBankMovement.HABER, OriginBankMovement.TRANSFERENCIA, null, new BigDecimal("200.00"), "Ingreso"
        );

        entityManager.persist(debeMovement);
        entityManager.persist(haberMovement);
        entityManager.flush();

        // Act
        List<BankMovement> result = bankMovementJpaDao.findAllByBankAccountId(testAccount.getId());

        // Assert
        assertThat(result).hasSize(2);
        assertThat(result).extracting(BankMovement::getTipoMovimientoBancario)
                .containsExactlyInAnyOrder(TypeBankMovement.DEBE, TypeBankMovement.HABER);
    }

    @Test
    void findAllByBankAccountId_ShouldReturnEmptyList_WhenNoMovements() {
        // Act
        List<BankMovement> result = bankMovementJpaDao.findAllByBankAccountId(999999L);

        // Assert
        assertThat(result).isEmpty();
    }

    @Test
    void findAllByCreditCardId_ShouldReturnEmptyList_WhenNoCardMovements() {
        // Arrange - crear solo movimiento sin tarjeta
        BankMovementJpaEntity transferMovement = createMovement(
                TypeBankMovement.HABER, OriginBankMovement.TRANSFERENCIA, null, new BigDecimal("100.00"), "Transferencia"
        );
        entityManager.persist(transferMovement);
        entityManager.flush();

        // Act
        List<BankMovement> result = bankMovementJpaDao.findAllByCreditCardId(testCard.getId());

        // Assert
        assertThat(result).isEmpty();
    }

    @Test
    void findById_ShouldReturnMovement_WhenExists() {
        // Arrange
        BankMovementJpaEntity movement = createMovement(
                TypeBankMovement.DEBE, OriginBankMovement.TARJETA, testCard, new BigDecimal("75.00"), "Test movement"
        );
        entityManager.persist(movement);
        entityManager.flush();

        // Act
        Optional<BankMovement> result = bankMovementJpaDao.findById(movement.getId());

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get().getConcepto()).isEqualTo("Test movement");
        assertThat(result.get().getImporte()).isEqualByComparingTo("75.00");
    }

    @Test
    void findById_ShouldReturnEmpty_WhenNotFound() {
        // Act
        Optional<BankMovement> result = bankMovementJpaDao.findById(999999L);

        // Assert
        assertThat(result).isEmpty();
    }

    // Helper method
    private BankMovementJpaEntity createMovement(
            TypeBankMovement type,
            OriginBankMovement origin,
            CreditCardJpaEntity card,
            BigDecimal amount,
            String concept
    ) {
        BankMovementJpaEntity movement = new BankMovementJpaEntity();
        movement.setTipoMovimientoBancario(type);
        movement.setOrigenMovimientoBancario(origin);
        movement.setTarjetaCreditoOrigen(card);
        movement.setCuentaBancaria(testAccount);
        movement.setFechaMovimiento(new Date());
        movement.setImporte(amount);
        movement.setConcepto(concept);
        return movement;
    }
}
