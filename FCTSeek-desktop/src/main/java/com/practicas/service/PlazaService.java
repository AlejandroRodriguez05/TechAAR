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

    public static void crear(long empresaId, long departamentoId, int plazas, boolean esGeneral) throws ApiException {
        ApiClient.postRaw("/plazas", Map.of(
                "empresaId", empresaId,
                "departamentoId", departamentoId,
                "plazasOfertadas", plazas,
                "esGeneral", esGeneral
        ));
    }
}
