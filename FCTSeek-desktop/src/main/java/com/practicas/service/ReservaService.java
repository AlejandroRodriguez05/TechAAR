package com.practicas.service;

import com.practicas.util.ApiClient;
import com.practicas.util.ApiClient.ApiException;

import java.util.HashMap;
import java.util.Map;

public class ReservaService {

    public static void crear(long empresaId, long departamentoId, int cantidad,
                             Long cursoId, String cursoSiglas, String clase) throws ApiException {
        Map<String, Object> body = new HashMap<>();
        body.put("empresaId", empresaId);
        body.put("departamentoId", departamentoId);
        body.put("cantidad", cantidad);
        if (cursoId != null) body.put("cursoId", cursoId);
        if (cursoSiglas != null) body.put("cursoSiglas", cursoSiglas);
        if (clase != null && !clase.isBlank()) body.put("clase", clase);
        ApiClient.postRaw("/reservas", body);
    }
}
