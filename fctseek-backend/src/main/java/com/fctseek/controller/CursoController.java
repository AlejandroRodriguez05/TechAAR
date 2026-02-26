package com.fctseek.controller;

import com.fctseek.dto.response.CursoResponse;
import com.fctseek.service.CursoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller para gestión de cursos/ciclos formativos.
 */
@RestController
@RequestMapping("/api/cursos")
public class CursoController {

    private final CursoService cursoService;

    public CursoController(CursoService cursoService) {
        this.cursoService = cursoService;
    }

    /**
     * GET /api/cursos
     * Lista todos los cursos activos.
     */
    @GetMapping
    public ResponseEntity<List<CursoResponse>> getAll() {
        return ResponseEntity.ok(cursoService.getAll());
    }

    /**
     * GET /api/cursos/{id}
     * Obtiene un curso por ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<CursoResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(cursoService.getById(id));
    }

    /**
     * GET /api/cursos/departamento/{departamentoId}
     * Lista cursos de un departamento.
     */
    @GetMapping("/departamento/{departamentoId}")
    public ResponseEntity<List<CursoResponse>> getByDepartamento(@PathVariable Long departamentoId) {
        return ResponseEntity.ok(cursoService.getByDepartamento(departamentoId));
    }

    /**
     * GET /api/cursos/grado/{grado}
     * Lista cursos por grado (MEDIO, SUPERIOR, etc.).
     */
    @GetMapping("/grado/{grado}")
    public ResponseEntity<List<CursoResponse>> getByGrado(@PathVariable String grado) {
        return ResponseEntity.ok(cursoService.getByGrado(grado.toUpperCase()));
    }

    /**
     * GET /api/cursos/siglas/{siglas}
     * Busca un curso por sus siglas.
     */
    @GetMapping("/siglas/{siglas}")
    public ResponseEntity<CursoResponse> getBySiglas(@PathVariable String siglas) {
        return ResponseEntity.ok(cursoService.getBySiglas(siglas.toUpperCase()));
    }

    /**
     * GET /api/cursos/empresa/{empresaId}
     * Lista cursos que acepta una empresa.
     */
    @GetMapping("/empresa/{empresaId}")
    public ResponseEntity<List<CursoResponse>> getByEmpresa(@PathVariable Long empresaId) {
        return ResponseEntity.ok(cursoService.getByEmpresa(empresaId));
    }
}
