package com.practicas.service;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.practicas.model.Usuario;
import com.practicas.util.ApiClient;
import com.practicas.util.ApiClient.ApiException;
import com.practicas.util.Session;

import java.util.Map;

public class AuthService {

    /**
     * Login: envía email+password, parsea token+usuario de la respuesta.
     * Soporta múltiples formatos de respuesta del backend.
     */
    public static Usuario login(String email, String password) throws ApiException {
        String json = ApiClient.postPublic("/auth/login", Map.of("email", email, "password", password));

        JsonObject root = JsonParser.parseString(json).getAsJsonObject();

        // Extraer token
        String token = null;
        for (String key : new String[]{"token", "accessToken", "jwt"}) {
            if (root.has(key) && !root.get(key).isJsonNull()) {
                token = root.get(key).getAsString();
                break;
            }
        }
        if (token == null || token.isBlank()) {
            throw new ApiException("No se recibió token del servidor", 0);
        }

        // Extraer usuario
        Usuario usuario;
        if (root.has("usuario") && root.get("usuario").isJsonObject()) {
            usuario = ApiClient.gson().fromJson(root.get("usuario"), Usuario.class);
        } else if (root.has("user") && root.get("user").isJsonObject()) {
            usuario = ApiClient.gson().fromJson(root.get("user"), Usuario.class);
        } else {
            // Campos a nivel raíz
            usuario = ApiClient.gson().fromJson(root, Usuario.class);
        }

        // Guardar sesión
        Session.get().iniciar(token, usuario);

        // Si faltan datos, intentar cargar desde /auth/me
        if (usuario.getNombre() == null || usuario.getNombre().isBlank()) {
            try {
                usuario = ApiClient.get("/auth/me", Usuario.class);
                Session.get().iniciar(token, usuario);
            } catch (Exception ignored) {}
        }

        return usuario;
    }

    /**
     * Obtener datos del usuario autenticado.
     */
    public static Usuario me() throws ApiException {
        return ApiClient.get("/auth/me", Usuario.class);
    }

    /**
     * Cerrar sesión.
     */
    public static void logout() {
        Session.get().cerrar();
    }
}
