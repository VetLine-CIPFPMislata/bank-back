package org.example.bankback;

import org.example.bankback.controller.mapper.PagoTarjetaMapper;
import org.example.bankback.domain.repository.BankAccountRepository;
import org.example.bankback.domain.repository.BankMovementRepository;
import org.example.bankback.domain.repository.ClientRepository;
import org.example.bankback.domain.repository.CreditCardRepository;
import org.example.bankback.domain.service.*;
import org.example.bankback.domain.service.impl.*;
import org.example.bankback.persistence.dao.BankAccountJpaDao;
import org.example.bankback.persistence.dao.BankMovementJpaDao;
import org.example.bankback.persistence.dao.ClientJpaDao;
import org.example.bankback.persistence.dao.CreditCardJpaDao;
import org.example.bankback.persistence.dao.impl.BankAccountJpaDaoImpl;
import org.example.bankback.persistence.dao.impl.BankMovementJpaDaoImpl;
import org.example.bankback.persistence.dao.impl.ClientJpaDaoImpl;
import org.example.bankback.persistence.dao.impl.CreditCardJpaDaoImpl;
import org.example.bankback.persistence.repository.BankAccountRepositoryImpl;
import org.example.bankback.persistence.repository.BankMovementRepositoryImpl;
import org.example.bankback.persistence.repository.ClientRepositoryImpl;
import org.example.bankback.persistence.repository.CreditCardRepositoryImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class SpringConfig {

    @Bean ClientJpaDao clientJpaDao() {
        return new ClientJpaDaoImpl();
    }

    @Bean BankAccountJpaDao bankAccountJpaDao() {
        return new BankAccountJpaDaoImpl();
    }

    @Bean BankMovementJpaDao bankMovementJpaDao() {
        return new BankMovementJpaDaoImpl();
    }

    @Bean CreditCardJpaDao creditCardJpaDao() {
        return new CreditCardJpaDaoImpl();
    }
    @Bean
    public ClientRepository clientRepository(ClientJpaDao clientJpaDao) {
        return new ClientRepositoryImpl(clientJpaDao);
    }

    @Bean
    public BankAccountRepository bankAccountRepository(BankAccountJpaDao bankAccountJpaDao) {
        return new BankAccountRepositoryImpl(bankAccountJpaDao);
    }

    @Bean
    public CreditCardRepository creditCardRepository(CreditCardJpaDao creditCardJpaDao) {
        return new CreditCardRepositoryImpl(creditCardJpaDao);
    }

    @Bean
    public BankMovementRepository bankMovementRepository(BankMovementJpaDao bankMovementJpaDao) {
        return new BankMovementRepositoryImpl(bankMovementJpaDao);
    }

    @Bean
    public BankAccountService bankAccountService(BankAccountRepository bankAccountRepository) {
        return new BankAccountServiceImpl(bankAccountRepository);
    }

    @Bean
    public BankApiTokenService bankApiTokenService() {
        return new BankApiTokenServiceImpl();
    }

    @Bean
    public BankMovementService bankMovementService(BankMovementRepository bankMovementRepository) {
        return new BankMovementServiceImpl(bankMovementRepository);
    }

    @Bean
    public ClientService clientService(ClientRepository clientRepository, PasswordEncryptionService passwordEncryptionService) {
        return new ClientServiceImpl(clientRepository, passwordEncryptionService);
    }

    @Bean
    public CreditCardService creditCardService(CreditCardRepository creditCardRepository) {
        return new CreditCardServiceImpl(creditCardRepository);
    }

    @Bean
    public PagoTarjetaService pagoTarjetaService(ClientService clientService,
                                                 CreditCardService creditCardService,
                                                 BankAccountService bankAccountService,
                                                 BankMovementService bankMovementService) {
        return new PagoTarjetaServiceImpl(clientService, creditCardService, bankAccountService, bankMovementService);
    }

    @Bean
    public PagoTarjetaMapper pagoTarjetaMapper() {
        return new PagoTarjetaMapper();
    }
}
