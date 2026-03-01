package com.practicas.util;

import com.practicas.model.Usuario;

/**
 * Singleton que mantiene el estado de la sesión activa.
 */
public class Session {
    private static Session instance;

    private String token;
    private Usuario usuario;

    private Session() {}

    public static Session get() {
        if (instance == null) instance = new Session();
        return instance;
    }

    public void iniciar(String token, Usuario usuario) {
        this.token = token;
        this.usuario = usuario;
    }

    public void cerrar() {
        this.token = null;
        this.usuario = null;
    }

    public boolean isAuthenticated() {
        return token != null && !token.isEmpty();
    }

    public String getToken() { return token; }
    public Usuario getUsuario() { return usuario; }

    public boolean esProfesor() {
        return usuario != null && usuario.esProfesor();
    }
}
