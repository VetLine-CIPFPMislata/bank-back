package org.example.bankback.domain.models;

import java.math.BigDecimal;

public class CuentaBancaria {
    private final Long id;
    private final String iban;
    private final BigDecimal saldo;

    public CuentaBancaria(Long id, String iban, BigDecimal saldo) {
        this.id = id;
        this.iban = iban;
        this.saldo = saldo;
    }

    public Long getId() {
        return id;
    }

    public String getIban() {
        return iban;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }
}
