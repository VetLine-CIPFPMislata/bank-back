package org.example.bankback.persistence.repository.mapper;

import org.example.bankback.domain.models.BankAccount;
import org.example.bankback.domain.models.BankMovement;
import org.example.bankback.domain.models.Client;
import org.example.bankback.domain.models.CreditCard;
import org.example.bankback.persistence.dao.entity.BankAccountJpaEntity;
import org.example.bankback.persistence.dao.entity.BankMovementJpaEntity;
import org.example.bankback.persistence.dao.entity.ClientJpaEntity;
import org.example.bankback.persistence.dao.entity.CreditCardJpaEntity;

public class MapperPersistence {
    private static MapperPersistence INSTANCE;

    private MapperPersistence() {
    }

    public static MapperPersistence getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new MapperPersistence();
        }
        return INSTANCE;
    }

    public BankAccount fromBankAccountJpaEntityToBankAccount (BankAccountJpaEntity bankAccountJpaEntity) {
        if (bankAccountJpaEntity == null) {
            return null;
        }
        BankAccount account = new BankAccount(
                bankAccountJpaEntity.getId(),
                bankAccountJpaEntity.getIBAN(),
                bankAccountJpaEntity.getSaldo()
        );
        account.setIdCliente(bankAccountJpaEntity.getIdCliente());
        return account;
    }

    public BankAccountJpaEntity fromBankAccountToBankAccountJpaEntity (BankAccount bankAccount) {
        if (bankAccount == null) {
            return null;
        }
        BankAccountJpaEntity entity = new BankAccountJpaEntity(
                bankAccount.getId(),
                bankAccount.getSaldo(),
                bankAccount.getIban()
        );
        entity.setIdCliente(bankAccount.getIdCliente());
        return entity;
    }

    public BankMovement fromBankMovementJpaEntityToBankMovement (BankMovementJpaEntity bankMovementJpaEntity) {
        if (bankMovementJpaEntity == null) {
            return null;
        }
        return new BankMovement(
                bankMovementJpaEntity.getId(),
                bankMovementJpaEntity.getTipoMovimientoBancario(),
                bankMovementJpaEntity.getOrigenMovimientoBancario(),
                fromCreditCardJpaEntityToCreditCard(bankMovementJpaEntity.getTarjetaCreditoOrigen()),
                bankMovementJpaEntity.getFechaMovimiento(),
                bankMovementJpaEntity.getImporte(),
                bankMovementJpaEntity.getConcepto()
        );
    }

    public BankMovementJpaEntity fromBankMovementToBankMovementJpaEntity (BankMovement bankMovement) {
        if (bankMovement == null) {
            return null;
        }
        return new BankMovementJpaEntity(
                bankMovement.getId(),
                bankMovement.getTipoMovimientoBancario(),
                bankMovement.getOrigenMovimientoBancario(),
                fromCreditCardToCreditCardJpaEntity(bankMovement.getTarjetaCreditoOrigen()),
                null, // cuentaBancaria - se puede mapear después si es necesario
                bankMovement.getFechaMovimiento(),
                bankMovement.getImporte(),
                bankMovement.getConcepto()
        );
    }

    public Client fromClientJpaEntityToClient (ClientJpaEntity clientJpaEntity) {
        if (clientJpaEntity == null) {
            return null;
        }
        return new Client(
                clientJpaEntity.getId(),
                clientJpaEntity.getUsername(),
                clientJpaEntity.getPassword(),
                clientJpaEntity.getNombre(),
                clientJpaEntity.getApellido1(),
                clientJpaEntity.getApellido2(),
                clientJpaEntity.getDni(),
                clientJpaEntity.getApi_token()
        );
    }

    public ClientJpaEntity fromClientToClientJpaEntity (Client client) {
        if (client == null) {
            return null;
        }
        return new ClientJpaEntity(
                client.getId(),
                client.getUsername(),
                client.getPassword(),
                client.getNombre(),
                client.getApellido1(),
                client.getApellido2(),
                client.getDni(),
                client.getApi_token()
        );
    }

    public CreditCard fromCreditCardJpaEntityToCreditCard (CreditCardJpaEntity creditCardJpaEntity) {
        if (creditCardJpaEntity == null) {
            return null;
        }
        CreditCard card = new CreditCard(
                creditCardJpaEntity.getId(),
                creditCardJpaEntity.getNumeroTarjeta(),
                creditCardJpaEntity.getFechaCaducidad(),
                creditCardJpaEntity.getCvc(),
                creditCardJpaEntity.getNombreCompleto()
        );
        card.setIdCuentaBancaria(creditCardJpaEntity.getIdCuentaBancaria());
        return card;
    }

    public CreditCardJpaEntity fromCreditCardToCreditCardJpaEntity (CreditCard creditCard) {
        if (creditCard == null) {
            return null;
        }
        CreditCardJpaEntity entity = new CreditCardJpaEntity(
                creditCard.getId(),
                creditCard.getNumeroTarjeta(),
                creditCard.getFechaCaducidad(),
                creditCard.getCvc(),
                creditCard.getNombreCompleto()
        );
        entity.setIdCuentaBancaria(creditCard.getIdCuentaBancaria());
        return entity;
    }
}
