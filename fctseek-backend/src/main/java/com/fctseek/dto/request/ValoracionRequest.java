package com.fctseek.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * DTO para crear/actualizar una valoración.
 */
public class ValoracionRequest {

    @NotNull(message = "El ID de empresa es obligatorio")
    private Long empresaId;

    @NotNull(message = "La puntuación es obligatoria")
    @Min(value = 1, message = "La puntuación mínima es 1")
    @Max(value = 5, message = "La puntuación máxima es 5")
    private Integer puntuacion;

    // Constructores
    public ValoracionRequest() {
    }

    public ValoracionRequest(Long empresaId, Integer puntuacion) {
        this.empresaId = empresaId;
        this.puntuacion = puntuacion;
    }

    // Getters y Setters
    public Long getEmpresaId() {
        return empresaId;
    }

    public void setEmpresaId(Long empresaId) {
        this.empresaId = empresaId;
    }

    public Integer getPuntuacion() {
        return puntuacion;
    }

    public void setPuntuacion(Integer puntuacion) {
        this.puntuacion = puntuacion;
    }
}
