package org.example.bankback.domain.models;

public class TarjetaCredito {
    Long id;
    String numeroTarjeta;
    String fechaCaducidad;
    String cvc;
    String nombreCompleto;

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
