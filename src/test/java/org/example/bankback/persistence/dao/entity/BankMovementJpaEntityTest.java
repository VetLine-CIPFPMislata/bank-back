package org.example.bankback.persistence.dao.entity;

import org.example.bankback.domain.models.TypeBankMovement;
import org.example.bankback.domain.models.OriginBankMovement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.math.BigDecimal;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class BankMovementJpaEntityTest {

    @Autowired
    private TestEntityManager entityManager;

    private BankAccountJpaEntity testAccount;
    private CreditCardJpaEntity testCard;

    @BeforeEach
    void setUp() {
        ClientJpaEntity client = new ClientJpaEntity(
                "testuser", "$2a$10$hash", "Test", "User", "Testing", "11111111X"
        );
        entityManager.persist(client);

        testAccount = new BankAccountJpaEntity();
        testAccount.setIBAN("ES1111111111111111111111");
        testAccount.setSaldo(new BigDecimal("5000.00"));
        testAccount.setIdCliente(client.getId());
        entityManager.persist(testAccount);

        testCard = new CreditCardJpaEntity();
        testCard.setNumeroTarjeta("4532999999999999");
        testCard.setFechaCaducidad("2027-12");
        testCard.setCvc("123");
        testCard.setNombreCompleto("Test User");
        testCard.setIdCuentaBancaria(testAccount.getId());
        entityManager.persist(testCard);

        entityManager.flush();
    }

    @Test
    void persist_ShouldSaveMovement_WithAccountAndCard() {
        BankMovementJpaEntity movement = new BankMovementJpaEntity();
        movement.setTipoMovimientoBancario(TypeBankMovement.DEBE);
        movement.setOrigenMovimientoBancario(OriginBankMovement.TARJETA);
        movement.setTarjetaCreditoOrigen(testCard);
        movement.setCuentaBancaria(testAccount);
        movement.setFechaMovimiento(new Date());
        movement.setImporte(new BigDecimal("49.99"));
        movement.setConcepto("Compra tienda online");

        entityManager.persist(movement);
        entityManager.flush();

        assertThat(movement.getId()).isNotNull();
        assertThat(movement.getTipoMovimientoBancario()).isEqualTo(TypeBankMovement.DEBE);
        assertThat(movement.getOrigenMovimientoBancario()).isEqualTo(OriginBankMovement.TARJETA);
        assertThat(movement.getImporte()).isEqualByComparingTo("49.99");
    }

    @Test
    void persist_ShouldSaveMovement_WithoutCard_WhenOriginIsTransferencia() {
        BankMovementJpaEntity movement = new BankMovementJpaEntity();
        movement.setTipoMovimientoBancario(TypeBankMovement.HABER);
        movement.setOrigenMovimientoBancario(OriginBankMovement.TRANSFERENCIA);
        movement.setTarjetaCreditoOrigen(null);
        movement.setCuentaBancaria(testAccount);
        movement.setFechaMovimiento(new Date());
        movement.setImporte(new BigDecimal("1200.00"));
        movement.setConcepto("Ingreso salario");

        entityManager.persist(movement);
        entityManager.flush();

        assertThat(movement.getId()).isNotNull();
        assertThat(movement.getOrigenMovimientoBancario()).isEqualTo(OriginBankMovement.TRANSFERENCIA);
        assertThat(movement.getTarjetaCreditoOrigen()).isNull();
    }

    @Test
    void persist_ShouldHandleAllOriginBankMovementTypes() {
        BankMovementJpaEntity tarjeta = createMovement(OriginBankMovement.TARJETA, new BigDecimal("50.00"));
        BankMovementJpaEntity transferencia = createMovement(OriginBankMovement.TRANSFERENCIA, new BigDecimal("100.00"));
        BankMovementJpaEntity domiciliacion = createMovement(OriginBankMovement.DOMICILIACION, new BigDecimal("30.00"));

        entityManager.persist(tarjeta);
        entityManager.persist(transferencia);
        entityManager.persist(domiciliacion);
        entityManager.flush();

        assertThat(tarjeta.getId()).isNotNull();
        assertThat(transferencia.getId()).isNotNull();
        assertThat(domiciliacion.getId()).isNotNull();
    }

    @Test
    void persist_ShouldGenerateId_WhenSaved() {

        BankMovementJpaEntity movement = new BankMovementJpaEntity();
        movement.setTipoMovimientoBancario(TypeBankMovement.DEBE);
        movement.setOrigenMovimientoBancario(OriginBankMovement.TARJETA);
        movement.setCuentaBancaria(testAccount);
        movement.setFechaMovimiento(new Date());
        movement.setImporte(new BigDecimal("25.00"));
        movement.setConcepto("Test");


        entityManager.persist(movement);
        entityManager.flush();

        assertThat(movement.getId()).isNotNull();
        assertThat(movement.getId()).isGreaterThan(0L);
    }

    @Test
    void delete_ShouldRemoveMovement() {

        BankMovementJpaEntity movement = createMovement(OriginBankMovement.TARJETA, new BigDecimal("50.00"));
        entityManager.persist(movement);
        entityManager.flush();
        Long movementId = movement.getId();


        entityManager.remove(movement);
        entityManager.flush();
        entityManager.clear();


        BankMovementJpaEntity found = entityManager.find(BankMovementJpaEntity.class, movementId);
        assertThat(found).isNull();
    }

    @Test
    void persist_ShouldSaveDateTime_Correctly() {

        Date specificDate = new Date();
        BankMovementJpaEntity movement = new BankMovementJpaEntity();
        movement.setTipoMovimientoBancario(TypeBankMovement.HABER);
        movement.setOrigenMovimientoBancario(OriginBankMovement.TRANSFERENCIA);
        movement.setCuentaBancaria(testAccount);
        movement.setFechaMovimiento(specificDate);
        movement.setImporte(new BigDecimal("200.00"));
        movement.setConcepto("Date test");


        entityManager.persist(movement);
        entityManager.flush();
        entityManager.clear();


        BankMovementJpaEntity found = entityManager.find(BankMovementJpaEntity.class, movement.getId());
        assertThat(found.getFechaMovimiento()).isNotNull();
    }

    private BankMovementJpaEntity createMovement(OriginBankMovement origin, BigDecimal amount) {
        BankMovementJpaEntity movement = new BankMovementJpaEntity();
        movement.setTipoMovimientoBancario(TypeBankMovement.DEBE);
        movement.setOrigenMovimientoBancario(origin);
        movement.setCuentaBancaria(testAccount);
        movement.setFechaMovimiento(new Date());
        movement.setImporte(amount);
        movement.setConcepto("Test " + origin);
        if (origin == OriginBankMovement.TARJETA) {
            movement.setTarjetaCreditoOrigen(testCard);
        }
        return movement;
    }
}
