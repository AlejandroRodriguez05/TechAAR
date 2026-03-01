package com.practicas.service;

import com.practicas.model.Empresa;
import com.practicas.util.ApiClient;
import com.practicas.util.ApiClient.ApiException;

import java.util.List;

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

    public static void eliminar(long id) throws ApiException {
        ApiClient.delete("/empresas/" + id);
    }

    private static String encode(String s) {
        return java.net.URLEncoder.encode(s, java.nio.charset.StandardCharsets.UTF_8);
    }
}
