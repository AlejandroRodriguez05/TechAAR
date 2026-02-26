package com.fctseek.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * DTO para crear un comentario.
 */
public class ComentarioRequest {

    @NotNull(message = "El ID de empresa es obligatorio")
    private Long empresaId;

    @NotBlank(message = "El texto del comentario es obligatorio")
    private String texto;

    private Boolean esPrivado = false;

    // Constructores
    public ComentarioRequest() {
    }

    public ComentarioRequest(Long empresaId, String texto, Boolean esPrivado) {
        this.empresaId = empresaId;
        this.texto = texto;
        this.esPrivado = esPrivado;
    }

    // Getters y Setters
    public Long getEmpresaId() {
        return empresaId;
    }

    public void setEmpresaId(Long empresaId) {
        this.empresaId = empresaId;
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
}
