package org.example.bankback.persistence.repository.mapper;

import org.example.bankback.domain.models.BankAccount;
import org.example.bankback.domain.models.BankMovement;
import org.example.bankback.domain.models.Client;
import org.example.bankback.domain.models.CreditCard;
import org.example.bankback.domain.models.TypeBankMovement;
import org.example.bankback.domain.models.OriginBankMovement;
import org.example.bankback.persistence.dao.entity.BankAccountJpaEntity;
import org.example.bankback.persistence.dao.entity.BankMovementJpaEntity;
import org.example.bankback.persistence.dao.entity.ClientJpaEntity;
import org.example.bankback.persistence.dao.entity.CreditCardJpaEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

class MapperPersistenceTest {

    private MapperPersistence mapper;

    @BeforeEach
    void setUp() {
        mapper = MapperPersistence.getInstance();
    }

    // ========== CLIENT MAPPING TESTS ==========

    @Test
    void fromClientJpaEntityToClient_ShouldMapAllFields_Correctly() {
        // Arrange
        ClientJpaEntity entity = new ClientJpaEntity(
                1L,
                "jperez",
                "$2a$10$gz4VW1ZResYvbq94LLnm0uid1nNS19R/VyZpo/II.MLVyFZdK65Je",
                "Juan",
                "Pérez",
                "García",
                "12345678A"
        );

        // Act
        Client client = mapper.fromClientJpaEntityToClient(entity);

        // Assert
        assertThat(client).isNotNull();
        assertThat(client.getId()).isEqualTo(1L);
        assertThat(client.getUsername()).isEqualTo("jperez");
        assertThat(client.getPassword()).isEqualTo("$2a$10$gz4VW1ZResYvbq94LLnm0uid1nNS19R/VyZpo/II.MLVyFZdK65Je");
        assertThat(client.getNombre()).isEqualTo("Juan");
        assertThat(client.getApellido1()).isEqualTo("Pérez");
        assertThat(client.getApellido2()).isEqualTo("García");
        assertThat(client.getDni()).isEqualTo("12345678A");
    }

    @Test
    void fromClientJpaEntityToClient_ShouldReturnNull_WhenEntityIsNull() {
        // Act
        Client client = mapper.fromClientJpaEntityToClient(null);

        // Assert
        assertThat(client).isNull();
    }

    @Test
    void fromClientToClientJpaEntity_ShouldMapAllFields_Correctly() {
        // Arrange
        Client client = new Client(
                2L,
                "mlopez",
                "$2a$10$hashedPassword",
                "María",
                "López",
                "Martínez",
                "87654321B"
        );

        // Act
        ClientJpaEntity entity = mapper.fromClientToClientJpaEntity(client);

        // Assert
        assertThat(entity).isNotNull();
        assertThat(entity.getId()).isEqualTo(2L);
        assertThat(entity.getUsername()).isEqualTo("mlopez");
        assertThat(entity.getPassword()).isEqualTo("$2a$10$hashedPassword");
        assertThat(entity.getNombre()).isEqualTo("María");
        assertThat(entity.getApellido1()).isEqualTo("López");
        assertThat(entity.getApellido2()).isEqualTo("Martínez");
        assertThat(entity.getDni()).isEqualTo("87654321B");
    }

    @Test
    void fromClientToClientJpaEntity_ShouldReturnNull_WhenClientIsNull() {
        // Act
        ClientJpaEntity entity = mapper.fromClientToClientJpaEntity(null);

        // Assert
        assertThat(entity).isNull();
    }

    // ========== BANK ACCOUNT MAPPING TESTS ==========

