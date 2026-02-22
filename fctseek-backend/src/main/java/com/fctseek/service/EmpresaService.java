package com.fctseek.service;

import com.fctseek.dto.request.EmpresaRequest;
import com.fctseek.dto.response.EmpresaDetailResponse;
import com.fctseek.dto.response.EmpresaResponse;
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
 * Servicio para gestión de empresas.
 */
@Service
public class EmpresaService {

    private final EmpresaRepository empresaRepository;
    private final CursoRepository cursoRepository;
    private final EmpresaCursoRepository empresaCursoRepository;
    private final ValoracionRepository valoracionRepository;
    private final FavoritoRepository favoritoRepository;
    private final PlazaRepository plazaRepository;
    private final UsuarioService usuarioService;

    public EmpresaService(EmpresaRepository empresaRepository,
                         CursoRepository cursoRepository,
                         EmpresaCursoRepository empresaCursoRepository,
                         ValoracionRepository valoracionRepository,
                         FavoritoRepository favoritoRepository,
                         PlazaRepository plazaRepository,
                         UsuarioService usuarioService) {
        this.empresaRepository = empresaRepository;
        this.cursoRepository = cursoRepository;
        this.empresaCursoRepository = empresaCursoRepository;
        this.valoracionRepository = valoracionRepository;
        this.favoritoRepository = favoritoRepository;
        this.plazaRepository = plazaRepository;
        this.usuarioService = usuarioService;
    }

