 package com.fctseek.controller;

import com.fctseek.dto.request.EmpresaRequest;
import com.fctseek.dto.response.ApiResponse;
import com.fctseek.dto.response.EmpresaDetailResponse;
import com.fctseek.dto.response.EmpresaResponse;
import com.fctseek.service.EmpresaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller para gestión de empresas.
 */
@RestController
@RequestMapping("/api/empresas")
public class EmpresaController {

    private final EmpresaService empresaService;

    public EmpresaController(EmpresaService empresaService) {
        this.empresaService = empresaService;
    }

    /**
     * GET /api/empresas
     * Lista todas las empresas activas.
     */
    @GetMapping
    public ResponseEntity<List<EmpresaResponse>> getAll() {
        return ResponseEntity.ok(empresaService.getAll());
    }

    /**
     * GET /api/empresas/{id}
     * Obtiene el detalle completo de una empresa.
     */
    @GetMapping("/{id}")
    public ResponseEntity<EmpresaDetailResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(empresaService.getById(id));
    }

    /**
     * GET /api/empresas/buscar?q={texto}
     * Busca empresas por nombre o ciudad.
     */
    @GetMapping("/buscar")
    public ResponseEntity<List<EmpresaResponse>> search(@RequestParam(name = "q", required = false) String texto) {
        return ResponseEntity.ok(empresaService.search(texto));
    }

    /**
     * GET /api/empresas/ciudad/{ciudad}
     * Lista empresas de una ciudad.
     */
    @GetMapping("/ciudad/{ciudad}")
    public ResponseEntity<List<EmpresaResponse>> getByCiudad(@PathVariable String ciudad) {
        return ResponseEntity.ok(empresaService.getByCiudad(ciudad));
    }

    /**
     * GET /api/empresas/curso/{cursoId}
     * Lista empresas que aceptan un curso específico.
     */
    @GetMapping("/curso/{cursoId}")
    public ResponseEntity<List<EmpresaResponse>> getByCurso(@PathVariable Long cursoId) {
        return ResponseEntity.ok(empresaService.getByCurso(cursoId));
    }

    /**
     * GET /api/empresas/contactadas/departamento/{departamentoId}
     * Lista empresas contactadas por un departamento.
     */
    @GetMapping("/contactadas/departamento/{departamentoId}")
    public ResponseEntity<List<EmpresaResponse>> getContactadasByDepartamento(
            @PathVariable Long departamentoId) {
        return ResponseEntity.ok(empresaService.getContactadasByDepartamento(departamentoId));
    }

    /**
     * GET /api/empresas/favoritas
     * Lista empresas favoritas del usuario actual.
     */
    @GetMapping("/favoritas")
    public ResponseEntity<List<EmpresaResponse>> getFavoritas() {
        return ResponseEntity.ok(empresaService.getFavoritas());
    }

    /**
     * POST /api/empresas
     * Crea una nueva empresa.
     */
    @PostMapping
    @PreAuthorize("hasRole('PROFESOR')")
    public ResponseEntity<EmpresaResponse> create(@Valid @RequestBody EmpresaRequest request) {
        EmpresaResponse response = empresaService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * PUT /api/empresas/{id}
     * Actualiza una empresa existente.
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('PROFESOR')")
    public ResponseEntity<EmpresaResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody EmpresaRequest request) {
        return ResponseEntity.ok(empresaService.update(id, request));
    }

    /**
     * DELETE /api/empresas/{id}
     * Desactiva una empresa.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('PROFESOR')")
    public ResponseEntity<ApiResponse> delete(@PathVariable Long id) {
        empresaService.delete(id);
        return ResponseEntity.ok(ApiResponse.ok("Empresa desactivada correctamente"));
    }
}
