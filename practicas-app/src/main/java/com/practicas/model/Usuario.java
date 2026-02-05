package com.practicas.model;

/**
 * Clase que representa un usuario del sistema (alumno o profesor)
 */
public class Usuario {
    private String username;
    private String password;
    private TipoUsuario tipo;
    private String nombreCompleto;

    public Usuario(String username, String password, TipoUsuario tipo, String nombreCompleto) {
        this.username = username;
        this.password = password;
        this.tipo = tipo;
        this.nombreCompleto = nombreCompleto;
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
