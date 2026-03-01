package com.practicas.service;

import com.practicas.model.Curso;
import com.practicas.model.Departamento;
import com.practicas.util.ApiClient;
import com.practicas.util.ApiClient.ApiException;

import java.util.List;

public class DepartamentoService {

    public static List<Departamento> getAll() throws ApiException {
        return ApiClient.getList("/departamentos", Departamento.class);
    }

    public static List<Curso> getCursos(long departamentoId) throws ApiException {
        return ApiClient.getList("/departamentos/" + departamentoId + "/cursos", Curso.class);
    }

    public static List<Curso> getAllCursos() throws ApiException {
        return ApiClient.getList("/cursos", Curso.class);
    }
}
