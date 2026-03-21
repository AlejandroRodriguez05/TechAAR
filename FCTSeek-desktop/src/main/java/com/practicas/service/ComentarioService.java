package com.practicas.service;

import com.practicas.model.Comentario;
import com.practicas.util.ApiClient;
import com.practicas.util.ApiClient.ApiException;

import java.util.List;
import java.util.Map;

public class ComentarioService {

    public static List<Comentario> getByEmpresa(long empresaId) throws ApiException {
        return ApiClient.getList("/comentarios/empresa/" + empresaId, Comentario.class);
    }

    public static Comentario crear(long empresaId, String texto, boolean esPrivado) throws ApiException {
        return ApiClient.post("/comentarios",
                Map.of("empresaId", empresaId, "texto", texto, "esPrivado", esPrivado),
                Comentario.class);
    }

    public static void eliminar(long id) throws ApiException {
        ApiClient.delete("/comentarios/" + id);
    }
}
