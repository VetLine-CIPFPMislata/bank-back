package org.example.bankback.domain.service.impl;

import org.example.bankback.domain.models.BankAccount;
import org.example.bankback.domain.models.BankMovement;
import org.example.bankback.domain.models.CreditCard;
import org.example.bankback.domain.models.Client;
import org.example.bankback.domain.service.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class LoginServiceImpl implements LoginService {

    private final ClientService clientService;
    private final BankAccountService bankAccountService;
    private final CreditCardService creditCardService;
    private final BankMovementService bankMovementService;

    public LoginServiceImpl(ClientService clientService,
                           BankAccountService bankAccountService,
                           CreditCardService creditCardService,
                           BankMovementService bankMovementService) {
        this.clientService = clientService;
        this.bankAccountService = bankAccountService;
        this.creditCardService = creditCardService;
        this.bankMovementService = bankMovementService;
    }

    @Override
    public Optional<LoginResult> login(String username, String password, String apiToken) {
        Optional<Client> clientOpt = clientService.findByUsername(username);

        if (clientOpt.isEmpty()) {
            return Optional.empty();
        }

        Client client = clientOpt.get();


        List<BankAccount> cuentas = bankAccountService.findByClientId(client.getId());


        List<CreditCard> tarjetas = new ArrayList<>();
        for (BankAccount cuenta : cuentas) {
            List<CreditCard> tarjetasCuenta = creditCardService.findByBankAccountId(cuenta.getId());
            tarjetas.addAll(tarjetasCuenta);
        }


        List<BankMovement> movimientos = new ArrayList<>();
        for (CreditCard tarjeta : tarjetas) {
            List<BankMovement> movimientosTarjeta = bankMovementService.findAllByCreditCardId(tarjeta.getId());
            movimientos.addAll(movimientosTarjeta);
        }

        return Optional.of(new LoginResult(client, cuentas, tarjetas, movimientos));
    }
}
