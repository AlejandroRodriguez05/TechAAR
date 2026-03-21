package com.practicas.service;

import com.practicas.model.Reserva;
import com.practicas.util.ApiClient;
import com.practicas.util.ApiClient.ApiException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReservaService {

    public static List<Reserva> getByEmpresa(long empresaId) throws ApiException {
        return ApiClient.getList("/reservas/empresa/" + empresaId, Reserva.class);
    }

    public static void crear(long plazaId, long cursoId, int cantidad, String clase) throws ApiException {
        Map<String, Object> body = new HashMap<>();
        body.put("plazaId", plazaId);
        body.put("cursoId", cursoId);
        body.put("cantidad", cantidad);
        if (clase != null && !clase.isBlank()) body.put("clase", clase);
        ApiClient.postRaw("/reservas", body);
    }

    public static void eliminar(long id) throws ApiException {
        ApiClient.delete("/reservas/" + id);
    }
}