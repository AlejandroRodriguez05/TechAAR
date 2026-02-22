package com.fctseek.service;

import com.fctseek.dto.request.ListaRequest;
import com.fctseek.dto.response.ListaResponse;
import com.fctseek.exception.BadRequestException;
import com.fctseek.exception.ResourceNotFoundException;
import com.fctseek.exception.UnauthorizedException;
import com.fctseek.model.Empresa;
import com.fctseek.model.Lista;
import com.fctseek.model.ListaEmpresa;
import com.fctseek.model.Usuario;
import com.fctseek.repository.EmpresaRepository;
import com.fctseek.repository.ListaEmpresaRepository;
import com.fctseek.repository.ListaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio para gestión de listas personalizadas de empresas.
 */
@Service
public class ListaService {

    private final ListaRepository listaRepository;
    private final ListaEmpresaRepository listaEmpresaRepository;
    private final EmpresaRepository empresaRepository;
    private final UsuarioService usuarioService;

    public ListaService(ListaRepository listaRepository,
                       ListaEmpresaRepository listaEmpresaRepository,
                       EmpresaRepository empresaRepository,
                       UsuarioService usuarioService) {
        this.listaRepository = listaRepository;
        this.listaEmpresaRepository = listaEmpresaRepository;
        this.empresaRepository = empresaRepository;
        this.usuarioService = usuarioService;
    }

    /**
     * Lista todas las listas del usuario actual.
     */
    public List<ListaResponse> getMisListas() {
        Usuario currentUser = usuarioService.getCurrentUser();
        return listaRepository.findByUsuarioIdOrderByCreatedAtDesc(currentUser.getId()).stream()
                .map(ListaResponse::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene una lista con sus empresas.
     */
    public ListaResponse getById(Long id) {
        Lista lista = getListaForCurrentUser(id);
        return ListaResponse.fromEntityWithEmpresas(lista);
    }

    /**
     * Crea una nueva lista.
     */
    @Transactional
    public ListaResponse create(ListaRequest request) {
        Usuario currentUser = usuarioService.getCurrentUser();

        // Verificar nombre único para el usuario
        if (listaRepository.existsByUsuarioIdAndNombre(currentUser.getId(), request.getNombre())) {
            throw new BadRequestException("Ya tienes una lista con ese nombre");
        }

        Lista lista = new Lista();
        lista.setUsuario(currentUser);
        lista.setNombre(request.getNombre());
        lista.setDescripcion(request.getDescripcion());
        lista.setEsFavoritos(false);

        lista = listaRepository.save(lista);
        return ListaResponse.fromEntity(lista);
    }

    /**
     * Actualiza una lista.
     */
    @Transactional
    public ListaResponse update(Long id, ListaRequest request) {
        Lista lista = getListaForCurrentUser(id);

        // No permitir editar la lista de favoritos
        if (lista.getEsFavoritos()) {
            throw new BadRequestException("No se puede editar la lista de favoritos");
        }

        // Verificar nombre único si cambió
        if (!lista.getNombre().equals(request.getNombre())) {
            Usuario currentUser = usuarioService.getCurrentUser();
            if (listaRepository.existsByUsuarioIdAndNombre(currentUser.getId(), request.getNombre())) {
                throw new BadRequestException("Ya tienes una lista con ese nombre");
            }
        }

        lista.setNombre(request.getNombre());
        lista.setDescripcion(request.getDescripcion());

        lista = listaRepository.save(lista);
        return ListaResponse.fromEntity(lista);
    }

    /**
     * Elimina una lista.
     */
    @Transactional
    public void delete(Long id) {
        Lista lista = getListaForCurrentUser(id);

        // No permitir eliminar la lista de favoritos
        if (lista.getEsFavoritos()) {
            throw new BadRequestException("No se puede eliminar la lista de favoritos");
        }

        listaRepository.delete(lista);
    }

    /**
     * Añade una empresa a una lista.
     */
    @Transactional
    public void addEmpresa(Long listaId, Long empresaId, String notas) {
        Lista lista = getListaForCurrentUser(listaId);
        
        // Verificar que no esté ya en la lista
        if (listaEmpresaRepository.existsByListaIdAndEmpresaId(listaId, empresaId)) {
            throw new BadRequestException("La empresa ya está en esta lista");
        }

        Empresa empresa = empresaRepository.findById(empresaId)
                .orElseThrow(() -> new ResourceNotFoundException("Empresa", "id", empresaId));

        ListaEmpresa listaEmpresa = new ListaEmpresa(lista, empresa, notas);
        listaEmpresaRepository.save(listaEmpresa);
    }

    /**
     * Elimina una empresa de una lista.
     */
    @Transactional
    public void removeEmpresa(Long listaId, Long empresaId) {
        Lista lista = getListaForCurrentUser(listaId);
        
        if (!listaEmpresaRepository.existsByListaIdAndEmpresaId(listaId, empresaId)) {
            throw new ResourceNotFoundException("La empresa no está en esta lista");
        }

        listaEmpresaRepository.deleteByListaIdAndEmpresaId(listaId, empresaId);
    }

    /**
     * Actualiza las notas de una empresa en una lista.
     */
    @Transactional
    public void updateNotasEmpresa(Long listaId, Long empresaId, String notas) {
        getListaForCurrentUser(listaId);
        
        ListaEmpresa listaEmpresa = listaEmpresaRepository.findByListaIdAndEmpresaId(listaId, empresaId)
                .orElseThrow(() -> new ResourceNotFoundException("La empresa no está en esta lista"));

        listaEmpresa.setNotas(notas);
        listaEmpresaRepository.save(listaEmpresa);
    }

    /**
     * Obtiene las listas que contienen una empresa.
     */
    public List<ListaResponse> getListasConEmpresa(Long empresaId) {
        Usuario currentUser = usuarioService.getCurrentUser();
        return listaRepository.findListasConEmpresa(currentUser.getId(), empresaId).stream()
                .map(ListaResponse::fromEntity)
                .collect(Collectors.toList());
    }

    // Método auxiliar para verificar propiedad de la lista
    private Lista getListaForCurrentUser(Long id) {
        Usuario currentUser = usuarioService.getCurrentUser();
        Lista lista = listaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lista", "id", id));
        
        if (!lista.getUsuario().getId().equals(currentUser.getId())) {
            throw new UnauthorizedException("No tienes permiso para acceder a esta lista");
        }
        
        return lista;
    }
}
