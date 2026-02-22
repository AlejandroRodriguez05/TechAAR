package com.fctseek.dto.request;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * DTO para registrar un contacto con una empresa.
 */
public class ContactoEmpresaRequest {

    @NotNull(message = "El ID de empresa es obligatorio")
    private Long empresaId;

    @NotNull(message = "El ID de departamento es obligatorio")
    private Long departamentoId;

    private LocalDate fecha;

    private String notas;

    // Constructores
    public ContactoEmpresaRequest() {
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

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public String getNotas() {
        return notas;
    }

    public void setNotas(String notas) {
        this.notas = notas;
    }
}