    @Test
    void fromBankAccountJpaEntityToBankAccount_ShouldMapIban_AndSaldo() {
        // Arrange
        BankAccountJpaEntity entity = new BankAccountJpaEntity();
        entity.setId(10L);
        entity.setIBAN("ES9121000418450200051332");
        entity.setSaldo(new BigDecimal("5000.00"));
        entity.setIdCliente(1L);

        // Act
        BankAccount account = mapper.fromBankAccountJpaEntityToBankAccount(entity);

        // Assert
        assertThat(account).isNotNull();
        assertThat(account.getId()).isEqualTo(10L);
        assertThat(account.getIban()).isEqualTo("ES9121000418450200051332");
        assertThat(account.getSaldo()).isEqualByComparingTo("5000.00");
        assertThat(account.getIdCliente()).isEqualTo(1L);
    }

    @Test
    void fromBankAccountJpaEntityToBankAccount_ShouldReturnNull_WhenEntityIsNull() {
        // Act
        BankAccount account = mapper.fromBankAccountJpaEntityToBankAccount(null);

        // Assert
        assertThat(account).isNull();
    }

    @Test
    void fromBankAccountToBankAccountJpaEntity_ShouldMapAllFields_Correctly() {
        // Arrange
        BankAccount account = new BankAccount(
                15L,
                "ES7620770024003801234567",
                new BigDecimal("12500.50")
        );
        account.setIdCliente(2L);

        // Act
        BankAccountJpaEntity entity = mapper.fromBankAccountToBankAccountJpaEntity(account);

        // Assert
        assertThat(entity).isNotNull();
        assertThat(entity.getId()).isEqualTo(15L);
        assertThat(entity.getIBAN()).isEqualTo("ES7620770024003801234567");
        assertThat(entity.getSaldo()).isEqualByComparingTo("12500.50");
        assertThat(entity.getIdCliente()).isEqualTo(2L);
    }

    @Test
    void fromBankAccountToBankAccountJpaEntity_ShouldReturnNull_WhenAccountIsNull() {
        // Act
        BankAccountJpaEntity entity = mapper.fromBankAccountToBankAccountJpaEntity(null);

        // Assert
        assertThat(entity).isNull();
    }

    // ========== CREDIT CARD MAPPING TESTS ==========

    @Test
    void fromCreditCardJpaEntityToCreditCard_ShouldMapCardNumber_AndExpiry() {
        // Arrange
        CreditCardJpaEntity entity = new CreditCardJpaEntity();
        entity.setId(5L);
        entity.setNumeroTarjeta("4532333333333333");
        entity.setFechaCaducidad("2027-12");
        entity.setCvc("123");
        entity.setNombreCompleto("Juan Pérez García");
        entity.setIdCuentaBancaria(10L);

        // Act
        CreditCard card = mapper.fromCreditCardJpaEntityToCreditCard(entity);

        // Assert
        assertThat(card).isNotNull();
        assertThat(card.getId()).isEqualTo(5L);
        assertThat(card.getNumeroTarjeta()).isEqualTo("4532333333333333");
        assertThat(card.getFechaCaducidad()).isEqualTo("2027-12");
        assertThat(card.getCvc()).isEqualTo("123");
        assertThat(card.getNombreCompleto()).isEqualTo("Juan Pérez García");
        assertThat(card.getIdCuentaBancaria()).isEqualTo(10L);
    }

    @Test
    void fromCreditCardJpaEntityToCreditCard_ShouldReturnNull_WhenEntityIsNull() {
        // Act
        CreditCard card = mapper.fromCreditCardJpaEntityToCreditCard(null);

        // Assert
        assertThat(card).isNull();
    }

    @Test
    void fromCreditCardToCreditCardJpaEntity_ShouldMapAllFields_Correctly() {
        // Arrange
        CreditCard card = new CreditCard(
                7L,
                "5425000000000000",
                "2026-06",
                "456",
                "María López Martínez"
        );
        card.setIdCuentaBancaria(15L);

        // Act
        CreditCardJpaEntity entity = mapper.fromCreditCardToCreditCardJpaEntity(card);

        // Assert
        assertThat(entity).isNotNull();
        assertThat(entity.getId()).isEqualTo(7L);
        assertThat(entity.getNumeroTarjeta()).isEqualTo("5425000000000000");
        assertThat(entity.getFechaCaducidad()).isEqualTo("2026-06");
        assertThat(entity.getCvc()).isEqualTo("456");
        assertThat(entity.getNombreCompleto()).isEqualTo("María López Martínez");
        assertThat(entity.getIdCuentaBancaria()).isEqualTo(15L);
    }

