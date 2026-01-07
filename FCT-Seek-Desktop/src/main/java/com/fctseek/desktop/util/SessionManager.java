/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.fctseek.desktop.util;

import com.fctseek.desktop.model.Usuario;

/**
 *
 * @author AlejandroR
 */
public class SessionManager {

    private static SessionManager instance;
    private Usuario usuarioActual;
    private String token; // Token JWT para autenticacion

    // Constructor
    private SessionManager() {
    }

    // Obtener una unica instancia
    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    // Iniciar sesion
    public void login(Usuario usuario, String token) {
        this.usuarioActual = usuario;
        this.token = token;
    }

    // Cerrar sesion
    public void logout() {
        this.usuarioActual = null;
        this.token = null;
    }

    // Verificar si hay sesion activa
    public boolean isLoggedIn() {
        return usuarioActual != null && token != null;
    }

    // Getters
    public Usuario getUsuarioActual() {
        return usuarioActual;
    }

    public String getToken() {
        return token;
    }
}
