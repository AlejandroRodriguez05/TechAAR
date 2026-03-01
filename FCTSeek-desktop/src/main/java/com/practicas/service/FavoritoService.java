package com.practicas.service;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.practicas.model.Empresa;
import com.practicas.util.ApiClient;
import com.practicas.util.ApiClient.ApiException;

import java.util.List;

public class FavoritoService {

    public static List<Empresa> getMisFavoritos() throws ApiException {
        return ApiClient.getList("/favoritos", Empresa.class);
    }

    /**
     * Toggle favorito. Devuelve true si ahora es favorito, false si se quitó.
     */
    public static boolean toggle(long empresaId) throws ApiException {
        String json = ApiClient.postRaw("/favoritos/empresa/" + empresaId + "/toggle", null);
        try {
            JsonObject obj = JsonParser.parseString(json).getAsJsonObject();
            return obj.has("favorito") && obj.get("favorito").getAsBoolean();
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isFavorito(long empresaId) throws ApiException {
        String json = ApiClient.postRaw("/favoritos/empresa/" + empresaId + "/check", null);
        try {
            JsonObject obj = JsonParser.parseString(json).getAsJsonObject();
            return obj.has("favorito") && obj.get("favorito").getAsBoolean();
        } catch (Exception e) {
            return false;
        }
    }
}
