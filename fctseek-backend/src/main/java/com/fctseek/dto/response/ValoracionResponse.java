package com.fctseek.dto.response;

import com.fctseek.model.Valoracion;

/**
 * DTO para la respuesta de datos de valoración.
 */
public class ValoracionResponse {

    private Long id;
    private Long empresaId;
    private Long usuarioId;
    private String usuarioNombre;
    private String usuarioRol;
    private Integer puntuacion;
    private String fecha;

    // Constructores
    public ValoracionResponse() {
    }

    // Constructor desde entidad
    public static ValoracionResponse fromEntity(Valoracion valoracion) {
        ValoracionResponse response = new ValoracionResponse();
        response.setId(valoracion.getId());
        response.setEmpresaId(valoracion.getEmpresa().getId());
        response.setUsuarioId(valoracion.getUsuario().getId());
        response.setUsuarioNombre(valoracion.getUsuario().getNombreCompleto());
        response.setUsuarioRol(valoracion.getUsuario().getRol());
        response.setPuntuacion(valoracion.getPuntuacion());
        
        if (valoracion.getCreatedAt() != null) {
            response.setFecha(valoracion.getCreatedAt().toLocalDate().toString());
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

    public Long getEmpresaId() {
        return empresaId;
    }

    public void setEmpresaId(Long empresaId) {
        this.empresaId = empresaId;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public String getUsuarioNombre() {
        return usuarioNombre;
    }

    public void setUsuarioNombre(String usuarioNombre) {
        this.usuarioNombre = usuarioNombre;
    }

    public String getUsuarioRol() {
        return usuarioRol;
    }

    public void setUsuarioRol(String usuarioRol) {
        this.usuarioRol = usuarioRol;
    }

    public Integer getPuntuacion() {
        return puntuacion;
    }

    public void setPuntuacion(Integer puntuacion) {
        this.puntuacion = puntuacion;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }
}
