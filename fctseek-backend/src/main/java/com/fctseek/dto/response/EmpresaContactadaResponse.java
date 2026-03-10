package com.fctseek.dto.response;

import com.fctseek.model.EmpresaContactada;

import java.time.LocalDate;

/**
 * DTO para la respuesta de empresa contactada.
 */
public class EmpresaContactadaResponse {

    private Long id;
    private Long empresaId;
    private String empresaNombre;
    private Long departamentoId;
    private String departamentoNombre;
    private Long profesorId;
    private String profesorNombre;
    private LocalDate fecha;
    private String notas;

    // Constructor
    public EmpresaContactadaResponse() {
    }

    public static EmpresaContactadaResponse fromEntity(EmpresaContactada ec) {
        EmpresaContactadaResponse r = new EmpresaContactadaResponse();
        r.setId(ec.getId());
        r.setEmpresaId(ec.getEmpresa().getId());
        r.setEmpresaNombre(ec.getEmpresa().getNombre());
        r.setDepartamentoId(ec.getDepartamento().getId());
        r.setDepartamentoNombre(ec.getDepartamento().getNombre());
        r.setProfesorId(ec.getProfesor().getId());
        r.setProfesorNombre(ec.getProfesor().getNombre() + " " + ec.getProfesor().getApellidos());
        r.setFecha(ec.getFecha());
        r.setNotas(ec.getNotas());
        return r;
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getEmpresaId() { return empresaId; }
    public void setEmpresaId(Long empresaId) { this.empresaId = empresaId; }

    public String getEmpresaNombre() { return empresaNombre; }
    public void setEmpresaNombre(String empresaNombre) { this.empresaNombre = empresaNombre; }

    public Long getDepartamentoId() { return departamentoId; }
    public void setDepartamentoId(Long departamentoId) { this.departamentoId = departamentoId; }

    public String getDepartamentoNombre() { return departamentoNombre; }
    public void setDepartamentoNombre(String departamentoNombre) { this.departamentoNombre = departamentoNombre; }

    public Long getProfesorId() { return profesorId; }
    public void setProfesorId(Long profesorId) { this.profesorId = profesorId; }

    public String getProfesorNombre() { return profesorNombre; }
    public void setProfesorNombre(String profesorNombre) { this.profesorNombre = profesorNombre; }

    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }

    public String getNotas() { return notas; }
    public void setNotas(String notas) { this.notas = notas; }
}
