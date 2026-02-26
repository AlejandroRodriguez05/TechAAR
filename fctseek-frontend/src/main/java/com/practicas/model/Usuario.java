package com.practicas.model;

/**
 * Clase que representa un usuario del sistema (alumno o profesor)
 */
public class Usuario {
    private String username;
    private String password;
    private TipoUsuario tipo;
    private String nombreCompleto;
    private String email;
    private String pais;
    private String dni;
    private String telefono;

    public Usuario(String username, String password, TipoUsuario tipo, String nombreCompleto) {
        this.username = username;
        this.password = password;
        this.tipo = tipo;
        this.nombreCompleto = nombreCompleto;
        // Valores por defecto
        this.email = username + "@gmail.com";
        this.pais = "España";
        this.dni = "";
        this.telefono = "";
    }

    public Usuario(String username, String password, TipoUsuario tipo, String nombreCompleto,
                   String email, String pais, String dni, String telefono) {
        this.username = username;
        this.password = password;
        this.tipo = tipo;
        this.nombreCompleto = nombreCompleto;
        this.email = email;
        this.pais = pais;
        this.dni = dni;
        this.telefono = telefono;
    }

    // Getters y Setters
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

    public TipoUsuario getTipo() {
        return tipo;
    }

    public void setTipo(TipoUsuario tipo) {
        this.tipo = tipo;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPais() {
        return pais;
    }

    public void setPais(String pais) {
        this.pais = pais;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public boolean esProfesor() {
        return this.tipo == TipoUsuario.PROFESOR;
    }

    public boolean esAlumno() {
        return this.tipo == TipoUsuario.ALUMNO;
    }

    @Override
    public String toString() {
        return nombreCompleto + " (" + tipo + ")";
    }
}