package org.example.bankback.persistence.dao.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "clients")
public class ClientJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name= "id_client")
    private Long id;

    @Column(name= "username", nullable = false)
    private String username;

    @Column(name= "password", nullable = false)
    private String password;

    @Column(name= "nombre", nullable = false)
    private String nombre;

    @Column(name= "apellido1", nullable = false)
    private String apellido1;

    @Column(name= "apellido2", nullable = false)
    private String apellido2;

    @Column(name= "dni", nullable = false, unique = true)
    private String dni;

    public ClientJpaEntity() {}

    public ClientJpaEntity(Long id, String username, String password, String nombre, String apellido1, String apellido2, String dni) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.nombre = nombre;
        this.apellido1 = apellido1;
        this.apellido2 = apellido2;
        this.dni = dni;
    }

    public ClientJpaEntity(String username, String password, String nombre, String apellido1, String apellido2, String dni) {
        this.username = username;
        this.password = password;
        this.nombre = nombre;
        this.apellido1 = apellido1;
        this.apellido2 = apellido2;
        this.dni = dni;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido1() {
        return apellido1;
    }

    public void setApellido1(String apellido1) {
        this.apellido1 = apellido1;
    }

    public String getApellido2() {
        return apellido2;
    }

    public void setApellido2(String apellido2) {
        this.apellido2 = apellido2;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }
}
