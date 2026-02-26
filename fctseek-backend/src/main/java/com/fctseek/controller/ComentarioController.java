package com.fctseek.controller;

import com.fctseek.dto.request.ComentarioRequest;
import com.fctseek.dto.response.ApiResponse;
import com.fctseek.dto.response.ComentarioResponse;
import com.fctseek.service.ComentarioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controller para gestión de comentarios sobre empresas.
 */
@RestController
@RequestMapping("/api/comentarios")
public class ComentarioController {

    private final ComentarioService comentarioService;

    public ComentarioController(ComentarioService comentarioService) {
        this.comentarioService = comentarioService;
    }

    /**
     * GET /api/comentarios/empresa/{empresaId}
     * Lista comentarios de una empresa.
     * Los alumnos solo ven públicos, los profesores ven todos.
     */
    @GetMapping("/empresa/{empresaId}")
    public ResponseEntity<List<ComentarioResponse>> getByEmpresa(@PathVariable Long empresaId) {
        return ResponseEntity.ok(comentarioService.getByEmpresa(empresaId));
    }

    /**
     * GET /api/comentarios/{id}
     * Obtiene un comentario por ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ComentarioResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(comentarioService.getById(id));
    }

    /**
     * POST /api/comentarios
     * Crea un nuevo comentario.
     */
    @PostMapping
    public ResponseEntity<ComentarioResponse> create(@Valid @RequestBody ComentarioRequest request) {
        ComentarioResponse response = comentarioService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * PUT /api/comentarios/{id}
     * Actualiza un comentario (solo el autor puede hacerlo).
     */
    @PutMapping("/{id}")
    public ResponseEntity<ComentarioResponse> update(
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {
        String texto = request.get("texto");
        return ResponseEntity.ok(comentarioService.update(id, texto));
    }

    /**
     * DELETE /api/comentarios/{id}
     * Elimina un comentario (solo el autor puede hacerlo).
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> delete(@PathVariable Long id) {
        comentarioService.delete(id);
        return ResponseEntity.ok(ApiResponse.ok("Comentario eliminado correctamente"));
    }
}
