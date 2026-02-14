package com.fctseek.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * DTO para crear/actualizar una plaza.
 */
public class PlazaRequest {

    @NotNull(message = "El ID de empresa es obligatorio")
    private Long empresaId;

    @NotNull(message = "El ID de departamento es obligatorio")
    private Long departamentoId;

    // Null si es plaza general
    private Long cursoId;

    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1, message = "La cantidad mínima es 1")
    private Integer cantidad;

    @NotBlank(message = "El curso académico es obligatorio")
    private String cursoAcademico;

    // Constructores
    public PlazaRequest() {
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

    public Long getCursoId() {
        return cursoId;
    }

    public void setCursoId(Long cursoId) {
        this.cursoId = cursoId;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public String getCursoAcademico() {
        return cursoAcademico;
    }

    public void setCursoAcademico(String cursoAcademico) {
        this.cursoAcademico = cursoAcademico;
    }

    // Helper
    public boolean isGeneral() {
        return cursoId == null;
    }
}
