package com.practicas.service;

import com.practicas.model.Empresa;
import com.practicas.util.ApiClient;
import com.practicas.util.ApiClient.ApiException;
import com.google.gson.JsonParser;

import java.util.List;
import java.util.Map;

public class EmpresaService {

    public static List<Empresa> getAll() throws ApiException {
        return ApiClient.getList("/empresas", Empresa.class);
    }

    public static Empresa getById(long id) throws ApiException {
        return ApiClient.get("/empresas/" + id, Empresa.class);
    }

    public static List<Empresa> buscar(String query) throws ApiException {
        return ApiClient.getList("/empresas/buscar?q=" + encode(query), Empresa.class);
    }

    public static Empresa crear(Empresa empresa) throws ApiException {
        return ApiClient.post("/empresas", empresa, Empresa.class);
    }

    public static Empresa actualizar(long id, Empresa empresa) throws ApiException {
        return ApiClient.put("/empresas/" + id, empresa, Empresa.class);
    }

    /** Crea empresa enviando un Map (incluye cursosIds). Devuelve el ID asignado. */
    public static long crearConRequest(Map<String, Object> request) throws ApiException {
        String json = ApiClient.postRaw("/empresas", request);
        return JsonParser.parseString(json).getAsJsonObject().get("id").getAsLong();
    }

    /** Actualiza empresa enviando un Map (incluye cursosIds). */
    public static void actualizarConRequest(long id, Map<String, Object> request) throws ApiException {
        ApiClient.put("/empresas/" + id, request, Object.class);
    }

    public static void eliminar(long id) throws ApiException {
        ApiClient.delete("/empresas/" + id);
    }

    private static String encode(String s) {
        return java.net.URLEncoder.encode(s, java.nio.charset.StandardCharsets.UTF_8);
    }
}
