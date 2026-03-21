package com.practicas.model;

public class Reserva {
    private long id;
    private long plazaId;
    private long empresaId;
    private long departamentoId;
    private String departamentoNombre;
    private long profesorId;
    private String profesorNombre;
    private Long cursoId;
    private String cursoSiglas;
    private int cantidad;
    private String clase;
    private String estado;
    private String cursoAcademico;
    private String createdAt;

    public long getId() { return id; }
    public void setId(long v) { this.id = v; }
    public long getPlazaId() { return plazaId; }
    public void setPlazaId(long v) { this.plazaId = v; }
    public long getEmpresaId() { return empresaId; }
    public void setEmpresaId(long v) { this.empresaId = v; }
    public long getDepartamentoId() { return departamentoId; }
    public void setDepartamentoId(long v) { this.departamentoId = v; }
    public String getDepartamentoNombre() { return departamentoNombre; }
    public void setDepartamentoNombre(String v) { this.departamentoNombre = v; }
    public long getProfesorId() { return profesorId; }
    public void setProfesorId(long v) { this.profesorId = v; }
    public String getProfesorNombre() { return profesorNombre; }
    public void setProfesorNombre(String v) { this.profesorNombre = v; }
    public Long getCursoId() { return cursoId; }
    public void setCursoId(Long v) { this.cursoId = v; }
    public String getCursoSiglas() { return cursoSiglas; }
    public void setCursoSiglas(String v) { this.cursoSiglas = v; }
    public int getCantidad() { return cantidad; }
    public void setCantidad(int v) { this.cantidad = v; }
    public String getClase() { return clase; }
    public void setClase(String v) { this.clase = v; }
    public String getEstado() { return estado; }
    public void setEstado(String v) { this.estado = v; }
    public String getCursoAcademico() { return cursoAcademico; }
    public void setCursoAcademico(String v) { this.cursoAcademico = v; }
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String v) { this.createdAt = v; }
}