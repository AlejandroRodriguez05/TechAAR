package com.fctseek.service;

import com.fctseek.dto.request.EmpresaContactadaRequest;
import com.fctseek.dto.response.EmpresaContactadaResponse;
import com.fctseek.exception.BadRequestException;
import com.fctseek.exception.ResourceNotFoundException;
import com.fctseek.model.*;
import com.fctseek.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio para gestión de empresas contactadas.
 */
@Service
public class EmpresaContactadaService {

    private final EmpresaContactadaRepository empresaContactadaRepository;
    private final EmpresaRepository empresaRepository;
    private final DepartamentoRepository departamentoRepository;
    private final UsuarioService usuarioService;

    public EmpresaContactadaService(EmpresaContactadaRepository empresaContactadaRepository,
                                     EmpresaRepository empresaRepository,
                                     DepartamentoRepository departamentoRepository,
                                     UsuarioService usuarioService) {
        this.empresaContactadaRepository = empresaContactadaRepository;
        this.empresaRepository = empresaRepository;
        this.departamentoRepository = departamentoRepository;
        this.usuarioService = usuarioService;
    }

    /**
     * Lista contactos de una empresa.
     */
    public List<EmpresaContactadaResponse> getByEmpresa(Long empresaId) {
        return empresaContactadaRepository.findByEmpresaId(empresaId).stream()
                .map(EmpresaContactadaResponse::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Marca una empresa como contactada por un departamento.
     */
    @Transactional
    public EmpresaContactadaResponse create(EmpresaContactadaRequest request) {
        Usuario currentUser = usuarioService.getCurrentUser();

        // Validar empresa
        Empresa empresa = empresaRepository.findById(request.getEmpresaId())
                .orElseThrow(() -> new ResourceNotFoundException("Empresa", "id", request.getEmpresaId()));

        // Validar departamento
        Departamento departamento = departamentoRepository.findById(request.getDepartamentoId())
                .orElseThrow(() -> new ResourceNotFoundException("Departamento", "id", request.getDepartamentoId()));

        // Verificar que no exista ya un contacto para esta empresa/departamento
        if (empresaContactadaRepository.existsByEmpresaIdAndDepartamentoId(
                request.getEmpresaId(), request.getDepartamentoId())) {
            throw new BadRequestException("Esta empresa ya fue contactada por este departamento");
        }

        // Crear registro de contacto
        EmpresaContactada contactada = new EmpresaContactada();
        contactada.setEmpresa(empresa);
        contactada.setDepartamento(departamento);
        contactada.setProfesor(currentUser);
        contactada.setFecha(LocalDate.now());
        contactada.setNotas(request.getNotas());

        contactada = empresaContactadaRepository.save(contactada);
        return EmpresaContactadaResponse.fromEntity(contactada);
    }

    /**
     * Elimina un registro de contacto.
     */
    @Transactional
    public void delete(Long id) {
        EmpresaContactada contactada = empresaContactadaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("EmpresaContactada", "id", id));
        empresaContactadaRepository.delete(contactada);
    }
}
