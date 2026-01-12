package org.example.bankback.domain.service;

import org.example.bankback.domain.models.BankAccount;
import org.example.bankback.domain.models.BankMovement;
import org.example.bankback.domain.models.Client;
import org.example.bankback.domain.models.CreditCard;

import java.util.List;
import java.util.Optional;

public interface LoginService {
    Optional<LoginResult> login(String username, String password, String apiToken);

    class LoginResult {
        private final Client client;
        private final List<BankAccount> cuentas;
        private final List<CreditCard> tarjetas;
        private final List<BankMovement> movimientos;

        public LoginResult(Client client, List<BankAccount> cuentas, List<CreditCard> tarjetas, List<BankMovement> movimientos) {
            this.client = client;
            this.cuentas = cuentas;
            this.tarjetas = tarjetas;
            this.movimientos = movimientos;
        }

        public Client getClient() {
            return client;
        }

        public List<BankAccount> getCuentas() {
            return cuentas;
        }

        public List<CreditCard> getTarjetas() {
            return tarjetas;
        }

        public List<BankMovement> getMovimientos() {
            return movimientos;
        }
    }
}

