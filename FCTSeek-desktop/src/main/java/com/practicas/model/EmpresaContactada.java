package com.practicas.model;

public class EmpresaContactada {
    private long id;
    private String profesorNombre;
    private String departamentoNombre;
    private String fecha;
    private String notas;

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public String getProfesorNombre() { return profesorNombre; }
    public void setProfesorNombre(String p) { this.profesorNombre = p; }
    public String getDepartamentoNombre() { return departamentoNombre; }
    public void setDepartamentoNombre(String d) { this.departamentoNombre = d; }
    public String getFecha() { return fecha; }
    public void setFecha(String f) { this.fecha = f; }
    public String getNotas() { return notas; }
    public void setNotas(String n) { this.notas = n; }
}
