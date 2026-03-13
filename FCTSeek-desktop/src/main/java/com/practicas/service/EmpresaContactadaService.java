package com.practicas.service;

import com.practicas.model.EmpresaContactada;
import com.practicas.util.ApiClient;
import com.practicas.util.ApiClient.ApiException;

import java.util.List;
import java.util.Map;

public class EmpresaContactadaService {

    public static void crear(long empresaId, long departamentoId) throws ApiException {
        ApiClient.postRaw("/empresas-contactadas",
                Map.of("empresaId", empresaId, "departamentoId", departamentoId));
    }

    public static List<EmpresaContactada> getByEmpresa(long empresaId) throws ApiException {
        return ApiClient.getList("/empresas-contactadas/empresa/" + empresaId, EmpresaContactada.class);
    }
}
