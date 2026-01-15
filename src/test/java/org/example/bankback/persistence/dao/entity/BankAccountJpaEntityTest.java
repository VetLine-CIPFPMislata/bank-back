package org.example.bankback.persistence.dao.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class BankAccountJpaEntityTest {

    @Autowired
    private TestEntityManager entityManager;

    private ClientJpaEntity testClient;

    @BeforeEach
    void setUp() {
        testClient = new ClientJpaEntity(
                "testuser", "$2a$10$hash", "Test", "User", "Testing", "11111111X"
        );
        entityManager.persist(testClient);
        entityManager.flush();
    }

    @Test
    void persist_ShouldSaveAccount_WithClientRelation() {

        BankAccountJpaEntity account = new BankAccountJpaEntity();
        account.setIBAN("ES7777777777777777777777");
        account.setSaldo(new BigDecimal("5000.00"));
        account.setIdCliente(testClient.getId());


        entityManager.persist(account);
        entityManager.flush();


        assertThat(account.getId()).isNotNull();
        assertThat(account.getIBAN()).isEqualTo("ES7777777777777777777777");
        assertThat(account.getSaldo()).isEqualByComparingTo("5000.00");
        assertThat(account.getIdCliente()).isEqualTo(testClient.getId());
    }

    @Test
    void persist_ShouldGenerateId_WhenSaved() {
        // Arrange
        BankAccountJpaEntity account = new BankAccountJpaEntity();
        account.setIBAN("ES6666666666666666666666");
        account.setSaldo(new BigDecimal("1000.00"));
        account.setIdCliente(testClient.getId());

        // Act
        entityManager.persist(account);
        entityManager.flush();

        // Assert
        assertThat(account.getId()).isNotNull();
        assertThat(account.getId()).isGreaterThan(0L);
    }

    @Test
    void update_ShouldModifyBalance() {
        // Arrange
        BankAccountJpaEntity account = new BankAccountJpaEntity();
        account.setIBAN("ES5555555555555555555555");
        account.setSaldo(new BigDecimal("2000.00"));
        account.setIdCliente(testClient.getId());
        entityManager.persist(account);
        entityManager.flush();

        // Act
        account.setSaldo(new BigDecimal("3000.00"));
        entityManager.merge(account);
        entityManager.flush();
        entityManager.clear();

        // Assert
        BankAccountJpaEntity found = entityManager.find(BankAccountJpaEntity.class, account.getId());
        assertThat(found.getSaldo()).isEqualByComparingTo("3000.00");
    }

    @Test
    void persist_ShouldAllowMultipleAccounts_ForSameClient() {
        // Arrange
        BankAccountJpaEntity account1 = new BankAccountJpaEntity();
        account1.setIBAN("ES1111111111111111111111");
        account1.setSaldo(new BigDecimal("1000.00"));
        account1.setIdCliente(testClient.getId());

        BankAccountJpaEntity account2 = new BankAccountJpaEntity();
        account2.setIBAN("ES2222222222222222222222");
        account2.setSaldo(new BigDecimal("2000.00"));
        account2.setIdCliente(testClient.getId());

        // Act
        entityManager.persist(account1);
        entityManager.persist(account2);
        entityManager.flush();

        // Assert
        assertThat(account1.getId()).isNotNull();
        assertThat(account2.getId()).isNotNull();
        assertThat(account1.getId()).isNotEqualTo(account2.getId());
    }

    @Test
    void delete_ShouldRemoveAccount() {
        // Arrange
        BankAccountJpaEntity account = new BankAccountJpaEntity();
        account.setIBAN("ES3333333333333333333333");
        account.setSaldo(new BigDecimal("500.00"));
        account.setIdCliente(testClient.getId());
        entityManager.persist(account);
        entityManager.flush();
        Long accountId = account.getId();

        // Act
        entityManager.remove(account);
        entityManager.flush();
        entityManager.clear();

        // Assert
        BankAccountJpaEntity found = entityManager.find(BankAccountJpaEntity.class, accountId);
        assertThat(found).isNull();
    }

    @Test
    void persist_ShouldHandleDecimalPrecision() {
        // Arrange
        BankAccountJpaEntity account = new BankAccountJpaEntity();
        account.setIBAN("ES4444444444444444444444");
        account.setSaldo(new BigDecimal("12345.67"));
        account.setIdCliente(testClient.getId());

        // Act
        entityManager.persist(account);
        entityManager.flush();
        entityManager.clear();

        // Assert
        BankAccountJpaEntity found = entityManager.find(BankAccountJpaEntity.class, account.getId());
        assertThat(found.getSaldo()).isEqualByComparingTo("12345.67");
    }
}
