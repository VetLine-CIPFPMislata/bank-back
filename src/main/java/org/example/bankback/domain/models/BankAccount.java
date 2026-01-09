package org.example.bankback.domain.models;

import java.math.BigDecimal;

public class BankAccount {
    Long id;
    String iban;
    BigDecimal saldo;
    Long idCliente;

   public BankAccount(Long id, String iban, BigDecimal saldo) {
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

    public void setIban(String iban) {
        this.iban = iban;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }

    public void setSaldo(BigDecimal saldo) {
        this.saldo = saldo;
    }

    public Long getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(Long idCliente) {
        this.idCliente = idCliente;
    }
}
