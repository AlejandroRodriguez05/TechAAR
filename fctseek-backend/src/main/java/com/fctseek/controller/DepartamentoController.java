package com.fctseek.controller;

import com.fctseek.dto.response.DepartamentoResponse;
import com.fctseek.service.DepartamentoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller para gestión de departamentos.
 */
@RestController
@RequestMapping("/api/departamentos")
public class DepartamentoController {

    private final DepartamentoService departamentoService;

    public DepartamentoController(DepartamentoService departamentoService) {
        this.departamentoService = departamentoService;
    }

    /**
     * GET /api/departamentos
     * Lista todos los departamentos.
     */
    @GetMapping
    public ResponseEntity<List<DepartamentoResponse>> getAll() {
        return ResponseEntity.ok(departamentoService.getAll());
    }

    /**
     * GET /api/departamentos/{id}
     * Obtiene un departamento por ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<DepartamentoResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(departamentoService.getById(id));
    }

    /**
     * GET /api/departamentos/codigo/{codigo}
     * Obtiene un departamento por código.
     */
    @GetMapping("/codigo/{codigo}")
    public ResponseEntity<DepartamentoResponse> getByCodigo(@PathVariable String codigo) {
        return ResponseEntity.ok(departamentoService.getByCodigo(codigo));
    }
}
