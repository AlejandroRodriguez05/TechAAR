package com.practicas.model;

public class Plaza {
    private long id;
    private long empresaId;
    private String empresaNombre;
    private long departamentoId;
    private String departamentoNombre;
    private Long cursoId;
    private String cursoSiglas;
    private String cursoNombre;
    private int cantidad;
    private boolean esGeneral;
    private String cursoAcademico;
    private int plazasReservadas;
    private int plazasDisponibles;
    private Long creadorId;

    public long getId() { return id; }
    public void setId(long v) { this.id = v; }
    public long getEmpresaId() { return empresaId; }
    public void setEmpresaId(long v) { this.empresaId = v; }
    public String getEmpresaNombre() { return empresaNombre; }
    public void setEmpresaNombre(String v) { this.empresaNombre = v; }
    public long getDepartamentoId() { return departamentoId; }
    public void setDepartamentoId(long v) { this.departamentoId = v; }
    public String getDepartamentoNombre() { return departamentoNombre; }
    public void setDepartamentoNombre(String v) { this.departamentoNombre = v; }
    public Long getCursoId() { return cursoId; }
    public void setCursoId(Long v) { this.cursoId = v; }
    public String getCursoSiglas() { return cursoSiglas; }
    public void setCursoSiglas(String v) { this.cursoSiglas = v; }
    public String getCursoNombre() { return cursoNombre; }
    public void setCursoNombre(String v) { this.cursoNombre = v; }
    public int getCantidad() { return cantidad; }
    public void setCantidad(int v) { this.cantidad = v; }
    public boolean isEsGeneral() { return esGeneral; }
    public void setEsGeneral(boolean v) { this.esGeneral = v; }
    public String getCursoAcademico() { return cursoAcademico; }
    public void setCursoAcademico(String v) { this.cursoAcademico = v; }
    public int getPlazasReservadas() { return plazasReservadas; }
    public void setPlazasReservadas(int v) { this.plazasReservadas = v; }
    public int getPlazasDisponibles() { return plazasDisponibles; }
    public void setPlazasDisponibles(int v) { this.plazasDisponibles = v; }
    public Long getCreadorId() { return creadorId; }
    public void setCreadorId(Long v) { this.creadorId = v; }
}