    /**
     * Lista todas las empresas activas.
     */
    public List<EmpresaResponse> getAll() {
        return empresaRepository.findByActivaTrue().stream()
                .map(this::toResponseWithValoracion)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene el detalle completo de una empresa.
     */
    public EmpresaDetailResponse getById(Long id) {
        Empresa empresa = empresaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Empresa", "id", id));
        
        EmpresaDetailResponse response = EmpresaDetailResponse.fromEntity(empresa);
        
        // Añadir valoraciones
        Double valoracionMedia = valoracionRepository.getValoracionMedia(id);
        long totalValoraciones = valoracionRepository.countByEmpresaId(id);
        response.setValoracionMedia(valoracionMedia != null ? Math.round(valoracionMedia * 10.0) / 10.0 : null);
        response.setTotalValoraciones((int) totalValoraciones);
        
        // Añadir plazas
        List<Plaza> plazas = plazaRepository.findByEmpresaId(id);
        response.setPlazas(plazas.stream()
                .map(PlazaResponse::fromEntity)
                .collect(Collectors.toList()));
        
        // Verificar si es favorita del usuario actual
        try {
            Usuario currentUser = usuarioService.getCurrentUser();
            response.setEsFavorita(favoritoRepository.existsByUsuarioIdAndEmpresaId(currentUser.getId(), id));
            
            // Obtener valoración del usuario
            valoracionRepository.findByEmpresaIdAndUsuarioId(id, currentUser.getId())
                    .ifPresent(v -> response.setMiValoracion(v.getPuntuacion()));
        } catch (Exception e) {
            response.setEsFavorita(false);
        }
        
        return response;
    }

    /**
     * Busca empresas por texto (nombre o ciudad).
     */
    public List<EmpresaResponse> search(String texto) {
        if (texto == null || texto.isBlank()) {
            return getAll();
        }
        return empresaRepository.buscarPorTexto(texto).stream()
                .map(this::toResponseWithValoracion)
                .collect(Collectors.toList());
    }

    /**
     * Busca empresas por ciudad.
     */
    public List<EmpresaResponse> getByCiudad(String ciudad) {
        return empresaRepository.findByCiudadIgnoreCaseAndActivaTrue(ciudad).stream()
                .map(this::toResponseWithValoracion)
                .collect(Collectors.toList());
    }

    /**
     * Busca empresas que aceptan un curso específico.
     */
    public List<EmpresaResponse> getByCurso(Long cursoId) {
        return empresaRepository.findByCursoId(cursoId).stream()
                .map(this::toResponseWithValoracion)
                .collect(Collectors.toList());
    }

    /**
     * Busca empresas contactadas por un departamento.
     */
    public List<EmpresaResponse> getContactadasByDepartamento(Long departamentoId) {
        return empresaRepository.findContactadasByDepartamento(departamentoId).stream()
                .map(this::toResponseWithValoracion)
                .collect(Collectors.toList());
    }

    /**
     * Crea una nueva empresa.
     */
    @Transactional
    public EmpresaResponse create(EmpresaRequest request) {
        // Verificar CIF único
        if (request.getCif() != null && !request.getCif().isBlank()) {
            if (empresaRepository.existsByCif(request.getCif())) {
                throw new BadRequestException("Ya existe una empresa con ese CIF");
            }
        }

        Usuario currentUser = usuarioService.getCurrentUser();

        Empresa empresa = new Empresa();
        mapRequestToEntity(request, empresa);
        empresa.setCreatedBy(currentUser);
        empresa = empresaRepository.save(empresa);

        // Asignar cursos
        if (request.getCursosIds() != null && !request.getCursosIds().isEmpty()) {
            assignCursos(empresa, request.getCursosIds());
        }

        return toResponseWithValoracion(empresa);
    }

    /**
     * Actualiza una empresa existente.
     */
    @Transactional
    public EmpresaResponse update(Long id, EmpresaRequest request) {
        Empresa empresa = empresaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Empresa", "id", id));

        // Verificar CIF único si cambió
        if (request.getCif() != null && !request.getCif().equals(empresa.getCif())) {
            if (empresaRepository.existsByCif(request.getCif())) {
                throw new BadRequestException("Ya existe una empresa con ese CIF");
            }
        }

        mapRequestToEntity(request, empresa);
        empresa = empresaRepository.save(empresa);

        // Actualizar cursos
        if (request.getCursosIds() != null) {
            empresaCursoRepository.deleteByEmpresaId(id);
            assignCursos(empresa, request.getCursosIds());
        }

        return toResponseWithValoracion(empresa);
    }

    /**
     * Elimina (desactiva) una empresa.
     */
    @Transactional
    public void delete(Long id) {
        Empresa empresa = empresaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Empresa", "id", id));
        empresa.setActiva(false);
        empresaRepository.save(empresa);
    }

    /**
     * Obtiene las empresas favoritas del usuario actual.
     */
    public List<EmpresaResponse> getFavoritas() {
        Usuario currentUser = usuarioService.getCurrentUser();
        return empresaRepository.findFavoritasByUsuario(currentUser.getId()).stream()
                .map(this::toResponseWithValoracion)
                .collect(Collectors.toList());
    }

    // Métodos privados auxiliares

    private void mapRequestToEntity(EmpresaRequest request, Empresa empresa) {
        empresa.setCif(request.getCif());
        empresa.setNombre(request.getNombre());
        empresa.setDireccion(request.getDireccion());
        empresa.setCiudad(request.getCiudad());
        empresa.setCodigoPostal(request.getCodigoPostal());
        empresa.setTelefono(request.getTelefono());
        empresa.setEmail(request.getEmail());
        empresa.setWeb(request.getWeb());
        empresa.setPersonaContacto(request.getPersonaContacto());
        empresa.setTelefonoContacto(request.getTelefonoContacto());
        empresa.setEmailContacto(request.getEmailContacto());
        empresa.setDescripcion(request.getDescripcion());
        if (request.getActiva() != null) {
            empresa.setActiva(request.getActiva());
        }
    }

    private void assignCursos(Empresa empresa, List<Long> cursosIds) {
        for (Long cursoId : cursosIds) {
            Curso curso = cursoRepository.findById(cursoId)
                    .orElseThrow(() -> new ResourceNotFoundException("Curso", "id", cursoId));
            EmpresaCurso empresaCurso = new EmpresaCurso(empresa, curso);
            empresaCursoRepository.save(empresaCurso);
        }
    }

    private EmpresaResponse toResponseWithValoracion(Empresa empresa) {
        Double valoracionMedia = valoracionRepository.getValoracionMedia(empresa.getId());
        long totalValoraciones = valoracionRepository.countByEmpresaId(empresa.getId());
        return EmpresaResponse.fromEntityWithDetails(
            empresa,
            valoracionMedia != null ? Math.round(valoracionMedia * 10.0) / 10.0 : null,
            (int) totalValoraciones
        );
    }

    /**
     * Obtiene la entidad Empresa por ID (para uso interno).
     */
    public Empresa getEntityById(Long id) {
        return empresaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Empresa", "id", id));
    }
}
