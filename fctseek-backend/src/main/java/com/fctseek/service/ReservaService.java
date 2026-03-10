package com.fctseek.service;

import com.fctseek.dto.request.ReservaRequest;
import com.fctseek.dto.response.ReservaResponse;
import com.fctseek.exception.BadRequestException;
import com.fctseek.exception.ResourceNotFoundException;
import com.fctseek.exception.UnauthorizedException;
import com.fctseek.model.*;
import com.fctseek.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio para gestión de reservas de plazas.
 */
@Service
public class ReservaService {

    private final ReservaRepository reservaRepository;
    private final PlazaRepository plazaRepository;
    private final CursoRepository cursoRepository;
    private final UsuarioService usuarioService;

    public ReservaService(ReservaRepository reservaRepository,
                         PlazaRepository plazaRepository,
                         CursoRepository cursoRepository,
                         UsuarioService usuarioService) {
        this.reservaRepository = reservaRepository;
        this.plazaRepository = plazaRepository;
        this.cursoRepository = cursoRepository;
        this.usuarioService = usuarioService;
    }

    /**
     * Lista reservas del profesor actual.
     */
    public List<ReservaResponse> getMisReservas() {
        Usuario currentUser = usuarioService.getCurrentUser();
        return reservaRepository.findByProfesorId(currentUser.getId()).stream()
                .map(ReservaResponse::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Lista reservas de una empresa.
     */
    public List<ReservaResponse> getByEmpresa(Long empresaId) {
        return reservaRepository.findByEmpresaId(empresaId).stream()
                .map(ReservaResponse::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Lista reservas de una plaza.
     */
    public List<ReservaResponse> getByPlaza(Long plazaId) {
        return reservaRepository.findByPlazaId(plazaId).stream()
                .map(ReservaResponse::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene una reserva por ID.
     */
    public ReservaResponse getById(Long id) {
        Reserva reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reserva", "id", id));
        return ReservaResponse.fromEntity(reserva);
    }

    /**
     * Crea una nueva reserva.
     */
    @Transactional
    public ReservaResponse create(ReservaRequest request) {
        Usuario currentUser = usuarioService.getCurrentUser();
        
        // Solo profesores pueden reservar
        if (!currentUser.esProfesor()) {
            throw new UnauthorizedException("Solo los profesores pueden realizar reservas");
        }

        // Validar plaza
        Plaza plaza = plazaRepository.findById(request.getPlazaId())
                .orElseThrow(() -> new ResourceNotFoundException("Plaza", "id", request.getPlazaId()));

        // Validar curso
        Curso curso = cursoRepository.findById(request.getCursoId())
                .orElseThrow(() -> new ResourceNotFoundException("Curso", "id", request.getCursoId()));

        // Verificar que el ciclo coincida si la plaza es específica (no general)
        if (!plaza.getEsGeneral() && plaza.getCurso() != null
                && !plaza.getCurso().getId().equals(curso.getId())) {
            throw new BadRequestException(
                "Esta plaza solo admite reservas para el ciclo: " + plaza.getCurso().getSiglas()
            );
        }

        // Verificar disponibilidad
        int disponibles = plaza.getPlazasDisponibles();
        if (request.getCantidad() > disponibles) {
            throw new BadRequestException("No hay suficientes plazas disponibles. Disponibles: " + disponibles);
        }

        // Crear reserva
        Reserva reserva = new Reserva();
        reserva.setPlaza(plaza);
        reserva.setProfesor(currentUser);
        reserva.setCurso(curso);
        reserva.setCantidad(request.getCantidad());
        reserva.setClase(request.getClase());
        reserva.setNotas(request.getNotas());
        reserva.setEstado("PENDIENTE");

        reserva = reservaRepository.save(reserva);
        return ReservaResponse.fromEntity(reserva);
    }

    /**
     * Confirma una reserva.
     */
    @Transactional
    public ReservaResponse confirmar(Long id) {
        Reserva reserva = getReservaForCurrentUser(id);
        
        if (!"PENDIENTE".equals(reserva.getEstado())) {
            throw new BadRequestException("Solo se pueden confirmar reservas pendientes");
        }
        
        reserva.setEstado("CONFIRMADA");
        reserva = reservaRepository.save(reserva);
        return ReservaResponse.fromEntity(reserva);
    }

    /**
     * Cancela una reserva.
     */
    @Transactional
    public ReservaResponse cancelar(Long id) {
        Reserva reserva = getReservaForCurrentUser(id);
        
        if ("CANCELADA".equals(reserva.getEstado())) {
            throw new BadRequestException("La reserva ya está cancelada");
        }
        
        reserva.setEstado("CANCELADA");
        reserva = reservaRepository.save(reserva);
        return ReservaResponse.fromEntity(reserva);
    }

    /**
     * Actualiza las notas de una reserva.
     */
    @Transactional
    public ReservaResponse updateNotas(Long id, String notas) {
        Reserva reserva = getReservaForCurrentUser(id);
        reserva.setNotas(notas);
        reserva = reservaRepository.save(reserva);
        return ReservaResponse.fromEntity(reserva);
    }

    /**
     * Elimina una reserva.
     */
    @Transactional
    public void delete(Long id) {
        Reserva reserva = getReservaForCurrentUser(id);
        reservaRepository.delete(reserva);
    }

    // Método auxiliar para verificar propiedad de la reserva
    private Reserva getReservaForCurrentUser(Long id) {
        Usuario currentUser = usuarioService.getCurrentUser();
        Reserva reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reserva", "id", id));
        
        // Verificar que la reserva pertenece al usuario actual
        if (!reserva.getProfesor().getId().equals(currentUser.getId())) {
            throw new UnauthorizedException("No tienes permiso para modificar esta reserva");
        }
        
        return reserva;
    }
}
