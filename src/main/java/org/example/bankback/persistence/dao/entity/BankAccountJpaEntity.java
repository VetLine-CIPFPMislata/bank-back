package org.example.bankback.persistence.dao.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "bank_accounts")
public class BankAccountJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name= "id_bank_account")
    private Long id;

    @Column(name = "saldo", nullable = false)
    private BigDecimal saldo;

    @Column(name = "iban", nullable = false, unique = true)
    private String iban;

    @Column(name = "client_id")
    private Long idCliente;

    public BankAccountJpaEntity() {

    }

    public BankAccountJpaEntity(Long id, BigDecimal saldo, String iban) {
        this.id = id;
        this.saldo = saldo;
        this.iban = iban;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }

    public void setSaldo(BigDecimal balance) {
        this.saldo = balance;
    }

    public String getIBAN() {
        return iban;
    }

    public void setIBAN(String iban) {
        this.iban = iban;
    }

    public Long getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(Long idCliente) {
        this.idCliente = idCliente;
    }
}
