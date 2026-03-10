package com.fctseek.dto.request;

import jakarta.validation.constraints.NotNull;

/**
 * DTO para marcar una empresa como contactada.
 */
public class EmpresaContactadaRequest {

    @NotNull(message = "El ID de empresa es obligatorio")
    private Long empresaId;

    @NotNull(message = "El ID de departamento es obligatorio")
    private Long departamentoId;

    private String notas;

    // Constructores
    public EmpresaContactadaRequest() {
    }

    // Getters y Setters
    public Long getEmpresaId() {
        return empresaId;
    }

    public void setEmpresaId(Long empresaId) {
        this.empresaId = empresaId;
    }

    public Long getDepartamentoId() {
        return departamentoId;
    }

    public void setDepartamentoId(Long departamentoId) {
        this.departamentoId = departamentoId;
    }

    public String getNotas() {
        return notas;
    }

    public void setNotas(String notas) {
        this.notas = notas;
    }
}
