package org.example.bankback.domain.models;

public class Cliente {
    private final Long id;
    private final String username;
    private final String password;
    private final String nombre;
    private final String apellido1;
    private final String apellido2;
    private final String dni;
    private final String api_token;

    public Cliente(Long id, String username, String password, String nombre, String apellido1, String apellido2, String dni, String api_token) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.nombre = nombre;
        this.apellido1 = apellido1;
        this.apellido2 = apellido2;
        this.dni = dni;
        this.api_token = api_token;
    }

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
