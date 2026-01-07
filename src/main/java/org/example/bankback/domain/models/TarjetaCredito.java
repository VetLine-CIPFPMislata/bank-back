package org.example.bankback.domain.models;

public class TarjetaCredito {
    private final Long id;
    private final String numeroTarjeta;
    private final String fechaCaducidad;
    private final String cvc;
    private final String nombreCompleto;

    public TarjetaCredito(Long id, String numeroTarjeta, String fechaCaducidad, String cvc, String nombreCompleto) {
        this.id = id;
        this.numeroTarjeta = numeroTarjeta;
        this.fechaCaducidad = fechaCaducidad;
        this.cvc = cvc;
        this.nombreCompleto = nombreCompleto;
    }

    public Long getId() {
        return id;
    }

    public String getNumeroTarjeta() {
        return numeroTarjeta;
    }

    public String getFechaCaducidad() {
        return fechaCaducidad;
    }

    public String getCvc() {
        return cvc;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }
}
