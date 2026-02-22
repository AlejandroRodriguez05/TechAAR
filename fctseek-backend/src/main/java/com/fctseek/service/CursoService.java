package com.fctseek.service;

import com.fctseek.dto.response.CursoResponse;
import com.fctseek.exception.ResourceNotFoundException;
import com.fctseek.model.Curso;
import com.fctseek.repository.CursoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio para gestión de cursos/ciclos formativos.
 */
@Service
public class CursoService {

    private final CursoRepository cursoRepository;

    public CursoService(CursoRepository cursoRepository) {
        this.cursoRepository = cursoRepository;
    }

    /**
     * Lista todos los cursos activos.
     */
    public List<CursoResponse> getAll() {
        return cursoRepository.findByActivoTrue().stream()
                .map(CursoResponse::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene un curso por ID.
     */
    public CursoResponse getById(Long id) {
        Curso curso = cursoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Curso", "id", id));
        return CursoResponse.fromEntity(curso);
    }

    /**
     * Lista cursos por departamento.
     */
    public List<CursoResponse> getByDepartamento(Long departamentoId) {
        return cursoRepository.findByDepartamentoIdAndActivoTrue(departamentoId).stream()
                .map(CursoResponse::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Lista cursos por grado (MEDIO, SUPERIOR, etc.).
     */
    public List<CursoResponse> getByGrado(String grado) {
        return cursoRepository.findByGradoAndActivoTrue(grado).stream()
                .map(CursoResponse::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Busca un curso por sus siglas.
     */
    public CursoResponse getBySiglas(String siglas) {
        Curso curso = cursoRepository.findBySiglas(siglas)
                .orElseThrow(() -> new ResourceNotFoundException("Curso", "siglas", siglas));
        return CursoResponse.fromEntity(curso);
    }

    /**
     * Lista cursos que acepta una empresa.
     */
    public List<CursoResponse> getByEmpresa(Long empresaId) {
        return cursoRepository.findByEmpresaId(empresaId).stream()
                .map(CursoResponse::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene la entidad Curso por ID (para uso interno).
     */
    public Curso getEntityById(Long id) {
        return cursoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Curso", "id", id));
    }
}
