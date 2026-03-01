package com.practicas.service;

import com.practicas.model.Lista;
import com.practicas.util.ApiClient;
import com.practicas.util.ApiClient.ApiException;

import java.util.List;
import java.util.Map;

public class ListaService {

    public static List<Lista> getMisListas() throws ApiException {
        return ApiClient.getList("/listas", Lista.class);
    }

    public static Lista getById(long id) throws ApiException {
        return ApiClient.get("/listas/" + id, Lista.class);
    }

    public static Lista crear(String nombre) throws ApiException {
        return ApiClient.post("/listas", Map.of("nombre", nombre), Lista.class);
    }

    public static void eliminar(long id) throws ApiException {
        ApiClient.delete("/listas/" + id);
    }

    public static void addEmpresa(long listaId, long empresaId) throws ApiException {
        ApiClient.postRaw("/listas/" + listaId + "/empresas", Map.of("empresaId", empresaId));
    }

    public static void removeEmpresa(long listaId, long empresaId) throws ApiException {
        ApiClient.delete("/listas/" + listaId + "/empresas/" + empresaId);
    }
}
