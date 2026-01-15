package org.example.bankback.persistence.dao.impl;

import org.example.bankback.domain.models.CreditCard;
import org.example.bankback.persistence.dao.CreditCardJpaDao;
import org.example.bankback.persistence.dao.entity.BankAccountJpaEntity;
import org.example.bankback.persistence.dao.entity.ClientJpaEntity;
import org.example.bankback.persistence.dao.entity.CreditCardJpaEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(CreditCardJpaDaoImpl.class)
class CreditCardJpaDaoImplIntegrationTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private CreditCardJpaDao creditCardJpaDao;

    private BankAccountJpaEntity testAccount;

    @BeforeEach
    void setUp() {
        // Crear cliente
        ClientJpaEntity client = new ClientJpaEntity(
                "testuser",
                "$2a$10$hash",
                "Test",
                "User",
                "Testing",
                "11111111X"
        );
        entityManager.persist(client);

        // Crear cuenta bancaria
        testAccount = new BankAccountJpaEntity();
        testAccount.setIBAN("ES1111111111111111111111");
        testAccount.setSaldo(new BigDecimal("5000.00"));
        testAccount.setIdCliente(client.getId());
        entityManager.persist(testAccount);

        entityManager.flush();
    }

    @Test
    void findByCardNumber_ShouldReturnCard_WhenExists() {
        // Arrange
        CreditCardJpaEntity entity = new CreditCardJpaEntity();
        entity.setNumeroTarjeta("4532666666666666");
        entity.setFechaCaducidad("2027-12");
        entity.setCvc("123");
        entity.setNombreCompleto("Juan Pérez García");
        entity.setIdCuentaBancaria(testAccount.getId());
        entityManager.persist(entity);
        entityManager.flush();

        // Act
        Optional<CreditCard> result = creditCardJpaDao.findByCardNumber("4532666666666666");

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get().getNumeroTarjeta()).isEqualTo("4532666666666666");
        assertThat(result.get().getFechaCaducidad()).isEqualTo("2027-12");
        assertThat(result.get().getCvc()).isEqualTo("123");
        assertThat(result.get().getNombreCompleto()).isEqualTo("Juan Pérez García");
    }

    @Test
    void findByCardNumber_ShouldReturnEmpty_WhenNotFound() {
        // Act
        Optional<CreditCard> result = creditCardJpaDao.findByCardNumber("0000000000000000");

        // Assert
        assertThat(result).isEmpty();
    }

    @Test
    void findByName_ShouldReturnCard_WhenExists() {
        // Arrange
        CreditCardJpaEntity entity = new CreditCardJpaEntity();
        entity.setNumeroTarjeta("5425111111111111");
        entity.setFechaCaducidad("2026-06");
        entity.setCvc("456");
        entity.setNombreCompleto("Test User Unique Name");
        entity.setIdCuentaBancaria(testAccount.getId());
        entityManager.persist(entity);
        entityManager.flush();

        // Act
        Optional<CreditCard> result = creditCardJpaDao.findByName("Test User Unique Name");

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get().getNombreCompleto()).isEqualTo("Test User Unique Name");
        assertThat(result.get().getNumeroTarjeta()).isEqualTo("5425111111111111");
    }

    @Test
    void findByName_ShouldReturnEmpty_WhenNotFound() {
        // Act
        Optional<CreditCard> result = creditCardJpaDao.findByName("NonExistent Name");

        // Assert
        assertThat(result).isEmpty();
    }

    @Test
    void findByBankAccountId_ShouldReturnMultipleCards() {
        // Arrange
        CreditCardJpaEntity card1 = new CreditCardJpaEntity();
        card1.setNumeroTarjeta("4111111111111111");
        card1.setFechaCaducidad("2028-01");
        card1.setCvc("111");
        card1.setNombreCompleto("Test User 1");
        card1.setIdCuentaBancaria(testAccount.getId());
        entityManager.persist(card1);

        CreditCardJpaEntity card2 = new CreditCardJpaEntity();
        card2.setNumeroTarjeta("4222222222222222");
        card2.setFechaCaducidad("2028-02");
        card2.setCvc("222");
        card2.setNombreCompleto("Test User 2");
        card2.setIdCuentaBancaria(testAccount.getId());
        entityManager.persist(card2);

        entityManager.flush();

        // Act
        List<CreditCard> result = creditCardJpaDao.findByBankAccountId(testAccount.getId());

        // Assert
        assertThat(result).hasSize(2);
        assertThat(result).extracting(CreditCard::getNumeroTarjeta)
                .containsExactlyInAnyOrder("4111111111111111", "4222222222222222");
        assertThat(result).allMatch(card -> card.getIdCuentaBancaria().equals(testAccount.getId()));
    }

    @Test
    void findByBankAccountId_ShouldReturnEmptyList_WhenNoCards() {
        // Act
        List<CreditCard> result = creditCardJpaDao.findByBankAccountId(999999L);

        // Assert
        assertThat(result).isEmpty();
    }

    @Test
    void findByCardNumber_ShouldMapAllFieldsCorrectly() {
        // Arrange
        CreditCardJpaEntity entity = new CreditCardJpaEntity();
        entity.setNumeroTarjeta("4916222222222222");
        entity.setFechaCaducidad("2028-03");
        entity.setCvc("789");
        entity.setNombreCompleto("Antonio García Rodríguez");
        entity.setIdCuentaBancaria(testAccount.getId());
        entityManager.persist(entity);
        entityManager.flush();

        // Act
        Optional<CreditCard> result = creditCardJpaDao.findByCardNumber("4916222222222222");

        // Assert
        assertThat(result).isPresent();
        CreditCard card = result.get();
        assertThat(card.getId()).isNotNull();
        assertThat(card.getNumeroTarjeta()).isEqualTo("4916222222222222");
        assertThat(card.getFechaCaducidad()).isEqualTo("2028-03");
        assertThat(card.getCvc()).isEqualTo("789");
        assertThat(card.getNombreCompleto()).isEqualTo("Antonio García Rodríguez");
        assertThat(card.getIdCuentaBancaria()).isEqualTo(testAccount.getId());
    }

    @Test
    void findByBankAccountId_ShouldOnlyReturnCardsForSpecificAccount() {
        // Arrange - crear otra cuenta
        BankAccountJpaEntity anotherAccount = new BankAccountJpaEntity();
        anotherAccount.setIBAN("ES2222222222222222222222");
        anotherAccount.setSaldo(new BigDecimal("3000.00"));
        anotherAccount.setIdCliente(testAccount.getIdCliente());
        entityManager.persist(anotherAccount);

        // Tarjeta de la primera cuenta
        CreditCardJpaEntity card1 = new CreditCardJpaEntity();
        card1.setNumeroTarjeta("4111111111111111");
        card1.setFechaCaducidad("2028-01");
        card1.setCvc("111");
        card1.setNombreCompleto("Card Account 1");
        card1.setIdCuentaBancaria(testAccount.getId());
        entityManager.persist(card1);

        // Tarjeta de la segunda cuenta
        CreditCardJpaEntity card2 = new CreditCardJpaEntity();
        card2.setNumeroTarjeta("4222222222222222");
        card2.setFechaCaducidad("2028-02");
        card2.setCvc("222");
        card2.setNombreCompleto("Card Account 2");
        card2.setIdCuentaBancaria(anotherAccount.getId());
        entityManager.persist(card2);

        entityManager.flush();

        // Act
        List<CreditCard> result = creditCardJpaDao.findByBankAccountId(testAccount.getId());

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getNumeroTarjeta()).isEqualTo("4111111111111111");
        assertThat(result.get(0).getIdCuentaBancaria()).isEqualTo(testAccount.getId());
    }
}
