package com.practicas.model;

public class EmpresaContactada {
    private long id;
    private long profesorId;
    private String profesorNombre;
    private String departamentoNombre;
    private String fecha;
    private String notas;

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public long getProfesorId() { return profesorId; }
    public void setProfesorId(long v) { this.profesorId = v; }
    public String getProfesorNombre() { return profesorNombre; }
    public void setProfesorNombre(String p) { this.profesorNombre = p; }
    public String getDepartamentoNombre() { return departamentoNombre; }
    public void setDepartamentoNombre(String d) { this.departamentoNombre = d; }
    public String getFecha() { return fecha; }
    public void setFecha(String f) { this.fecha = f; }
    public String getNotas() { return notas; }
    public void setNotas(String n) { this.notas = n; }
}