package com.fctseek.service;

import com.fctseek.dto.request.ComentarioRequest;
import com.fctseek.dto.response.ComentarioResponse;
import com.fctseek.exception.ResourceNotFoundException;
import com.fctseek.exception.UnauthorizedException;
import com.fctseek.model.Comentario;
import com.fctseek.model.Empresa;
import com.fctseek.model.Usuario;
import com.fctseek.repository.ComentarioRepository;
import com.fctseek.repository.EmpresaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio para gestión de comentarios sobre empresas.
 */
@Service
public class ComentarioService {

    private final ComentarioRepository comentarioRepository;
    private final EmpresaRepository empresaRepository;
    private final UsuarioService usuarioService;

    public ComentarioService(ComentarioRepository comentarioRepository,
                            EmpresaRepository empresaRepository,
                            UsuarioService usuarioService) {
        this.comentarioRepository = comentarioRepository;
        this.empresaRepository = empresaRepository;
        this.usuarioService = usuarioService;
    }

    /**
     * Lista comentarios de una empresa.
     * Los alumnos solo ven comentarios públicos.
     * Los profesores ven todos.
     */
    public List<ComentarioResponse> getByEmpresa(Long empresaId) {
        Usuario currentUser = usuarioService.getCurrentUser();
        
        List<Comentario> comentarios;
        if (currentUser.esProfesor()) {
            comentarios = comentarioRepository.findAllComentariosByEmpresa(empresaId);
        } else {
            comentarios = comentarioRepository.findComentariosPublicosByEmpresa(empresaId);
        }
        
        return comentarios.stream()
                .map(ComentarioResponse::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene un comentario por ID.
     */
    public ComentarioResponse getById(Long id) {
        Comentario comentario = comentarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comentario", "id", id));
        
        // Verificar acceso a comentarios privados
        Usuario currentUser = usuarioService.getCurrentUser();
        if (comentario.getEsPrivado() && !currentUser.esProfesor()) {
            throw new UnauthorizedException("No tienes permiso para ver este comentario");
        }
        
        return ComentarioResponse.fromEntity(comentario);
    }

    /**
     * Crea un nuevo comentario.
     */
    @Transactional
    public ComentarioResponse create(ComentarioRequest request) {
        Usuario currentUser = usuarioService.getCurrentUser();
        
        // Solo profesores pueden crear comentarios privados
        if (request.getEsPrivado() != null && request.getEsPrivado() && !currentUser.esProfesor()) {
            throw new UnauthorizedException("Solo los profesores pueden crear comentarios privados");
        }

        Empresa empresa = empresaRepository.findById(request.getEmpresaId())
                .orElseThrow(() -> new ResourceNotFoundException("Empresa", "id", request.getEmpresaId()));

        Comentario comentario = new Comentario();
        comentario.setEmpresa(empresa);
        comentario.setUsuario(currentUser);
        comentario.setTexto(request.getTexto());
        comentario.setEsPrivado(request.getEsPrivado() != null ? request.getEsPrivado() : false);

        comentario = comentarioRepository.save(comentario);
        return ComentarioResponse.fromEntity(comentario);
    }

    /**
     * Actualiza un comentario.
     */
    @Transactional
    public ComentarioResponse update(Long id, String texto) {
        Comentario comentario = getComentarioForCurrentUser(id);
        comentario.setTexto(texto);
        comentario = comentarioRepository.save(comentario);
        return ComentarioResponse.fromEntity(comentario);
    }

    /**
     * Elimina un comentario.
     */
    @Transactional
    public void delete(Long id) {
        Comentario comentario = getComentarioForCurrentUser(id);
        comentarioRepository.delete(comentario);
    }

    // Método auxiliar
    private Comentario getComentarioForCurrentUser(Long id) {
        Usuario currentUser = usuarioService.getCurrentUser();
        Comentario comentario = comentarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comentario", "id", id));
        
        if (!comentario.getUsuario().getId().equals(currentUser.getId())) {
            throw new UnauthorizedException("No tienes permiso para modificar este comentario");
        }
        
        return comentario;
    }
}
