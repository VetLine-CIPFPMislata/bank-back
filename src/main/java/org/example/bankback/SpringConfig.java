package org.example.bankback;

import org.example.bankback.domain.models.Cliente;
import org.example.bankback.domain.models.CuentaBancaria;
import org.example.bankback.domain.models.MovimientoBancario;
import org.example.bankback.domain.models.TarjetaCredito;
import org.example.bankback.domain.repository.BankAccountRepository;
import org.example.bankback.domain.repository.BankMovementRepository;
import org.example.bankback.domain.repository.ClientRepository;
import org.example.bankback.domain.repository.CreditCardRepository;
import org.example.bankback.domain.service.BankAccountService;
import org.example.bankback.domain.service.BankMovementService;
import org.example.bankback.domain.service.ClientService;
import org.example.bankback.domain.service.CreditCardService;
import org.example.bankback.domain.service.impl.BankAccountServiceImpl;
import org.example.bankback.domain.service.impl.BankMovementServiceImpl;
import org.example.bankback.domain.service.impl.ClientServiceImpl;
import org.example.bankback.domain.service.impl.CreditCardServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Optional;

@Configuration
public class SpringConfig {
    @Bean
    public BankAccountRepository bankAccountRepository() {
        return new BankAccountRepository() {
            @Override
            public Optional<CuentaBancaria> findById(Long id) {
                return Optional.empty();
            }
            @Override
            public Optional<CuentaBancaria> findByIBAN(BigDecimal iban) {
                return Optional.empty();
            }
        };
    }

    @Bean
    public BankMovementRepository bankMovementRepository() {
        return new BankMovementRepository() {
            @Override
            public Optional<MovimientoBancario> findById(Long id) {
                return Optional.empty();
            }
            @Override
            public Optional<MovimientoBancario> findByCreditCard(TarjetaCredito tarjetaCredito) {
                return Optional.empty();
            }
            @Override
            public Optional<MovimientoBancario> findByDate(Date date) {
                return Optional.empty();
            }
        };
    }

    @Bean
    public ClientRepository clientRepository() {
        return new ClientRepository() {
            @Override
            public Optional<org.example.bankback.domain.models.Cliente> findByUsername(String username) {
                return Optional.empty();
            }

            @Override
            public Optional<Cliente> findByApiToken(String apiToken) {
                return Optional.empty();
            }
        };
    }

    @Bean
    public CreditCardRepository creditCardRepository() {
        return new CreditCardRepository() {
            @Override
            public Optional<org.example.bankback.domain.models.TarjetaCredito> findByCardNumber(String cardNumber) {
                return Optional.empty();
            }
            @Override
            public Optional<org.example.bankback.domain.models.TarjetaCredito> findByName(String name) {
                return Optional.empty();
            }
        };
    }

    @Bean
    public BankAccountService bankAccountService(BankAccountRepository bankAccountRepository) {
        return new BankAccountServiceImpl(bankAccountRepository);
    }

    @Bean
    public BankMovementService bankMovementService(BankMovementRepository bankMovementRepository) {
        return new BankMovementServiceImpl(bankMovementRepository);
    }

    @Bean
    public ClientService clientService(ClientRepository clientRepository) {
        return new ClientServiceImpl(clientRepository);
    }

    @Bean
    public CreditCardService creditCardService(CreditCardRepository creditCardRepository) {
        return new CreditCardServiceImpl(creditCardRepository);
    }
}
