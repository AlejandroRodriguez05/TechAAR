package com.fctseek.dto.response;

import com.fctseek.model.Curso;

/**
 * DTO para la respuesta de datos de curso.
 */
public class CursoResponse {

    private Long id;
    private String codigo;
    private String siglas;
    private String nombre;
    private String grado;
    private Long departamentoId;
    private String departamentoNombre;

    // Constructores
    public CursoResponse() {
    }

    // Constructor desde entidad
    public static CursoResponse fromEntity(Curso curso) {
        CursoResponse response = new CursoResponse();
        response.setId(curso.getId());
        response.setCodigo(curso.getCodigo());
        response.setSiglas(curso.getSiglas());
        response.setNombre(curso.getNombre());
        response.setGrado(curso.getGrado());
        
        if (curso.getDepartamento() != null) {
            response.setDepartamentoId(curso.getDepartamento().getId());
            response.setDepartamentoNombre(curso.getDepartamento().getNombre());
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

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getSiglas() {
        return siglas;
    }

    public void setSiglas(String siglas) {
        this.siglas = siglas;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getGrado() {
        return grado;
    }

    public void setGrado(String grado) {
        this.grado = grado;
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
}
