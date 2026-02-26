package com.fctseek.service;

import com.fctseek.dto.response.EmpresaResponse;
import com.fctseek.exception.BadRequestException;
import com.fctseek.exception.ResourceNotFoundException;
import com.fctseek.model.Empresa;
import com.fctseek.model.Favorito;
import com.fctseek.model.Usuario;
import com.fctseek.repository.EmpresaRepository;
import com.fctseek.repository.FavoritoRepository;
import com.fctseek.repository.ValoracionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio para gestión de empresas favoritas.
 */
@Service
public class FavoritoService {

    private final FavoritoRepository favoritoRepository;
    private final EmpresaRepository empresaRepository;
    private final ValoracionRepository valoracionRepository;
    private final UsuarioService usuarioService;

    public FavoritoService(FavoritoRepository favoritoRepository,
                          EmpresaRepository empresaRepository,
                          ValoracionRepository valoracionRepository,
                          UsuarioService usuarioService) {
        this.favoritoRepository = favoritoRepository;
        this.empresaRepository = empresaRepository;
        this.valoracionRepository = valoracionRepository;
        this.usuarioService = usuarioService;
    }

    /**
     * Lista las empresas favoritas del usuario actual.
     */
    public List<EmpresaResponse> getMisFavoritos() {
        Usuario currentUser = usuarioService.getCurrentUser();
        List<Favorito> favoritos = favoritoRepository.findByUsuarioIdOrderByCreatedAtDesc(currentUser.getId());
        
        return favoritos.stream()
                .map(f -> {
                    Empresa empresa = f.getEmpresa();
                    Double valoracionMedia = valoracionRepository.getValoracionMedia(empresa.getId());
                    long totalValoraciones = valoracionRepository.countByEmpresaId(empresa.getId());
                    return EmpresaResponse.fromEntityWithDetails(
                        empresa,
                        valoracionMedia != null ? Math.round(valoracionMedia * 10.0) / 10.0 : null,
                        (int) totalValoraciones
                    );
                })
                .collect(Collectors.toList());
    }

    /**
     * Verifica si una empresa es favorita del usuario actual.
     */
    public boolean isFavorita(Long empresaId) {
        Usuario currentUser = usuarioService.getCurrentUser();
        return favoritoRepository.existsByUsuarioIdAndEmpresaId(currentUser.getId(), empresaId);
    }

    /**
     * Añade una empresa a favoritos.
     */
    @Transactional
    public void addFavorito(Long empresaId) {
        Usuario currentUser = usuarioService.getCurrentUser();
        
        // Verificar que no sea ya favorita
        if (favoritoRepository.existsByUsuarioIdAndEmpresaId(currentUser.getId(), empresaId)) {
            throw new BadRequestException("La empresa ya está en favoritos");
        }

        Empresa empresa = empresaRepository.findById(empresaId)
                .orElseThrow(() -> new ResourceNotFoundException("Empresa", "id", empresaId));

        Favorito favorito = new Favorito(currentUser, empresa);
        favoritoRepository.save(favorito);
    }

    /**
     * Elimina una empresa de favoritos.
     */
    @Transactional
    public void removeFavorito(Long empresaId) {
        Usuario currentUser = usuarioService.getCurrentUser();
        
        if (!favoritoRepository.existsByUsuarioIdAndEmpresaId(currentUser.getId(), empresaId)) {
            throw new ResourceNotFoundException("La empresa no está en favoritos");
        }

        favoritoRepository.deleteByUsuarioIdAndEmpresaId(currentUser.getId(), empresaId);
    }

    /**
     * Alterna el estado de favorito de una empresa.
     */
    @Transactional
    public boolean toggleFavorito(Long empresaId) {
        Usuario currentUser = usuarioService.getCurrentUser();
        
        if (favoritoRepository.existsByUsuarioIdAndEmpresaId(currentUser.getId(), empresaId)) {
            favoritoRepository.deleteByUsuarioIdAndEmpresaId(currentUser.getId(), empresaId);
            return false; // Ya no es favorita
        } else {
            Empresa empresa = empresaRepository.findById(empresaId)
                    .orElseThrow(() -> new ResourceNotFoundException("Empresa", "id", empresaId));
            Favorito favorito = new Favorito(currentUser, empresa);
            favoritoRepository.save(favorito);
            return true; // Ahora es favorita
        }
    }

    /**
     * Cuenta las empresas favoritas del usuario actual.
     */
    public long countMisFavoritos() {
        Usuario currentUser = usuarioService.getCurrentUser();
        return favoritoRepository.countByUsuarioId(currentUser.getId());
    }
}
