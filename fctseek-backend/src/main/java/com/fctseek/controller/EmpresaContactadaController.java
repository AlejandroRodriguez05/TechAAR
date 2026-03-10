package com.fctseek.controller;

import com.fctseek.dto.request.EmpresaContactadaRequest;
import com.fctseek.dto.response.ApiResponse;
import com.fctseek.dto.response.EmpresaContactadaResponse;
import com.fctseek.service.EmpresaContactadaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller para gestión de empresas contactadas.
 */
@RestController
@RequestMapping("/api/empresas-contactadas")
public class EmpresaContactadaController {

    private final EmpresaContactadaService empresaContactadaService;

    public EmpresaContactadaController(EmpresaContactadaService empresaContactadaService) {
        this.empresaContactadaService = empresaContactadaService;
    }

    /**
     * GET /api/empresas-contactadas/empresa/{empresaId}
     * Lista los registros de contacto de una empresa.
     */
    @GetMapping("/empresa/{empresaId}")
    public ResponseEntity<List<EmpresaContactadaResponse>> getByEmpresa(@PathVariable Long empresaId) {
        return ResponseEntity.ok(empresaContactadaService.getByEmpresa(empresaId));
    }

    /**
     * POST /api/empresas-contactadas
     * Marca una empresa como contactada por un departamento.
     */
    @PostMapping
    @PreAuthorize("hasRole('PROFESOR')")
    public ResponseEntity<EmpresaContactadaResponse> create(@Valid @RequestBody EmpresaContactadaRequest request) {
        EmpresaContactadaResponse response = empresaContactadaService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * DELETE /api/empresas-contactadas/{id}
     * Elimina un registro de contacto.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('PROFESOR')")
    public ResponseEntity<ApiResponse> delete(@PathVariable Long id) {
        empresaContactadaService.delete(id);
        return ResponseEntity.ok(ApiResponse.ok("Registro de contacto eliminado"));
    }
}
