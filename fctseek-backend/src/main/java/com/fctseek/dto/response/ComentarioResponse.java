package com.fctseek.dto.response;

import com.fctseek.model.Comentario;

/**
 * DTO para la respuesta de datos de comentario.
 */
public class ComentarioResponse {

    private Long id;
    private Long empresaId;
    private Long usuarioId;
    private String usuarioNombre;
    private String usuarioRol;
    private String texto;
    private Boolean esPrivado;
    private String fecha;

    // Constructores
    public ComentarioResponse() {
    }

    // Constructor desde entidad
    public static ComentarioResponse fromEntity(Comentario comentario) {
        ComentarioResponse response = new ComentarioResponse();
        response.setId(comentario.getId());
        response.setEmpresaId(comentario.getEmpresa().getId());
        response.setUsuarioId(comentario.getUsuario().getId());
        response.setUsuarioNombre(comentario.getUsuario().getNombreCompleto());
        response.setUsuarioRol(comentario.getUsuario().getRol());
        response.setTexto(comentario.getTexto());
        response.setEsPrivado(comentario.getEsPrivado());
        
        if (comentario.getCreatedAt() != null) {
            response.setFecha(comentario.getCreatedAt().toLocalDate().toString());
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

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    public Boolean getEsPrivado() {
        return esPrivado;
    }

    public void setEsPrivado(Boolean esPrivado) {
        this.esPrivado = esPrivado;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }
}
