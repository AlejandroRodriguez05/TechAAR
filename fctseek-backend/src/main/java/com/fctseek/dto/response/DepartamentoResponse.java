package com.fctseek.dto.response;

import com.fctseek.model.Departamento;

/**
 * DTO para la respuesta de datos de departamento.
 */
public class DepartamentoResponse {

    private Long id;
    private String codigo;
    private String nombre;

    // Constructores
    public DepartamentoResponse() {
    }

    public DepartamentoResponse(Long id, String codigo, String nombre) {
        this.id = id;
        this.codigo = codigo;
        this.nombre = nombre;
    }

    // Constructor desde entidad
    public static DepartamentoResponse fromEntity(Departamento departamento) {
        return new DepartamentoResponse(
            departamento.getId(),
            departamento.getCodigo(),
            departamento.getNombre()
        );
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}
