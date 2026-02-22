package com.fctseek.controller;

import com.fctseek.dto.request.ReservaRequest;
import com.fctseek.dto.response.ApiResponse;
import com.fctseek.dto.response.ReservaResponse;
import com.fctseek.service.ReservaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controller para gestión de reservas de plazas.
 */
@RestController
@RequestMapping("/api/reservas")
@PreAuthorize("hasRole('PROFESOR')")
public class ReservaController {

    private final ReservaService reservaService;

    public ReservaController(ReservaService reservaService) {
        this.reservaService = reservaService;
    }

    /**
     * GET /api/reservas/mis-reservas
     * Lista las reservas del profesor actual.
     */
    @GetMapping("/mis-reservas")
    public ResponseEntity<List<ReservaResponse>> getMisReservas() {
        return ResponseEntity.ok(reservaService.getMisReservas());
    }

    /**
     * GET /api/reservas/{id}
     * Obtiene una reserva por ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ReservaResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(reservaService.getById(id));
    }

    /**
     * GET /api/reservas/empresa/{empresaId}
     * Lista reservas de una empresa.
     */
    @GetMapping("/empresa/{empresaId}")
    public ResponseEntity<List<ReservaResponse>> getByEmpresa(@PathVariable Long empresaId) {
        return ResponseEntity.ok(reservaService.getByEmpresa(empresaId));
    }

    /**
     * GET /api/reservas/plaza/{plazaId}
     * Lista reservas de una plaza.
     */
    @GetMapping("/plaza/{plazaId}")
    public ResponseEntity<List<ReservaResponse>> getByPlaza(@PathVariable Long plazaId) {
        return ResponseEntity.ok(reservaService.getByPlaza(plazaId));
    }

    /**
     * POST /api/reservas
     * Crea una nueva reserva.
     */
    @PostMapping
    public ResponseEntity<ReservaResponse> create(@Valid @RequestBody ReservaRequest request) {
        ReservaResponse response = reservaService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * PUT /api/reservas/{id}/confirmar
     * Confirma una reserva.
     */
    @PutMapping("/{id}/confirmar")
    public ResponseEntity<ReservaResponse> confirmar(@PathVariable Long id) {
        return ResponseEntity.ok(reservaService.confirmar(id));
    }

    /**
     * PUT /api/reservas/{id}/cancelar
     * Cancela una reserva.
     */
    @PutMapping("/{id}/cancelar")
    public ResponseEntity<ReservaResponse> cancelar(@PathVariable Long id) {
        return ResponseEntity.ok(reservaService.cancelar(id));
    }

    /**
     * PUT /api/reservas/{id}/notas
     * Actualiza las notas de una reserva.
     */
    @PutMapping("/{id}/notas")
    public ResponseEntity<ReservaResponse> updateNotas(
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {
        String notas = request.get("notas");
        return ResponseEntity.ok(reservaService.updateNotas(id, notas));
    }

    /**
     * DELETE /api/reservas/{id}
     * Elimina una reserva.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> delete(@PathVariable Long id) {
        reservaService.delete(id);
        return ResponseEntity.ok(ApiResponse.ok("Reserva eliminada correctamente"));
    }
}
