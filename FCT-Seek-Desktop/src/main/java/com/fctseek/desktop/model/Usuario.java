/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.fctseek.desktop.model;

import com.fctseek.desktop.enums.Rol;

/**
 *
 * @author AlejandroR
 */

public class Usuario {
    private Long id;
    private String nif;
    private String nombre;
    private String apellidos;
    private String email;
    private Rol rol;

    // Constructor vacio
    public Usuario() {
    }

    // Constructor con parametros
    public Usuario(Long id, String nif, String nombre, String apellidos, Rol rol) {
        this.id = id;
        this.nif = nif;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.rol = rol;
    }

    // Getters y Setters (NetBeans puede generarlos automaticamente)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNif() {
        return nif;
    }

    public void setNif(String nif) {
        this.nif = nif;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Rol getRol() {
        return rol;
    }

    public void setRol(Rol rol) {
        this.rol = rol;
    }

    // Metodo del nombre completo
    public String getNombreCompleto() {
        return nombre + " " + apellidos;
    }

    @Override
    public String toString() {
        return getNombreCompleto() + " (" + nif + ")";
    }
}