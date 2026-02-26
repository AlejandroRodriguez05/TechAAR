package com.fctseek.service;

import com.fctseek.dto.response.DepartamentoResponse;
import com.fctseek.exception.ResourceNotFoundException;
import com.fctseek.model.Departamento;
import com.fctseek.repository.DepartamentoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio para gestión de departamentos.
 */
@Service
public class DepartamentoService {

    private final DepartamentoRepository departamentoRepository;

    public DepartamentoService(DepartamentoRepository departamentoRepository) {
        this.departamentoRepository = departamentoRepository;
    }

    /**
     * Lista todos los departamentos.
     */
    public List<DepartamentoResponse> getAll() {
        return departamentoRepository.findAll().stream()
                .map(DepartamentoResponse::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene un departamento por ID.
     */
    public DepartamentoResponse getById(Long id) {
        Departamento departamento = departamentoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Departamento", "id", id));
        return DepartamentoResponse.fromEntity(departamento);
    }

    /**
     * Obtiene un departamento por código.
     */
    public DepartamentoResponse getByCodigo(String codigo) {
        Departamento departamento = departamentoRepository.findByCodigo(codigo)
                .orElseThrow(() -> new ResourceNotFoundException("Departamento", "codigo", codigo));
        return DepartamentoResponse.fromEntity(departamento);
    }

    /**
     * Obtiene la entidad Departamento por ID (para uso interno).
     */
    public Departamento getEntityById(Long id) {
        return departamentoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Departamento", "id", id));
    }
}