    @Test
    void fromCreditCardToCreditCardJpaEntity_ShouldReturnNull_WhenCardIsNull() {
        // Act
        CreditCardJpaEntity entity = mapper.fromCreditCardToCreditCardJpaEntity(null);

        // Assert
        assertThat(entity).isNull();
    }

    // ========== BANK MOVEMENT MAPPING TESTS ==========

    @Test
    void fromBankMovementJpaEntityToBankMovement_ShouldMapAmount_AndConcept() {
        // Arrange
        Date movementDate = new Date();
        BankMovementJpaEntity entity = new BankMovementJpaEntity();
        entity.setId(100L);
        entity.setTipoMovimientoBancario(TypeBankMovement.DEBE);
        entity.setOrigenMovimientoBancario(OriginBankMovement.TARJETA);
        entity.setFechaMovimiento(movementDate);
        entity.setImporte(new BigDecimal("49.99"));
        entity.setConcepto("Compra tienda online");

        // Act
        BankMovement movement = mapper.fromBankMovementJpaEntityToBankMovement(entity);

        // Assert
        assertThat(movement).isNotNull();
        assertThat(movement.getId()).isEqualTo(100L);
        assertThat(movement.getTipoMovimientoBancario()).isEqualTo(TypeBankMovement.DEBE);
        assertThat(movement.getOrigenMovimientoBancario()).isEqualTo(OriginBankMovement.TARJETA);
        assertThat(movement.getFechaMovimiento()).isEqualTo(movementDate);
        assertThat(movement.getImporte()).isEqualByComparingTo("49.99");
        assertThat(movement.getConcepto()).isEqualTo("Compra tienda online");
    }

    @Test
    void fromBankMovementJpaEntityToBankMovement_ShouldHandleNullCreditCard() {
        // Arrange
        BankMovementJpaEntity entity = new BankMovementJpaEntity();
        entity.setId(101L);
        entity.setTipoMovimientoBancario(TypeBankMovement.HABER);
        entity.setOrigenMovimientoBancario(OriginBankMovement.TRANSFERENCIA);
        entity.setTarjetaCreditoOrigen(null);
        entity.setFechaMovimiento(new Date());
        entity.setImporte(new BigDecimal("1200.00"));
        entity.setConcepto("Transferencia recibida");

        // Act
        BankMovement movement = mapper.fromBankMovementJpaEntityToBankMovement(entity);

        // Assert
        assertThat(movement).isNotNull();
        assertThat(movement.getTarjetaCreditoOrigen()).isNull();
        assertThat(movement.getOrigenMovimientoBancario()).isEqualTo(OriginBankMovement.TRANSFERENCIA);
    }

    @Test
    void fromBankMovementJpaEntityToBankMovement_ShouldReturnNull_WhenEntityIsNull() {
        // Act
        BankMovement movement = mapper.fromBankMovementJpaEntityToBankMovement(null);

        // Assert
        assertThat(movement).isNull();
    }

