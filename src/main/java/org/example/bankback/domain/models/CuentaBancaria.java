package org.example.bankback.domain.models;

import java.math.BigDecimal;

public class CuentaBancaria {
    Long id;
    String Iban;
    BigDecimal saldo;

    public Long getId() {
        return id;
    }

    public String getIban() {
        return Iban;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }
}
