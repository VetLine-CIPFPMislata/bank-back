package org.example.bankback.persistence.dao.impl;

import org.example.bankback.domain.models.BankAccount;
import org.example.bankback.persistence.dao.BankAccountJpaDao;
import org.example.bankback.persistence.dao.entity.BankAccountJpaEntity;
import org.example.bankback.persistence.dao.entity.ClientJpaEntity;
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
@Import(BankAccountJpaDaoImpl.class)
class BankAccountJpaDaoImplIntegrationTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private BankAccountJpaDao bankAccountJpaDao;

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
    void findByIBAN_ShouldReturnAccount_WhenExists() {
        // Arrange
        BankAccountJpaEntity entity = new BankAccountJpaEntity();
        entity.setIBAN("ES9999999999999999999999");
        entity.setSaldo(new BigDecimal("5000.00"));
        entity.setIdCliente(testClient.getId());
        entityManager.persist(entity);
        entityManager.flush();

        // Act
        Optional<BankAccount> result = bankAccountJpaDao.findByIBAN("ES9999999999999999999999");

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get().getIban()).isEqualTo("ES9999999999999999999999");
        assertThat(result.get().getSaldo()).isEqualByComparingTo("5000.00");
        assertThat(result.get().getIdCliente()).isEqualTo(testClient.getId());
    }

    @Test
    void findByIBAN_ShouldReturnEmpty_WhenNotFound() {
        // Act
        Optional<BankAccount> result = bankAccountJpaDao.findByIBAN("ES0000000000000000000000");

        // Assert
        assertThat(result).isEmpty();
    }

    @Test
    void findByClientId_ShouldReturnMultipleAccounts() {
        // Arrange
        BankAccountJpaEntity account1 = new BankAccountJpaEntity();
        account1.setIBAN("ES1111111111111111111111");
        account1.setSaldo(new BigDecimal("1000.00"));
        account1.setIdCliente(testClient.getId());
        entityManager.persist(account1);

        BankAccountJpaEntity account2 = new BankAccountJpaEntity();
        account2.setIBAN("ES2222222222222222222222");
        account2.setSaldo(new BigDecimal("2000.00"));
        account2.setIdCliente(testClient.getId());
        entityManager.persist(account2);

        entityManager.flush();

        // Act
        List<BankAccount> result = bankAccountJpaDao.findByClientId(testClient.getId());

        // Assert
        assertThat(result).hasSize(2);
        assertThat(result).extracting(BankAccount::getIban)
                .containsExactlyInAnyOrder("ES1111111111111111111111", "ES2222222222222222222222");
        assertThat(result).allMatch(acc -> acc.getIdCliente().equals(testClient.getId()));
    }

    @Test
    void findByClientId_ShouldReturnEmptyList_WhenNoAccounts() {
        // Act
        List<BankAccount> result = bankAccountJpaDao.findByClientId(999999L);

        // Assert
        assertThat(result).isEmpty();
    }

    @Test
    void save_ShouldPersistNewAccount() {
        // Arrange
        BankAccount newAccount = new BankAccount(
                null,
                "ES3333333333333333333333",
                new BigDecimal("3000.00")
        );
        newAccount.setIdCliente(testClient.getId());

        // Act
        BankAccount savedAccount = bankAccountJpaDao.save(newAccount);

        // Assert
        assertThat(savedAccount.getId()).isNotNull();
        assertThat(savedAccount.getIban()).isEqualTo("ES3333333333333333333333");
        assertThat(savedAccount.getSaldo()).isEqualByComparingTo("3000.00");

        // Verificar que se guardó en BD
        BankAccountJpaEntity found = entityManager.find(BankAccountJpaEntity.class, savedAccount.getId());
        assertThat(found).isNotNull();
        assertThat(found.getIBAN()).isEqualTo("ES3333333333333333333333");
    }


    @Test
    void findById_ShouldReturnAccount_WhenExists() {
        // Arrange
        BankAccountJpaEntity entity = new BankAccountJpaEntity();
        entity.setIBAN("ES5555555555555555555555");
        entity.setSaldo(new BigDecimal("6000.00"));
        entity.setIdCliente(testClient.getId());
        entityManager.persist(entity);
        entityManager.flush();
        Long accountId = entity.getId();

        // Act
        Optional<BankAccount> result = bankAccountJpaDao.findById(accountId);

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(accountId);
        assertThat(result.get().getIban()).isEqualTo("ES5555555555555555555555");
    }

    @Test
    void findById_ShouldReturnEmpty_WhenNotFound() {
        // Act
        Optional<BankAccount> result = bankAccountJpaDao.findById(999999L);

        // Assert
        assertThat(result).isEmpty();
    }

    @Test
    void save_ShouldHandleDecimalPrecision() {
        // Arrange
        BankAccount account = new BankAccount(
                null,
                "ES6666666666666666666666",
                new BigDecimal("12345.67")
        );
        account.setIdCliente(testClient.getId());

        // Act
        BankAccount saved = bankAccountJpaDao.save(account);
        entityManager.flush();
        entityManager.clear();

        // Assert
        BankAccountJpaEntity found = entityManager.find(BankAccountJpaEntity.class, saved.getId());
        assertThat(found.getSaldo()).isEqualByComparingTo("12345.67");
    }
}
