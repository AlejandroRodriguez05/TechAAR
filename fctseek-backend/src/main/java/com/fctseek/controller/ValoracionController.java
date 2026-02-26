package com.fctseek.controller;

import com.fctseek.dto.request.ValoracionRequest;
import com.fctseek.dto.response.ApiResponse;
import com.fctseek.dto.response.ValoracionResponse;
import com.fctseek.service.ValoracionService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller para gestión de valoraciones de empresas.
 */
@RestController
@RequestMapping("/api/valoraciones")
public class ValoracionController {

    private final ValoracionService valoracionService;

    public ValoracionController(ValoracionService valoracionService) {
        this.valoracionService = valoracionService;
    }

    /**
     * GET /api/valoraciones/empresa/{empresaId}
     * Lista valoraciones de una empresa.
     */
    @GetMapping("/empresa/{empresaId}")
    public ResponseEntity<List<ValoracionResponse>> getByEmpresa(@PathVariable Long empresaId) {
        return ResponseEntity.ok(valoracionService.getByEmpresa(empresaId));
    }

    /**
     * GET /api/valoraciones/empresa/{empresaId}/mi-valoracion
     * Obtiene la valoración del usuario actual para una empresa.
     */
    @GetMapping("/empresa/{empresaId}/mi-valoracion")
    public ResponseEntity<ValoracionResponse> getMiValoracion(@PathVariable Long empresaId) {
        ValoracionResponse response = valoracionService.getMiValoracion(empresaId);
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/valoraciones/empresa/{empresaId}/estadisticas
     * Obtiene estadísticas de valoraciones de una empresa.
     */
    @GetMapping("/empresa/{empresaId}/estadisticas")
    public ResponseEntity<Map<String, Object>> getEstadisticas(@PathVariable Long empresaId) {
        Map<String, Object> stats = new HashMap<>();
        stats.put("media", valoracionService.getValoracionMedia(empresaId));
        stats.put("total", valoracionService.countByEmpresa(empresaId));
        return ResponseEntity.ok(stats);
    }

    /**
     * POST /api/valoraciones
     * Crea o actualiza una valoración.
     */
    @PostMapping
    public ResponseEntity<ValoracionResponse> valorar(@Valid @RequestBody ValoracionRequest request) {
        return ResponseEntity.ok(valoracionService.valorar(request));
    }

    /**
     * DELETE /api/valoraciones/empresa/{empresaId}
     * Elimina la valoración del usuario actual para una empresa.
     */
    @DeleteMapping("/empresa/{empresaId}")
    public ResponseEntity<ApiResponse> deleteMyValoracion(@PathVariable Long empresaId) {
        valoracionService.deleteMyValoracion(empresaId);
        return ResponseEntity.ok(ApiResponse.ok("Valoración eliminada correctamente"));
    }
}
