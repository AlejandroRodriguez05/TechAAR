package com.fctseek.service;

import com.fctseek.dto.request.PlazaRequest;
import com.fctseek.dto.response.PlazaResponse;
import com.fctseek.exception.BadRequestException;
import com.fctseek.exception.ResourceNotFoundException;
import com.fctseek.model.*;
import com.fctseek.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio para gestión de plazas FCT.
 */
@Service
public class PlazaService {

    private final PlazaRepository plazaRepository;
    private final EmpresaRepository empresaRepository;
    private final DepartamentoRepository departamentoRepository;
    private final CursoRepository cursoRepository;
    private final UsuarioService usuarioService;

    public PlazaService(PlazaRepository plazaRepository,
                       EmpresaRepository empresaRepository,
                       DepartamentoRepository departamentoRepository,
                       CursoRepository cursoRepository,
                       UsuarioService usuarioService) {
        this.plazaRepository = plazaRepository;
        this.empresaRepository = empresaRepository;
        this.departamentoRepository = departamentoRepository;
        this.cursoRepository = cursoRepository;
        this.usuarioService = usuarioService;
    }

    /**
     * Lista plazas de una empresa.
     */
    public List<PlazaResponse> getByEmpresa(Long empresaId) {
        return plazaRepository.findByEmpresaId(empresaId).stream()
                .map(PlazaResponse::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Lista plazas de un departamento.
     */
    public List<PlazaResponse> getByDepartamento(Long departamentoId) {
        return plazaRepository.findByDepartamentoId(departamentoId).stream()
                .map(PlazaResponse::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Lista plazas de un departamento en un curso académico.
     */
    public List<PlazaResponse> getByDepartamentoAndCursoAcademico(Long departamentoId, String cursoAcademico) {
        return plazaRepository.findByDepartamentoIdAndCursoAcademico(departamentoId, cursoAcademico).stream()
                .map(PlazaResponse::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Lista plazas disponibles (con hueco) de un departamento.
     */
    public List<PlazaResponse> getDisponibles(Long departamentoId, String cursoAcademico) {
        return plazaRepository.findPlazasDisponibles(departamentoId, cursoAcademico).stream()
                .map(PlazaResponse::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene una plaza por ID.
     */
    public PlazaResponse getById(Long id) {
        Plaza plaza = plazaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Plaza", "id", id));
        return PlazaResponse.fromEntity(plaza);
    }

    /**
     * Crea una nueva plaza.
     */
    @Transactional
    public PlazaResponse create(PlazaRequest request) {
        // Validar empresa
        Empresa empresa = empresaRepository.findById(request.getEmpresaId())
                .orElseThrow(() -> new ResourceNotFoundException("Empresa", "id", request.getEmpresaId()));

        // Validar departamento
        Departamento departamento = departamentoRepository.findById(request.getDepartamentoId())
                .orElseThrow(() -> new ResourceNotFoundException("Departamento", "id", request.getDepartamentoId()));

        // Validar curso si no es general
        Curso curso = null;
        if (request.getCursoId() != null) {
            curso = cursoRepository.findById(request.getCursoId())
                    .orElseThrow(() -> new ResourceNotFoundException("Curso", "id", request.getCursoId()));
        }

        // Verificar que no exista ya una plaza igual
        if (plazaRepository.findByEmpresaIdAndDepartamentoIdAndCursoIdAndCursoAcademico(
                request.getEmpresaId(), request.getDepartamentoId(), 
                request.getCursoId(), request.getCursoAcademico()).isPresent()) {
            throw new BadRequestException("Ya existe una plaza para esta combinación empresa/departamento/curso/año");
        }

        Usuario currentUser = usuarioService.getCurrentUser();

        Plaza plaza = new Plaza();
        plaza.setEmpresa(empresa);
        plaza.setDepartamento(departamento);
        plaza.setCurso(curso);
        plaza.setCantidad(request.getCantidad());
        plaza.setEsGeneral(curso == null);
        plaza.setCursoAcademico(request.getCursoAcademico());
        plaza.setCreatedBy(currentUser);

        plaza = plazaRepository.save(plaza);
        return PlazaResponse.fromEntity(plaza);
    }

    /**
     * Actualiza la cantidad de una plaza.
     */
    @Transactional
    public PlazaResponse updateCantidad(Long id, Integer cantidad) {
        Plaza plaza = plazaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Plaza", "id", id));
        
        if (cantidad < 1) {
            throw new BadRequestException("La cantidad mínima es 1");
        }
        
        // Verificar que no se reduzca por debajo de las reservadas
        int reservadas = plaza.getPlazasReservadas();
        if (cantidad < reservadas) {
            throw new BadRequestException("No se puede reducir a " + cantidad + " porque hay " + reservadas + " plazas reservadas");
        }
        
        plaza.setCantidad(cantidad);
        plaza = plazaRepository.save(plaza);
        return PlazaResponse.fromEntity(plaza);
    }

    /**
     * Elimina una plaza.
     */
    @Transactional
    public void delete(Long id) {
        Plaza plaza = plazaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Plaza", "id", id));
        
        if (plaza.getPlazasReservadas() > 0) {
            throw new BadRequestException("No se puede eliminar una plaza con reservas activas");
        }
        
        plazaRepository.delete(plaza);
    }

    /**
     * Obtiene la entidad Plaza por ID (para uso interno).
     */
    public Plaza getEntityById(Long id) {
        return plazaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Plaza", "id", id));
    }
}
