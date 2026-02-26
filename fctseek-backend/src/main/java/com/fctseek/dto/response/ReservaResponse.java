package com.fctseek.dto.response;

import com.fctseek.model.Reserva;

/**
 * DTO para la respuesta de datos de reserva.
 */
public class ReservaResponse {

    private Long id;
    private Long plazaId;
    private Long empresaId;
    private String empresaNombre;
    private Long departamentoId;
    private String departamentoNombre;
    private Long profesorId;
    private String profesorNombre;
    private Long cursoId;
    private String cursoSiglas;
    private Integer cantidad;
    private String clase;
    private String estado;
    private String notas;
    private String cursoAcademico;
    private String createdAt;

    // Constructores
    public ReservaResponse() {
    }

    // Constructor desde entidad
    public static ReservaResponse fromEntity(Reserva reserva) {
        ReservaResponse response = new ReservaResponse();
        response.setId(reserva.getId());
        response.setPlazaId(reserva.getPlaza().getId());
        response.setEmpresaId(reserva.getPlaza().getEmpresa().getId());
        response.setEmpresaNombre(reserva.getPlaza().getEmpresa().getNombre());
        response.setDepartamentoId(reserva.getPlaza().getDepartamento().getId());
        response.setDepartamentoNombre(reserva.getPlaza().getDepartamento().getNombre());
        response.setProfesorId(reserva.getProfesor().getId());
        response.setProfesorNombre(reserva.getProfesor().getNombreCompleto());
        response.setCursoId(reserva.getCurso().getId());
        response.setCursoSiglas(reserva.getCurso().getSiglas());
        response.setCantidad(reserva.getCantidad());
        response.setClase(reserva.getClase());
        response.setEstado(reserva.getEstado());
        response.setNotas(reserva.getNotas());
        response.setCursoAcademico(reserva.getPlaza().getCursoAcademico());
        
        if (reserva.getCreatedAt() != null) {
            response.setCreatedAt(reserva.getCreatedAt().toString());
        }
        
        return response;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPlazaId() {
        return plazaId;
    }

    public void setPlazaId(Long plazaId) {
        this.plazaId = plazaId;
    }

    public Long getEmpresaId() {
        return empresaId;
    }

    public void setEmpresaId(Long empresaId) {
        this.empresaId = empresaId;
    }

    public String getEmpresaNombre() {
        return empresaNombre;
    }

    public void setEmpresaNombre(String empresaNombre) {
        this.empresaNombre = empresaNombre;
    }

    public Long getDepartamentoId() {
        return departamentoId;
    }

    public void setDepartamentoId(Long departamentoId) {
        this.departamentoId = departamentoId;
    }

    public String getDepartamentoNombre() {
        return departamentoNombre;
    }

    public void setDepartamentoNombre(String departamentoNombre) {
        this.departamentoNombre = departamentoNombre;
    }

    public Long getProfesorId() {
        return profesorId;
    }

    public void setProfesorId(Long profesorId) {
        this.profesorId = profesorId;
    }

    public String getProfesorNombre() {
        return profesorNombre;
    }

    public void setProfesorNombre(String profesorNombre) {
        this.profesorNombre = profesorNombre;
    }

    public Long getCursoId() {
        return cursoId;
    }

    public void setCursoId(Long cursoId) {
        this.cursoId = cursoId;
    }

    public String getCursoSiglas() {
        return cursoSiglas;
    }

    public void setCursoSiglas(String cursoSiglas) {
        this.cursoSiglas = cursoSiglas;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public String getClase() {
        return clase;
    }

    public void setClase(String clase) {
        this.clase = clase;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getNotas() {
        return notas;
    }

    public void setNotas(String notas) {
        this.notas = notas;
    }

    public String getCursoAcademico() {
        return cursoAcademico;
    }

    public void setCursoAcademico(String cursoAcademico) {
        this.cursoAcademico = cursoAcademico;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
