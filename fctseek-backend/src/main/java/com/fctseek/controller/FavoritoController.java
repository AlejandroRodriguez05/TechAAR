package com.fctseek.controller;

import com.fctseek.dto.response.ApiResponse;
import com.fctseek.dto.response.EmpresaResponse;
import com.fctseek.service.FavoritoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller para gestión de empresas favoritas.
 */
@RestController
@RequestMapping("/api/favoritos")
public class FavoritoController {

    private final FavoritoService favoritoService;

    public FavoritoController(FavoritoService favoritoService) {
        this.favoritoService = favoritoService;
    }

    /**
     * GET /api/favoritos
     * Lista las empresas favoritas del usuario actual.
     */
    @GetMapping
    public ResponseEntity<List<EmpresaResponse>> getMisFavoritos() {
        return ResponseEntity.ok(favoritoService.getMisFavoritos());
    }

    /**
     * GET /api/favoritos/empresa/{empresaId}
     * Verifica si una empresa es favorita del usuario actual.
     */
    @GetMapping("/empresa/{empresaId}")
    public ResponseEntity<Map<String, Boolean>> isFavorita(@PathVariable Long empresaId) {
        Map<String, Boolean> response = new HashMap<>();
        response.put("esFavorita", favoritoService.isFavorita(empresaId));
        return ResponseEntity.ok(response);
    }

    /**
     * POST /api/favoritos/empresa/{empresaId}
     * Añade una empresa a favoritos.
     */
    @PostMapping("/empresa/{empresaId}")
    public ResponseEntity<ApiResponse> addFavorito(@PathVariable Long empresaId) {
        favoritoService.addFavorito(empresaId);
        return ResponseEntity.ok(ApiResponse.ok("Empresa añadida a favoritos"));
    }

    /**
     * DELETE /api/favoritos/empresa/{empresaId}
     * Elimina una empresa de favoritos.
     */
    @DeleteMapping("/empresa/{empresaId}")
    public ResponseEntity<ApiResponse> removeFavorito(@PathVariable Long empresaId) {
        favoritoService.removeFavorito(empresaId);
        return ResponseEntity.ok(ApiResponse.ok("Empresa eliminada de favoritos"));
    }

    /**
     * POST /api/favoritos/empresa/{empresaId}/toggle
     * Alterna el estado de favorito de una empresa.
     */
    @PostMapping("/empresa/{empresaId}/toggle")
    public ResponseEntity<Map<String, Boolean>> toggleFavorito(@PathVariable Long empresaId) {
        boolean esFavorita = favoritoService.toggleFavorito(empresaId);
        Map<String, Boolean> response = new HashMap<>();
        response.put("esFavorita", esFavorita);
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/favoritos/count
     * Cuenta las empresas favoritas del usuario actual.
     */
    @GetMapping("/count")
    public ResponseEntity<Map<String, Long>> countMisFavoritos() {
        Map<String, Long> response = new HashMap<>();
        response.put("total", favoritoService.countMisFavoritos());
        return ResponseEntity.ok(response);
    }
}
