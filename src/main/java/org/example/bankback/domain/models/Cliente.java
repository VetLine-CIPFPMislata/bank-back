package org.example.bankback.domain.models;

public class Cliente {
    Long id;
    String username;
    String password;
    String nombre;
    String apellido1;
    String apellido2;
    String dni;
    String api_token;

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getNombre() {
        return nombre;
    }

    public String getApellido1() {
        return apellido1;
    }

    public String getApellido2() {
        return apellido2;
    }

    public String getDni() {
        return dni;
    }

    public String getApi_token() {
        return api_token;
    }

}
