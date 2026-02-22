package com.fctseek.controller;

import com.fctseek.dto.request.PlazaRequest;
import com.fctseek.dto.response.ApiResponse;
import com.fctseek.dto.response.PlazaResponse;
import com.fctseek.service.PlazaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controller para gestión de plazas FCT.
 */
@RestController
@RequestMapping("/api/plazas")
public class PlazaController {

    private final PlazaService plazaService;

    public PlazaController(PlazaService plazaService) {
        this.plazaService = plazaService;
    }

    /**
     * GET /api/plazas/{id}
     * Obtiene una plaza por ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<PlazaResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(plazaService.getById(id));
    }

    /**
     * GET /api/plazas/empresa/{empresaId}
     * Lista plazas de una empresa.
     */
    @GetMapping("/empresa/{empresaId}")
    public ResponseEntity<List<PlazaResponse>> getByEmpresa(@PathVariable Long empresaId) {
        return ResponseEntity.ok(plazaService.getByEmpresa(empresaId));
    }

    /**
     * GET /api/plazas/departamento/{departamentoId}
     * Lista plazas de un departamento.
     */
    @GetMapping("/departamento/{departamentoId}")
    public ResponseEntity<List<PlazaResponse>> getByDepartamento(@PathVariable Long departamentoId) {
        return ResponseEntity.ok(plazaService.getByDepartamento(departamentoId));
    }

    /**
     * GET /api/plazas/departamento/{departamentoId}/curso-academico/{cursoAcademico}
     * Lista plazas de un departamento en un curso académico.
     */
    @GetMapping("/departamento/{departamentoId}/curso-academico/{cursoAcademico}")
    public ResponseEntity<List<PlazaResponse>> getByDepartamentoAndCursoAcademico(
            @PathVariable Long departamentoId,
            @PathVariable String cursoAcademico) {
        return ResponseEntity.ok(plazaService.getByDepartamentoAndCursoAcademico(departamentoId, cursoAcademico));
    }

    /**
     * GET /api/plazas/disponibles/departamento/{departamentoId}/curso-academico/{cursoAcademico}
     * Lista plazas disponibles (con hueco) de un departamento.
     */
    @GetMapping("/disponibles/departamento/{departamentoId}/curso-academico/{cursoAcademico}")
    public ResponseEntity<List<PlazaResponse>> getDisponibles(
            @PathVariable Long departamentoId,
            @PathVariable String cursoAcademico) {
        return ResponseEntity.ok(plazaService.getDisponibles(departamentoId, cursoAcademico));
    }

    /**
     * POST /api/plazas
     * Crea una nueva plaza.
     */
    @PostMapping
    @PreAuthorize("hasRole('PROFESOR')")
    public ResponseEntity<PlazaResponse> create(@Valid @RequestBody PlazaRequest request) {
        PlazaResponse response = plazaService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * PUT /api/plazas/{id}/cantidad
     * Actualiza la cantidad de una plaza.
     */
    @PutMapping("/{id}/cantidad")
    @PreAuthorize("hasRole('PROFESOR')")
    public ResponseEntity<PlazaResponse> updateCantidad(
            @PathVariable Long id,
            @RequestBody Map<String, Integer> request) {
        Integer cantidad = request.get("cantidad");
        return ResponseEntity.ok(plazaService.updateCantidad(id, cantidad));
    }

    /**
     * DELETE /api/plazas/{id}
     * Elimina una plaza.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('PROFESOR')")
    public ResponseEntity<ApiResponse> delete(@PathVariable Long id) {
        plazaService.delete(id);
        return ResponseEntity.ok(ApiResponse.ok("Plaza eliminada correctamente"));
    }
}