    @Test
    void fromBankMovementToBankMovementJpaEntity_ShouldMapAllFields_Correctly() {
        // Arrange
        Date movementDate = new Date();
        BankMovement movement = new BankMovement(
                200L,
                TypeBankMovement.DEBE,
                OriginBankMovement.DOMICILIACION,
                null,
                null,
                movementDate,
                new BigDecimal("60.00"),
                "Recibo luz"
        );

        // Act
        BankMovementJpaEntity entity = mapper.fromBankMovementToBankMovementJpaEntity(movement);

        // Assert
        assertThat(entity).isNotNull();
        assertThat(entity.getId()).isEqualTo(200L);
        assertThat(entity.getTipoMovimientoBancario()).isEqualTo(TypeBankMovement.DEBE);
        assertThat(entity.getOrigenMovimientoBancario()).isEqualTo(OriginBankMovement.DOMICILIACION);
        assertThat(entity.getFechaMovimiento()).isEqualTo(movementDate);
        assertThat(entity.getImporte()).isEqualByComparingTo("60.00");
        assertThat(entity.getConcepto()).isEqualTo("Recibo luz");
    }

    @Test
    void fromBankMovementToBankMovementJpaEntity_ShouldReturnNull_WhenMovementIsNull() {
        // Act
        BankMovementJpaEntity entity = mapper.fromBankMovementToBankMovementJpaEntity(null);

        // Assert
        assertThat(entity).isNull();
    }

    // ========== BIDIRECTIONAL MAPPING TESTS ==========

    @Test
    void clientMapping_ShouldBeReversible() {
        // Arrange
        Client originalClient = new Client(
                99L, "testuser", "hashedPwd", "Test", "User", "Testing", "99999999Z"
        );

        // Act
        ClientJpaEntity entity = mapper.fromClientToClientJpaEntity(originalClient);
        Client mappedBackClient = mapper.fromClientJpaEntityToClient(entity);

        // Assert
        assertThat(mappedBackClient.getId()).isEqualTo(originalClient.getId());
        assertThat(mappedBackClient.getUsername()).isEqualTo(originalClient.getUsername());
        assertThat(mappedBackClient.getDni()).isEqualTo(originalClient.getDni());
    }

    @Test
    void bankAccountMapping_ShouldBeReversible() {
        // Arrange
        BankAccount originalAccount = new BankAccount(
                50L, "ES1234567890123456789012", new BigDecimal("3000.75")
        );
        originalAccount.setIdCliente(3L);

        // Act
        BankAccountJpaEntity entity = mapper.fromBankAccountToBankAccountJpaEntity(originalAccount);
        BankAccount mappedBackAccount = mapper.fromBankAccountJpaEntityToBankAccount(entity);

        // Assert
        assertThat(mappedBackAccount.getId()).isEqualTo(originalAccount.getId());
        assertThat(mappedBackAccount.getIban()).isEqualTo(originalAccount.getIban());
        assertThat(mappedBackAccount.getSaldo()).isEqualByComparingTo(originalAccount.getSaldo());
        assertThat(mappedBackAccount.getIdCliente()).isEqualTo(originalAccount.getIdCliente());
    }

    @Test
    void creditCardMapping_ShouldBeReversible() {
        // Arrange
        CreditCard originalCard = new CreditCard(
                20L, "4916338506082832", "2028-03", "789", "Antonio García Rodríguez"
        );
        originalCard.setIdCuentaBancaria(50L);

        // Act
        CreditCardJpaEntity entity = mapper.fromCreditCardToCreditCardJpaEntity(originalCard);
        CreditCard mappedBackCard = mapper.fromCreditCardJpaEntityToCreditCard(entity);

        // Assert
        assertThat(mappedBackCard.getId()).isEqualTo(originalCard.getId());
        assertThat(mappedBackCard.getNumeroTarjeta()).isEqualTo(originalCard.getNumeroTarjeta());
        assertThat(mappedBackCard.getFechaCaducidad()).isEqualTo(originalCard.getFechaCaducidad());
        assertThat(mappedBackCard.getIdCuentaBancaria()).isEqualTo(originalCard.getIdCuentaBancaria());
    }

    @Test
    void getInstance_ShouldReturnSameInstance() {
        // Act
        MapperPersistence instance1 = MapperPersistence.getInstance();
        MapperPersistence instance2 = MapperPersistence.getInstance();

        // Assert
        assertThat(instance1).isSameAs(instance2);
    }
}
