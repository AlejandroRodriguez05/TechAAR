package com.practicas.service;

import com.practicas.model.Plaza;
import com.practicas.util.ApiClient;
import com.practicas.util.ApiClient.ApiException;

import java.util.List;
import java.util.Map;

public class PlazaService {

    public static List<Plaza> getByEmpresa(long empresaId) throws ApiException {
        return ApiClient.getList("/plazas/empresa/" + empresaId, Plaza.class);
    }

    public static void crearConRequest(Map<String, Object> request) throws ApiException {
        ApiClient.postRaw("/plazas", request);
    }

    public static void eliminar(long id) throws ApiException {
        ApiClient.delete("/plazas/" + id);
    }
}