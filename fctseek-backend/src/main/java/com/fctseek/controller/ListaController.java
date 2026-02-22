package com.fctseek.controller;

import com.fctseek.dto.request.ListaRequest;
import com.fctseek.dto.response.ApiResponse;
import com.fctseek.dto.response.ListaResponse;
import com.fctseek.service.ListaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controller para gestión de listas personalizadas de empresas.
 */
@RestController
@RequestMapping("/api/listas")
public class ListaController {

    private final ListaService listaService;

    public ListaController(ListaService listaService) {
        this.listaService = listaService;
    }

    /**
     * GET /api/listas
     * Lista todas las listas del usuario actual.
     */
    @GetMapping
    public ResponseEntity<List<ListaResponse>> getMisListas() {
        return ResponseEntity.ok(listaService.getMisListas());
    }

    /**
     * GET /api/listas/{id}
     * Obtiene una lista con sus empresas.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ListaResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(listaService.getById(id));
    }

    /**
     * GET /api/listas/con-empresa/{empresaId}
     * Obtiene las listas del usuario que contienen una empresa.
     */
    @GetMapping("/con-empresa/{empresaId}")
    public ResponseEntity<List<ListaResponse>> getListasConEmpresa(@PathVariable Long empresaId) {
        return ResponseEntity.ok(listaService.getListasConEmpresa(empresaId));
    }

    /**
     * POST /api/listas
     * Crea una nueva lista.
     */
    @PostMapping
    public ResponseEntity<ListaResponse> create(@Valid @RequestBody ListaRequest request) {
        ListaResponse response = listaService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * PUT /api/listas/{id}
     * Actualiza una lista.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ListaResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody ListaRequest request) {
        return ResponseEntity.ok(listaService.update(id, request));
    }

    /**
     * DELETE /api/listas/{id}
     * Elimina una lista.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> delete(@PathVariable Long id) {
        listaService.delete(id);
        return ResponseEntity.ok(ApiResponse.ok("Lista eliminada correctamente"));
    }

    /**
     * POST /api/listas/{listaId}/empresas/{empresaId}
     * Añade una empresa a una lista.
     */
    @PostMapping("/{listaId}/empresas/{empresaId}")
    public ResponseEntity<ApiResponse> addEmpresa(
            @PathVariable Long listaId,
            @PathVariable Long empresaId,
            @RequestBody(required = false) Map<String, String> request) {
        String notas = request != null ? request.get("notas") : null;
        listaService.addEmpresa(listaId, empresaId, notas);
        return ResponseEntity.ok(ApiResponse.ok("Empresa añadida a la lista"));
    }

    /**
     * DELETE /api/listas/{listaId}/empresas/{empresaId}
     * Elimina una empresa de una lista.
     */
    @DeleteMapping("/{listaId}/empresas/{empresaId}")
    public ResponseEntity<ApiResponse> removeEmpresa(
            @PathVariable Long listaId,
            @PathVariable Long empresaId) {
        listaService.removeEmpresa(listaId, empresaId);
        return ResponseEntity.ok(ApiResponse.ok("Empresa eliminada de la lista"));
    }

    /**
     * PUT /api/listas/{listaId}/empresas/{empresaId}/notas
     * Actualiza las notas de una empresa en una lista.
     */
    @PutMapping("/{listaId}/empresas/{empresaId}/notas")
    public ResponseEntity<ApiResponse> updateNotasEmpresa(
            @PathVariable Long listaId,
            @PathVariable Long empresaId,
            @RequestBody Map<String, String> request) {
        String notas = request.get("notas");
        listaService.updateNotasEmpresa(listaId, empresaId, notas);
        return ResponseEntity.ok(ApiResponse.ok("Notas actualizadas correctamente"));
    }
}
