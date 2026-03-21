package com.practicas.service;

import com.practicas.util.ApiClient;
import com.practicas.util.ApiClient.ApiException;

import java.util.Map;

public class ValoracionService {

    public static void crear(long empresaId, int puntuacion) throws ApiException {
        ApiClient.postRaw("/valoraciones", Map.of("empresaId", empresaId, "puntuacion", puntuacion));
    }
}
