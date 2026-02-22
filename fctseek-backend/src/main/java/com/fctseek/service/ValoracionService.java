package com.fctseek.service;

import com.fctseek.dto.request.ValoracionRequest;
import com.fctseek.dto.response.ValoracionResponse;
import com.fctseek.exception.ResourceNotFoundException;
import com.fctseek.model.Empresa;
import com.fctseek.model.Usuario;
import com.fctseek.model.Valoracion;
import com.fctseek.repository.EmpresaRepository;
import com.fctseek.repository.ValoracionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Servicio para gestión de valoraciones de empresas.
 */
@Service
public class ValoracionService {

    private final ValoracionRepository valoracionRepository;
    private final EmpresaRepository empresaRepository;
    private final UsuarioService usuarioService;

    public ValoracionService(ValoracionRepository valoracionRepository,
                            EmpresaRepository empresaRepository,
                            UsuarioService usuarioService) {
        this.valoracionRepository = valoracionRepository;
        this.empresaRepository = empresaRepository;
        this.usuarioService = usuarioService;
    }

    /**
     * Lista valoraciones de una empresa.
     */
    public List<ValoracionResponse> getByEmpresa(Long empresaId) {
        return valoracionRepository.findByEmpresaId(empresaId).stream()
                .map(ValoracionResponse::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene la valoración del usuario actual para una empresa.
     */
    public ValoracionResponse getMiValoracion(Long empresaId) {
        Usuario currentUser = usuarioService.getCurrentUser();
        Optional<Valoracion> valoracion = valoracionRepository.findByEmpresaIdAndUsuarioId(empresaId, currentUser.getId());
        return valoracion.map(ValoracionResponse::fromEntity).orElse(null);
    }

    /**
     * Obtiene la valoración media de una empresa.
     */
    public Double getValoracionMedia(Long empresaId) {
        Double media = valoracionRepository.getValoracionMedia(empresaId);
        return media != null ? Math.round(media * 10.0) / 10.0 : null;
    }

    /**
     * Cuenta las valoraciones de una empresa.
     */
    public long countByEmpresa(Long empresaId) {
        return valoracionRepository.countByEmpresaId(empresaId);
    }

    /**
     * Crea o actualiza una valoración.
     */
    @Transactional
    public ValoracionResponse valorar(ValoracionRequest request) {
        Usuario currentUser = usuarioService.getCurrentUser();
        
        Empresa empresa = empresaRepository.findById(request.getEmpresaId())
                .orElseThrow(() -> new ResourceNotFoundException("Empresa", "id", request.getEmpresaId()));

        // Buscar valoración existente
        Optional<Valoracion> existente = valoracionRepository.findByEmpresaIdAndUsuarioId(
                request.getEmpresaId(), currentUser.getId());

        Valoracion valoracion;
        if (existente.isPresent()) {
            // Actualizar valoración existente
            valoracion = existente.get();
            valoracion.setPuntuacion(request.getPuntuacion());
        } else {
            // Crear nueva valoración
            valoracion = new Valoracion();
            valoracion.setEmpresa(empresa);
            valoracion.setUsuario(currentUser);
            valoracion.setPuntuacion(request.getPuntuacion());
        }

        valoracion = valoracionRepository.save(valoracion);
        return ValoracionResponse.fromEntity(valoracion);
    }

    /**
     * Elimina la valoración del usuario actual para una empresa.
     */
    @Transactional
    public void deleteMyValoracion(Long empresaId) {
        Usuario currentUser = usuarioService.getCurrentUser();
        Valoracion valoracion = valoracionRepository.findByEmpresaIdAndUsuarioId(empresaId, currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("No has valorado esta empresa"));
        valoracionRepository.delete(valoracion);
    }
}
