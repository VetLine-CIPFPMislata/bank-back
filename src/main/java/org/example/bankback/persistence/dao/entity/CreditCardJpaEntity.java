package org.example.bankback.persistence.dao.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "credit_cards")
public class CreditCardJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_credit_card")
    private Long id;

    @Column(name = "card_number", nullable = false, unique = true)
    private String numeroTarjeta;

    @Column(name = "expiry_date", nullable = false)
    private String fechaCaducidad;

    @Column(name = "cvc", nullable = false)
    private String cvc;

    @Column(name = "full_name", nullable = false)
    private String nombreCompleto;

    @Column(name = "bank_account_id")
    private Long idCuentaBancaria;

    public CreditCardJpaEntity() {
    }

    public CreditCardJpaEntity(Long id, String numeroTarjeta, String fechaCaducidad, String cvc, String nombreCompleto) {
        this.id = id;
        this.numeroTarjeta = numeroTarjeta;
        this.fechaCaducidad = fechaCaducidad;
        this.cvc = cvc;
        this.nombreCompleto = nombreCompleto;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumeroTarjeta() {
        return numeroTarjeta;
    }

    public void setNumeroTarjeta(String numeroTarjeta) {
        this.numeroTarjeta = numeroTarjeta;
    }

    public String getFechaCaducidad() {
        return fechaCaducidad;
    }

    public void setFechaCaducidad(String fechaCaducidad) {
        this.fechaCaducidad = fechaCaducidad;
    }

    public String getCvc() {
        return cvc;
    }

    public void setCvc(String cvc) {
        this.cvc = cvc;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    public Long getIdCuentaBancaria() {
        return idCuentaBancaria;
    }

    public void setIdCuentaBancaria(Long idCuentaBancaria) {
        this.idCuentaBancaria = idCuentaBancaria;
    }
}
