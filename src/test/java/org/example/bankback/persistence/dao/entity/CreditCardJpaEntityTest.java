package org.example.bankback.persistence.dao.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class CreditCardJpaEntityTest {

    @Autowired
    private TestEntityManager entityManager;

    private BankAccountJpaEntity testAccount;

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
        entityManager.flush();
    }

    @Test
    void persist_ShouldSaveCard_WithAccountRelation() {
        // Arrange
        CreditCardJpaEntity card = new CreditCardJpaEntity();
        card.setNumeroTarjeta("4532888888888888");
        card.setFechaCaducidad("2027-12");
        card.setCvc("123");
        card.setNombreCompleto("Juan Pérez García");
        card.setIdCuentaBancaria(testAccount.getId());

        // Act
        entityManager.persist(card);
        entityManager.flush();

        // Assert
        assertThat(card.getId()).isNotNull();
        assertThat(card.getNumeroTarjeta()).isEqualTo("4532888888888888");
        assertThat(card.getFechaCaducidad()).isEqualTo("2027-12");
        assertThat(card.getCvc()).isEqualTo("123");
        assertThat(card.getNombreCompleto()).isEqualTo("Juan Pérez García");
    }

    @Test
    void persist_ShouldGenerateId_WhenSaved() {
        // Arrange
        CreditCardJpaEntity card = new CreditCardJpaEntity();
        card.setNumeroTarjeta("5425222222222222");
        card.setFechaCaducidad("2026-06");
        card.setCvc("456");
        card.setNombreCompleto("Test User");
        card.setIdCuentaBancaria(testAccount.getId());

        // Act
        entityManager.persist(card);
        entityManager.flush();

        // Assert
        assertThat(card.getId()).isNotNull();
        assertThat(card.getId()).isGreaterThan(0L);
    }

    @Test
    void persist_ShouldAllowMultipleCards_ForSameAccount() {
        // Arrange
        CreditCardJpaEntity card1 = new CreditCardJpaEntity();
        card1.setNumeroTarjeta("4111111111111111");
        card1.setFechaCaducidad("2028-01");
        card1.setCvc("111");
        card1.setNombreCompleto("Cardholder One");
        card1.setIdCuentaBancaria(testAccount.getId());

        CreditCardJpaEntity card2 = new CreditCardJpaEntity();
        card2.setNumeroTarjeta("4222222222222222");
        card2.setFechaCaducidad("2028-02");
        card2.setCvc("222");
        card2.setNombreCompleto("Cardholder Two");
        card2.setIdCuentaBancaria(testAccount.getId());

        // Act
        entityManager.persist(card1);
        entityManager.persist(card2);
        entityManager.flush();

        // Assert
        assertThat(card1.getId()).isNotNull();
        assertThat(card2.getId()).isNotNull();
        assertThat(card1.getId()).isNotEqualTo(card2.getId());
    }

    @Test
    void update_ShouldModifyCardDetails() {
        // Arrange
        CreditCardJpaEntity card = new CreditCardJpaEntity();
        card.setNumeroTarjeta("4916111111111111");
        card.setFechaCaducidad("2028-03");
        card.setCvc("789");
        card.setNombreCompleto("Original Name");
        card.setIdCuentaBancaria(testAccount.getId());
        entityManager.persist(card);
        entityManager.flush();

        // Act
        card.setNombreCompleto("Updated Name");
        entityManager.merge(card);
        entityManager.flush();
        entityManager.clear();

        // Assert
        CreditCardJpaEntity found = entityManager.find(CreditCardJpaEntity.class, card.getId());
        assertThat(found.getNombreCompleto()).isEqualTo("Updated Name");
    }

    @Test
    void delete_ShouldRemoveCard() {
        // Arrange
        CreditCardJpaEntity card = new CreditCardJpaEntity();
        card.setNumeroTarjeta("4024000000000000");
        card.setFechaCaducidad("2025-09");
        card.setCvc("321");
        card.setNombreCompleto("Delete Test");
        card.setIdCuentaBancaria(testAccount.getId());
        entityManager.persist(card);
        entityManager.flush();
        Long cardId = card.getId();

        // Act
        entityManager.remove(card);
        entityManager.flush();
        entityManager.clear();

        // Assert
        CreditCardJpaEntity found = entityManager.find(CreditCardJpaEntity.class, cardId);
        assertThat(found).isNull();
    }
}
